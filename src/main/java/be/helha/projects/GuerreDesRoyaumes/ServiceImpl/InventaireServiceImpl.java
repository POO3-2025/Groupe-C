package be.helha.projects.GuerreDesRoyaumes.ServiceImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.CoffreMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.InventaireDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.InventaireMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.CoffreMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.InventaireMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.InventairePersonnageMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.MongoDBConnectionException;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;
import be.helha.projects.GuerreDesRoyaumes.Service.InventaireService;
import be.helha.projects.GuerreDesRoyaumes.Config.DAOProvider;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implémentation de l'interface InventaireService pour la gestion des transferts
 * entre le coffre et l'inventaire de combat.
 * Utilise MongoDB pour la persistance des données.
 */
@Service
public class InventaireServiceImpl implements InventaireService {

    private static InventaireServiceImpl instance;
    private InventaireMongoDAO inventairePersonnageDAO;
    private CoffreMongoDAO coffreMongoDAO;

    /**
     * Constructeur par défaut
     */
    public InventaireServiceImpl() throws MongoDBConnectionException {
        this.inventairePersonnageDAO = InventairePersonnageMongoDAOImpl.getInstance();
        this.coffreMongoDAO = DAOProvider.getCoffreMongoDAO();
    }

    /**
     * Obtient l'instance unique de InventaireServiceImpl (Singleton)
     * @return L'instance unique de InventaireServiceImpl
     */
    public static synchronized InventaireServiceImpl getInstance() throws MongoDBConnectionException {
        if (instance == null) {
            instance = new InventaireServiceImpl();
        }
        return instance;
    }

    @Override
    public Item transfererDuCoffreVersInventaire(Joueur joueur, int itemId) throws Exception {
        if (joueur == null) {
            throw new Exception("Impossible de transférer un item: joueur non défini");
        }

        try {
            // Vérifier si l'item existe dans le coffre
            List<Item> itemsCoffre = coffreMongoDAO.obtenirItemsDuCoffre(joueur.getPseudo());
            Item itemATrouver = null;

            for (Item item : itemsCoffre) {
                if (item.getId() == itemId) {
                    itemATrouver = item;
                    break;
                }
            }

            if (itemATrouver == null) {
                throw new Exception("Item non trouvé dans le coffre");
            }

            // Vérifier si l'inventaire du personnage est initialisé
            if (joueur.getPersonnage() == null) {
                throw new Exception("Le personnage du joueur n'est pas initialisé");
            }
            
            if (joueur.getPersonnage().getInventaire() == null) {
                joueur.getPersonnage().setInventaire(new Inventaire());
            }
            
            // Vérifier si l'inventaire n'est pas plein (max 5 items)
            List<Item> itemsInventaire = inventairePersonnageDAO.obtenirItemsInventaire(joueur.getPseudo());
            if (itemsInventaire.size() >= 5) {
                throw new Exception("Inventaire de combat plein, impossible d'ajouter plus d'items");
            }

            // Ajouter l'item à l'inventaire du personnage
            boolean ajoutReussi = inventairePersonnageDAO.ajouterItemAInventaire(joueur.getId(), itemATrouver, 1);

            if (!ajoutReussi) {
                throw new Exception("Échec de l'ajout à l'inventaire");
            }

            // Supprimer l'item du coffre
            boolean suppressionReussie = coffreMongoDAO.supprimerItemDuCoffre(joueur.getPseudo(), itemId);

            if (!suppressionReussie) {
                // Si la suppression échoue, on essaie de retirer l'item de l'inventaire pour revenir à l'état initial
                inventairePersonnageDAO.supprimerItemInventaire(joueur.getPseudo(), itemId);
                throw new Exception("Échec de la suppression du coffre");
            }

            return itemATrouver;
        } catch (Exception e) {
            // Consigner l'erreur dans la console, mais la propager pour l'affichage dans l'UI
            System.err.println("Erreur lors du transfert d'un item du coffre vers l'inventaire: " + e.getMessage());
            throw e; // Relancer l'exception pour qu'elle soit traitée par l'interface utilisateur
        }
    }

    @Override
    public boolean transfererDeInventaireVersCoffre(Joueur joueur, int itemId) throws Exception {
        if (joueur == null) {
            throw new Exception("Impossible de transférer un item: joueur non défini");
        }

        try {
            // Vérifier si l'item existe dans l'inventaire
            List<Item> itemsInventaire = inventairePersonnageDAO.obtenirItemsInventaire(joueur.getPseudo());
            Item itemATrouver = null;

            for (Item item : itemsInventaire) {
                if (item.getId() == itemId) {
                    itemATrouver = item;
                    break;
                }
            }

            if (itemATrouver == null) {
                throw new Exception("Item non trouvé dans l'inventaire");
            }

            // Ajouter l'item au coffre
            boolean ajoutReussi = coffreMongoDAO.ajouterItemAuCoffre(joueur.getId(), itemATrouver, 1);

            if (!ajoutReussi) {
                throw new Exception("Échec de l'ajout au coffre");
            }

            // Supprimer l'item de l'inventaire
            boolean suppressionReussie = inventairePersonnageDAO.supprimerItemInventaire(joueur.getPseudo(), itemId);

            if (!suppressionReussie) {
                // Si la suppression échoue, on essaie de retirer l'item du coffre pour revenir à l'état initial
                coffreMongoDAO.supprimerItemDuCoffre(joueur.getPseudo(), itemId);
                throw new Exception("Échec de la suppression de l'inventaire");
            }

            return true;
        } catch (Exception e) {
            // Consigner l'erreur dans la console, mais la propager pour l'affichage dans l'UI
            System.err.println("Erreur lors du transfert d'un item de l'inventaire vers le coffre: " + e.getMessage());
            throw e; // Relancer l'exception pour qu'elle soit traitée par l'interface utilisateur
        }
    }
}