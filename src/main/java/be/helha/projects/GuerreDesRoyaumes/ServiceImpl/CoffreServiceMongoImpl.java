package be.helha.projects.GuerreDesRoyaumes.ServiceImpl;

import be.helha.projects.GuerreDesRoyaumes.Config.InitialiserAPP;
import be.helha.projects.GuerreDesRoyaumes.Config.DAOProvider;
import be.helha.projects.GuerreDesRoyaumes.DAO.CoffreMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.ItemMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.CoffreMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.ItemMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.MongoDBConnectionException;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Coffre;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Slot;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Service.CoffreService;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implémentation de l'interface CoffreService utilisant MongoDB pour la gestion des coffres des joueurs.
 */
@Service
public class CoffreServiceMongoImpl implements CoffreService {

    private static CoffreServiceMongoImpl instance;
    private final MongoDatabase mongoDB;
    private final CoffreMongoDAO coffreMongoDAO;
    private final ItemMongoDAO itemMongoDAO;

    /**
     * Constructeur avec injection de dépendances pour Spring
     */
    @Autowired
    public CoffreServiceMongoImpl(CoffreMongoDAO coffreMongoDAO, ItemMongoDAO itemMongoDAO) {
        try {
            this.mongoDB = InitialiserAPP.getMongoConnexion();
            this.coffreMongoDAO = coffreMongoDAO;
            this.itemMongoDAO = itemMongoDAO;
        } catch (MongoDBConnectionException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Constructeur public sans paramètres pour le singleton
     */
    public CoffreServiceMongoImpl() {
        try {
            mongoDB = InitialiserAPP.getMongoConnexion();
            this.coffreMongoDAO = DAOProvider.getCoffreMongoDAO();
            this.itemMongoDAO = ItemMongoDAOImpl.getInstance();
        } catch (MongoDBConnectionException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Obtient l'instance unique de CoffreServiceMongoImpl (Singleton)
     * @return L'instance unique de CoffreServiceMongoImpl
     */
    public static synchronized CoffreServiceMongoImpl getInstance() {
        if (instance == null) {
            instance = new CoffreServiceMongoImpl();
        }
        return instance;
    }

    @Override
    public boolean sauvegarderCoffre(Joueur joueur) {
        if (joueur == null || joueur.getCoffre() == null) {
            System.err.println("Impossible de sauvegarder le coffre: joueur ou coffre null");
            return false;
        }

        try {
            return coffreMongoDAO.sauvegarderCoffre(joueur, joueur.getCoffre());
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde du coffre dans MongoDB: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean chargerCoffre(Joueur joueur) {
        if (joueur == null) {
            System.err.println("Impossible de charger le coffre: joueur null");
            return false;
        }

        try {
            // Récupérer le coffre depuis MongoDB
            Coffre coffre = coffreMongoDAO.obtenirCoffreParJoueurId(joueur.getId());
            
            if (coffre != null) {
                // Mettre à jour le coffre du joueur
                joueur.setCoffre(coffre);
                return true;
            } else {
                // Si aucun coffre n'est trouvé, initialiser un nouveau coffre vide
                joueur.setCoffre(new Coffre());
                return true;
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du coffre depuis MongoDB: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean viderCoffre(Joueur joueur) {
        if (joueur == null) {
            System.err.println("Impossible de vider le coffre: joueur null");
            return false;
        }

        try {
            // Vider le coffre dans MongoDB
            boolean success = coffreMongoDAO.viderCoffre(joueur.getId());
            
            // Vider également l'objet coffre en mémoire
            if (success && joueur.getCoffre() != null) {
                joueur.setCoffre(new Coffre());
            }
            
            return success;
        } catch (Exception e) {
            System.err.println("Erreur lors du vidage du coffre dans MongoDB: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Ajoute un item au coffre d'un joueur.
     * 
     * @param joueur Le joueur
     * @param item L'item à ajouter
     * @param quantite La quantité à ajouter
     * @return true si l'ajout a réussi, false sinon
     */
    public boolean ajouterItemAuCoffre(Joueur joueur, Item item, int quantite) {
        if (joueur == null || item == null || quantite <= 0) {
            System.err.println("Paramètres invalides pour l'ajout d'item au coffre");
            return false;
        }

        try {
            // S'assurer que le coffre est chargé
            if (joueur.getCoffre() == null) {
                chargerCoffre(joueur);
            }
            
            // Ajouter l'item au coffre
            boolean success = joueur.getCoffre().ajouterItem(item, quantite);
            
            if (success) {
                // Sauvegarder le coffre mis à jour
                return sauvegarderCoffre(joueur);
            }
            
            return false;
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout d'un item au coffre: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retire un item du coffre d'un joueur.
     * 
     * @param joueur Le joueur
     * @param itemId L'identifiant de l'item à retirer
     * @param quantite La quantité à retirer
     * @return true si le retrait a réussi, false sinon
     */
    public boolean retirerItemDuCoffre(Joueur joueur, int itemId, int quantite) {
        if (joueur == null || joueur.getCoffre() == null || quantite <= 0) {
            System.err.println("Paramètres invalides pour le retrait d'item du coffre");
            return false;
        }

        try {
            // Chercher l'item dans le coffre
            Item itemToRemove = null;
            for (Slot slot : joueur.getCoffre().getSlots()) {
                if (slot != null && slot.getItem() != null && slot.getItem().getId() == itemId) {
                    itemToRemove = slot.getItem();
                    break;
                }
            }
            
            if (itemToRemove == null) {
                System.err.println("Item non trouvé dans le coffre");
                return false;
            }
            
            // Retirer l'item du coffre
            boolean success = joueur.getCoffre().enleverItem(itemToRemove, quantite);
            
            if (success) {
                // Sauvegarder le coffre mis à jour
                return sauvegarderCoffre(joueur);
            }
            
            return false;
        } catch (Exception e) {
            System.err.println("Erreur lors du retrait d'un item du coffre: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
} 