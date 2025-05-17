package be.helha.projects.GuerreDesRoyaumes.DAOImpl;

import be.helha.projects.GuerreDesRoyaumes.Config.InitialiserAPP;
import be.helha.projects.GuerreDesRoyaumes.DAO.CoffreMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.ItemMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.MongoDBConnectionException;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Coffre;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Slot;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Arme;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Bouclier;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation de l'interface CoffreMongoDAO pour la gestion des coffres dans MongoDB.
 * Cette classe gère les opérations CRUD pour les documents Coffre.
 */
@Repository
public class CoffreMongoDAOImpl implements CoffreMongoDAO {

    private static CoffreMongoDAOImpl instance;
    private final MongoCollection<Document> collection;
    private final ItemMongoDAO itemMongoDAO;

    /**
     * Constructeur privé pour le singleton qui initialise la connexion à la collection MongoDB.
     */
    private CoffreMongoDAOImpl() {
        MongoDatabase mongoDB;
        try {
            mongoDB = InitialiserAPP.getMongoConnexion();
        } catch (MongoDBConnectionException ex) {
            throw new RuntimeException(ex);
        }
        this.collection = mongoDB.getCollection("coffres");
        this.itemMongoDAO = ItemMongoDAOImpl.getInstance();
    }

    /**
     * Obtient l'instance unique de CoffreMongoDAOImpl (pattern Singleton).
     *
     * @return L'instance unique de CoffreMongoDAOImpl
     */
    public static synchronized CoffreMongoDAOImpl getInstance() throws MongoDBConnectionException {
        if (instance == null) {
            instance = new CoffreMongoDAOImpl();
        }
        return instance;
    }

