package be.helha.projects.GuerreDesRoyaumes.DAOImpl;

import be.helha.projects.GuerreDesRoyaumes.Config.InitialiserAPP;
import be.helha.projects.GuerreDesRoyaumes.DAO.InventaireMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.ItemMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.MongoDBConnectionException;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Slot;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Arme;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Bouclier;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Potion;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.springframework.stereotype.Repository;
import org.bson.conversions.Bson;

import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation de l'interface InventaireMongoDAO pour la gestion des inventaires des personnages dans MongoDB.
 * Cette classe gère les opérations CRUD pour les documents Inventaire.
 */
@Repository
public class InventairePersonnageMongoDAOImpl implements InventaireMongoDAO {

    private static InventairePersonnageMongoDAOImpl instance;
    private final MongoCollection<Document> collection;
    private final ItemMongoDAO itemMongoDAO;
    private static final String COLLECTION_NAME = "inventaires";

    /**
     * Constructeur public pour l'injection de dépendances avec Spring
     */
    public InventairePersonnageMongoDAOImpl() {
        MongoDatabase mongoDB;
        try {
            mongoDB = InitialiserAPP.getMongoConnexion();
        } catch (MongoDBConnectionException ex) {
            throw new RuntimeException(ex);
        }
        this.collection = mongoDB.getCollection(COLLECTION_NAME);
        this.itemMongoDAO = ItemMongoDAOImpl.getInstance();
    }

    /**
     * Obtient l'instance unique de InventairePersonnageMongoDAOImpl (pattern Singleton).
     *
     * @return L'instance unique de InventairePersonnageMongoDAOImpl
     */
    public static synchronized InventairePersonnageMongoDAOImpl getInstance() throws MongoDBConnectionException {
        if (instance == null) {
            instance = new InventairePersonnageMongoDAOImpl();
        }
        return instance;
    }

    /**
     * Sauvegarde l'inventaire d'un personnage dans MongoDB.
     *
     * @param joueur Le joueur propriétaire du personnage
     * @param inventaire L'inventaire à sauvegarder
     * @return true si la sauvegarde a réussi, false sinon
     */
    @Override
    public boolean sauvegarderInventaire(Joueur joueur, Inventaire inventaire) {
        try {
            System.out.println("Sauvegarde de l'inventaire en MongoDB pour le joueur ID: " + joueur.getId());

            // Vérifier si un inventaire existe déjà pour ce joueur
            Document existingInventaire = collection.find(Filters.eq("id_joueur", joueur.getId())).first();

            if (existingInventaire != null) {
                // Si un inventaire existe, le supprimer d'abord
                System.out.println("Suppression de l'inventaire existant dans MongoDB");
                collection.deleteOne(Filters.eq("id_joueur", joueur.getId()));
            }

            // Créer un nouveau document pour l'inventaire
            Document inventaireDoc = new Document();
            inventaireDoc.append("id_joueur", joueur.getId());

            List<Document> itemsDoc = new ArrayList<>();

            // Parcourir les slots de l'inventaire
            int nbItemsSauvegardes = 0;
            for (int i = 0; i < inventaire.getSlots().size(); i++) {
                Slot slot = inventaire.getSlots().get(i);
                if (slot != null && slot.getItem() != null && slot.getQuantity() > 0) {
                    Document itemDoc = new Document();
                    itemDoc.append("id_item", slot.getItem().getId());
                    itemDoc.append("quantite", slot.getQuantity());
                    itemDoc.append("position", i); // Sauvegarder la position dans l'inventaire

                    // Vérifier si c'est un item empilable ou non (arme/bouclier)
                    boolean empilable = !(slot.getItem() instanceof Arme || slot.getItem() instanceof Bouclier);
                    itemDoc.append("empilable", empilable);

                    itemsDoc.add(itemDoc);
                    nbItemsSauvegardes++;
                    System.out.println("  Slot " + i + " sauvegardé: " + slot.getItem().getNom() + " x" + slot.getQuantity());
                }
            }

            inventaireDoc.append("items", itemsDoc);
            inventaireDoc.append("max_slots", inventaire.getMaxSlots());

            // Insérer le nouveau document
            System.out.println("Insertion de " + nbItemsSauvegardes + " items dans MongoDB pour l'inventaire");
            collection.insertOne(inventaireDoc);

            // Vérifier que l'insertion a bien fonctionné
            Document verif = collection.find(Filters.eq("id_joueur", joueur.getId())).first();
            if (verif == null) {
                System.err.println("Erreur: L'inventaire n'a pas été sauvegardé correctement dans MongoDB");
                return false;
            }

            System.out.println("Inventaire sauvegardé avec succès dans MongoDB");
            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde de l'inventaire dans MongoDB: " + e.getMessage());
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
     * Récupère l'inventaire d'un personnage depuis MongoDB.
     *
     * @param joueurId L'identifiant du joueur
     * @return L'inventaire du personnage ou null si aucun inventaire n'est trouvé
     */
    @Override
    public Inventaire obtenirInventaireParJoueurId(int joueurId) {
        try {
            System.out.println("Chargement de l'inventaire depuis MongoDB pour le joueur ID: " + joueurId);
            Document inventaireDoc = collection.find(Filters.eq("id_joueur", joueurId)).first();

            if (inventaireDoc == null) {
                System.out.println("Aucun inventaire trouvé dans MongoDB pour le joueur ID: " + joueurId);
                return null;
            }

            // Créer un nouvel inventaire
            Inventaire inventaire = new Inventaire();

            // Récupérer la liste des items
            List<Document> itemsDoc = inventaireDoc.getList("items", Document.class);
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
                        if (position >= 0 && position < inventaire.getMaxSlots()) {
                            // Vérifier si le slot existe déjà à cette position
                            if (inventaire.getSlots().get(position) == null) {
                                inventaire.getSlots().set(position, new Slot(item, quantite));
                                System.out.println("  → Placé dans le slot " + position);
                            } else {
                                // Si le slot existe déjà, ajouter la quantité
                                inventaire.getSlots().get(position).add(quantite);
                                System.out.println("  → Ajouté au slot existant " + position + ", nouvelle quantité: " + inventaire.getSlots().get(position).getQuantity());
                            }
                        } else {
                            // Sinon ajouter normalement (le système trouvera un slot)
                            boolean ajoutOk = inventaire.ajouterItem(item, quantite);
                            System.out.println("  → Ajout automatique dans un slot disponible: " + (ajoutOk ? "réussi" : "échoué"));
                        }
                    } else {
                        System.err.println("Item avec ID " + itemId + " non trouvé lors du chargement de l'inventaire");
                    }
                }
            }

            // Afficher le contenu final de l'inventaire chargé
            System.out.println("Contenu final de l'inventaire chargé:");
            for (int i = 0; i < inventaire.getSlots().size(); i++) {
                Slot slot = inventaire.getSlots().get(i);
                if (slot != null && slot.getItem() != null) {
                    System.out.println("  Slot " + i + ": " + slot.getItem().getNom() + " x" + slot.getQuantity());
                }
            }

            return inventaire;
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération de l'inventaire depuis MongoDB: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Ajoute un item à l'inventaire d'un personnage.
     *
     * @param joueurId L'identifiant du joueur
     * @param item L'item à ajouter
     * @param quantite La quantité à ajouter
     * @return true si l'ajout a réussi, false sinon
     */
    @Override
    public boolean ajouterItemAInventaire(int joueurId, Item item, int quantite) {
        try {
            // Récupérer l'inventaire actuel
            Inventaire inventaire = obtenirInventaireParJoueurId(joueurId);

            if (inventaire == null) {
                inventaire = new Inventaire();
            }

            // Ajouter l'item à l'inventaire
            boolean success = inventaire.ajouterItem(item, quantite);

            if (!success) {
                return false;
            }

            // Sauvegarder l'inventaire
            Joueur joueur = new Joueur();
            joueur.setId(joueurId);

            return sauvegarderInventaire(joueur, inventaire);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout d'un item à l'inventaire dans MongoDB: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retire un item de l'inventaire d'un personnage.
     *
     * @param joueurId L'identifiant du joueur
     * @param itemId L'identifiant de l'item à retirer
     * @param quantite La quantité à retirer
     * @return true si le retrait a réussi, false sinon
     */
    @Override
    public boolean retirerItemDeInventaire(int joueurId, int itemId, int quantite) {
        try {
            // Récupérer l'inventaire actuel
            Inventaire inventaire = obtenirInventaireParJoueurId(joueurId);

            if (inventaire == null) {
                return false;
            }

            // Chercher l'item dans l'inventaire
            Item itemToRemove = null;
            for (Slot slot : inventaire.getSlots()) {
                if (slot != null && slot.getItem() != null && slot.getItem().getId() == itemId) {
                    itemToRemove = slot.getItem();
                    break;
                }
            }

            if (itemToRemove == null) {
                return false;
            }

            // Retirer l'item de l'inventaire
            boolean success = inventaire.enleverItem(itemToRemove, quantite);

            if (!success) {
                return false;
            }

            // Sauvegarder l'inventaire
            Joueur joueur = new Joueur();
            joueur.setId(joueurId);

            return sauvegarderInventaire(joueur, inventaire);
        } catch (Exception e) {
            System.err.println("Erreur lors du retrait d'un item de l'inventaire dans MongoDB: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Vide complètement l'inventaire d'un personnage.
     *
     * @param joueurId L'identifiant du joueur
     * @return true si l'opération a réussi, false sinon
     */
    @Override
    public boolean viderInventaire(int joueurId) {
        try {
            // Supprimer le document de l'inventaire
            collection.deleteOne(Filters.eq("id_joueur", joueurId));

            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors du vidage de l'inventaire dans MongoDB: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtient la liste des slots de l'inventaire d'un personnage.
     *
     * @param joueurId L'identifiant du joueur
     * @return La liste des slots de l'inventaire ou une liste vide si aucun inventaire n'est trouvé
     */
    @Override
    public List<Slot> obtenirSlotsInventaire(int joueurId) {
        Inventaire inventaire = obtenirInventaireParJoueurId(joueurId);

        if (inventaire != null) {
            return inventaire.getSlots();
        }

        return new ArrayList<>();
    }

    @Override
    public List<Item> obtenirItemsInventaire(String pseudo) {
        try {
            // Obtenir l'ID du joueur à partir du pseudo
            be.helha.projects.GuerreDesRoyaumes.DAOImpl.JoueurDAOImpl joueurDAO = new be.helha.projects.GuerreDesRoyaumes.DAOImpl.JoueurDAOImpl();
            Joueur joueur = joueurDAO.obtenirJoueurParPseudo(pseudo);
            
            if (joueur == null) {
                System.err.println("Joueur non trouvé avec le pseudo: " + pseudo);
                return List.of();
            }
            
            // Récupérer l'inventaire du personnage
            Inventaire inventaire = obtenirInventaireParJoueurId(joueur.getId());
            
            if (inventaire == null) {
                System.err.println("Inventaire non trouvé pour le joueur: " + pseudo);
                return List.of();
            }
            
            // Extraire les items de l'inventaire
            List<Item> items = new ArrayList<>();
            for (Slot slot : inventaire.getSlots()) {
                if (slot != null && slot.getItem() != null) {
                    items.add(slot.getItem());
                }
            }
            
            System.out.println("Items trouvés dans l'inventaire de " + pseudo + ": " + items.size());
            return items;
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des items de l'inventaire: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public boolean supprimerItemInventaire(String pseudo, int itemId) {
        try {
            // Obtenir l'ID du joueur à partir du pseudo
            be.helha.projects.GuerreDesRoyaumes.DAOImpl.JoueurDAOImpl joueurDAO = new be.helha.projects.GuerreDesRoyaumes.DAOImpl.JoueurDAOImpl();
            Joueur joueur = joueurDAO.obtenirJoueurParPseudo(pseudo);
            
            if (joueur == null) {
                System.err.println("Joueur non trouvé avec le pseudo: " + pseudo);
                return false;
            }
            
            // Utiliser la méthode existante en passant l'ID du joueur et l'ID de l'item
            return retirerItemDeInventaire(joueur.getId(), itemId, 1); // Supprime un exemplaire de l'item
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression de l'item de l'inventaire: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
} 