    /**
     * Sauvegarde le coffre d'un joueur dans MongoDB.
     *
     * @param joueur Le joueur propriétaire du coffre
     * @param coffre Le coffre à sauvegarder
     * @return true si la sauvegarde a réussi, false sinon
     */
    @Override
    public boolean sauvegarderCoffre(Joueur joueur, Coffre coffre) {
        try {
            System.out.println("Sauvegarde du coffre en MongoDB pour le joueur ID: " + joueur.getId());
            
            // Vérifier si un coffre existe déjà pour ce joueur
            Document existingCoffre = collection.find(Filters.eq("id_joueur", joueur.getId())).first();
            
            if (existingCoffre != null) {
                // Si un coffre existe, le supprimer d'abord
                System.out.println("Suppression du coffre existant dans MongoDB");
                collection.deleteOne(Filters.eq("id_joueur", joueur.getId()));
            }
            
            // Créer un nouveau document pour le coffre
            Document coffreDoc = new Document();
            coffreDoc.append("id_joueur", joueur.getId());
            
            List<Document> itemsDoc = new ArrayList<>();
            
            // Parcourir les slots du coffre
            int nbItemsSauvegardes = 0;
            for (int i = 0; i < coffre.getSlots().size(); i++) {
                Slot slot = coffre.getSlots().get(i);
                if (slot != null && slot.getItem() != null && slot.getQuantity() > 0) {
                    Document itemDoc = new Document();
                    itemDoc.append("id_item", slot.getItem().getId());
                    itemDoc.append("quantite", slot.getQuantity());
                    itemDoc.append("position", i); // Sauvegarder la position dans le coffre
                    
                    // Vérifier si c'est un item empilable ou non (arme/bouclier)
                    boolean empilable = !(slot.getItem() instanceof Arme || slot.getItem() instanceof Bouclier);
                    itemDoc.append("empilable", empilable);
                    
                    itemsDoc.add(itemDoc);
                    nbItemsSauvegardes++;
                    System.out.println("  Slot " + i + " sauvegardé: " + slot.getItem().getNom() + " x" + slot.getQuantity());
                }
            }
            
            coffreDoc.append("items", itemsDoc);
            coffreDoc.append("max_slots", coffre.getMaxSlots());
            
            // Insérer le nouveau document
            System.out.println("Insertion de " + nbItemsSauvegardes + " items dans MongoDB pour le coffre");
            collection.insertOne(coffreDoc);
            
            // Vérifier que l'insertion a bien fonctionné
            Document verif = collection.find(Filters.eq("id_joueur", joueur.getId())).first();
            if (verif == null) {
                System.err.println("Erreur: Le coffre n'a pas été sauvegardé correctement dans MongoDB");
                return false;
            }
            
            System.out.println("Coffre sauvegardé avec succès dans MongoDB");
            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde du coffre dans MongoDB: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Recherche un item par son ID en cherchant d'abord dans MongoDB puis en SQL
     * 
     * @param itemId L'ID de l'item à rechercher
     * @return L'item trouvé ou null si non trouvé
     */
    private Item trouverItemParId(int itemId) {
        // D'abord, chercher dans MongoDB
        List<Item> itemsMongo = itemMongoDAO.obtenirTousLesItems();
        for (Item item : itemsMongo) {
            if (item.getId() == itemId) {
                return item;
            }
        }
        
        // Si l'item n'est pas trouvé, retourner null
        return null;
    }

    /**
     * Récupère le coffre d'un joueur depuis MongoDB.
     *
     * @param joueurId L'identifiant du joueur
     * @return Le coffre du joueur ou null si aucun coffre n'est trouvé
     */
    @Override
    public Coffre obtenirCoffreParJoueurId(int joueurId) {
        try {
            System.out.println("Chargement du coffre depuis MongoDB pour le joueur ID: " + joueurId);
            Document coffreDoc = collection.find(Filters.eq("id_joueur", joueurId)).first();
            
            if (coffreDoc == null) {
                System.out.println("Aucun coffre trouvé dans MongoDB pour le joueur ID: " + joueurId);
                return null;
            }
            
            // Créer un nouveau coffre
            Coffre coffre = new Coffre();
            
            // Récupérer la liste des items
            List<Document> itemsDoc = coffreDoc.getList("items", Document.class);
            System.out.println("Nombre d'items trouvés dans MongoDB: " + (itemsDoc != null ? itemsDoc.size() : 0));
            
            if (itemsDoc != null) {
                for (Document itemDoc : itemsDoc) {
                    int itemId = itemDoc.getInteger("id_item");
                    int quantite = itemDoc.getInteger("quantite");
                    int position = itemDoc.getInteger("position", -1);
                    
                    // Récupérer l'item en cherchant à la fois dans MongoDB et SQL
                    Item item = trouverItemParId(itemId);
                    
                    if (item != null) {
                        System.out.println("Item trouvé - ID: " + itemId + ", Nom: " + item.getNom() + ", Quantité: " + quantite + ", Position: " + position);
                        
                        // Si une position spécifique est indiquée
                        if (position >= 0 && position < coffre.getMaxSlots()) {
                            // Vérifier si le slot existe déjà à cette position
                            if (coffre.getSlots().get(position) == null) {
                                coffre.getSlots().set(position, new Slot(item, quantite));
                                System.out.println("  → Placé dans le slot " + position);
                            } else {
                                // Si le slot existe déjà, ajouter la quantité
                                coffre.getSlots().get(position).add(quantite);
                                System.out.println("  → Ajouté au slot existant " + position + ", nouvelle quantité: " + coffre.getSlots().get(position).getQuantity());
                            }
                        } else {
                            // Sinon ajouter normalement (le système trouvera un slot)
                            boolean ajoutOk = coffre.ajouterItem(item, quantite);
                            System.out.println("  → Ajout automatique dans un slot disponible: " + (ajoutOk ? "réussi" : "échoué"));
                        }
                    } else {
                        System.err.println("Item avec ID " + itemId + " non trouvé lors du chargement du coffre");
                    }
                }
            }
            
            // Afficher le contenu final du coffre chargé
            System.out.println("Contenu final du coffre chargé:");
            for (int i = 0; i < coffre.getSlots().size(); i++) {
                Slot slot = coffre.getSlots().get(i);
                if (slot != null && slot.getItem() != null) {
                    System.out.println("  Slot " + i + ": " + slot.getItem().getNom() + " x" + slot.getQuantity());
                }
            }
            
            return coffre;
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du coffre depuis MongoDB: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Ajoute un item au coffre d'un joueur.
     *
     * @param joueurId L'identifiant du joueur
     * @param item L'item à ajouter
     * @param quantite La quantité à ajouter
     * @return true si l'ajout a réussi, false sinon
     */
    @Override
    public boolean ajouterItemAuCoffre(int joueurId, Item item, int quantite) {
        try {
            // Récupérer le coffre actuel
            Coffre coffre = obtenirCoffreParJoueurId(joueurId);
            
            if (coffre == null) {
                coffre = new Coffre();
            }
            
            // Ajouter l'item au coffre
            boolean success = coffre.ajouterItem(item, quantite);
            
            if (!success) {
                return false;
            }
            
            // Sauvegarder le coffre
            Joueur joueur = new Joueur();
            joueur.setId(joueurId);
            
            return sauvegarderCoffre(joueur, coffre);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout d'un item au coffre dans MongoDB: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retire un item du coffre d'un joueur.
     *
     * @param joueurId L'identifiant du joueur
     * @param itemId L'identifiant de l'item à retirer
     * @param quantite La quantité à retirer
     * @return true si le retrait a réussi, false sinon
     */
    @Override
    public boolean retirerItemDuCoffre(int joueurId, int itemId, int quantite) {
        try {
            // Récupérer le coffre actuel
            Coffre coffre = obtenirCoffreParJoueurId(joueurId);
            
            if (coffre == null) {
                return false;
            }
            
            // Chercher l'item dans le coffre
            Item itemToRemove = null;
            for (Slot slot : coffre.getSlots()) {
                if (slot != null && slot.getItem() != null && slot.getItem().getId() == itemId) {
                    itemToRemove = slot.getItem();
                    break;
                }
            }
            
            if (itemToRemove == null) {
                return false;
            }
            
            // Retirer l'item du coffre
            boolean success = coffre.enleverItem(itemToRemove, quantite);
            
            if (!success) {
                return false;
            }
            
            // Sauvegarder le coffre
            Joueur joueur = new Joueur();
            joueur.setId(joueurId);
            
            return sauvegarderCoffre(joueur, coffre);
        } catch (Exception e) {
            System.err.println("Erreur lors du retrait d'un item du coffre dans MongoDB: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Vide complètement le coffre d'un joueur.
     *
     * @param joueurId L'identifiant du joueur
     * @return true si l'opération a réussi, false sinon
     */
    @Override
    public boolean viderCoffre(int joueurId) {
        try {
            // Supprimer le document du coffre
            collection.deleteOne(Filters.eq("id_joueur", joueurId));
            
            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors du vidage du coffre dans MongoDB: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtient la liste des slots du coffre d'un joueur.
     *
     * @param joueurId L'identifiant du joueur
     * @return La liste des slots du coffre ou une liste vide si aucun coffre n'est trouvé
     */
    @Override
    public List<Slot> obtenirSlotsDuCoffre(int joueurId) {
        Coffre coffre = obtenirCoffreParJoueurId(joueurId);
        
        if (coffre != null) {
            return coffre.getSlots();
        }
        
        return new ArrayList<>();
    }
} 