package be.helha.projects.GuerreDesRoyaumes.ServiceImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.CoffreMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.InventaireDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.CoffreMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.InventaireMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.MongoDBConnectionException;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Service.InventaireService;
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
    private InventaireDAO inventaireDAO;
    private CoffreMongoDAO coffreMongoDAO;

    /**
     * Constructeur par défaut
     */
    public InventaireServiceImpl() throws MongoDBConnectionException {
        this.inventaireDAO = InventaireMongoDAOImpl.getInstance();
        this.coffreMongoDAO = CoffreMongoDAOImpl.getInstance();
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
    public boolean transfererDuCoffreVersInventaire(Joueur joueur, int itemId) {
        if (joueur == null) {
            System.err.println("Impossible de transférer un item: joueur null");
            return false;
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
                System.err.println("Item non trouvé dans le coffre");
                return false;
            }

            // Vérifier si l'inventaire n'est pas plein
            List<Item> itemsInventaire = inventaireDAO.obtenirItemsInventaire(joueur.getPseudo());
            if (itemsInventaire.size() >= 10) {
                System.err.println("Inventaire plein, impossible d'ajouter plus d'items");
                return false;
            }

            // Ajouter l'item à l'inventaire
            boolean ajoutReussi = inventaireDAO.ajouterItemInventaire(joueur.getPseudo(), itemATrouver);

            if (!ajoutReussi) {
                System.err.println("Échec de l'ajout à l'inventaire");
                return false;
            }

            // Supprimer l'item du coffre
            boolean suppressionReussie = coffreMongoDAO.supprimerItemDuCoffre(joueur.getPseudo(), itemId);

            if (!suppressionReussie) {
                // Si la suppression échoue, on essaie de retirer l'item de l'inventaire pour revenir à l'état initial
                inventaireDAO.supprimerItemInventaire(joueur.getPseudo(), itemId);
                System.err.println("Échec de la suppression du coffre");
                return false;
            }

            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors du transfert d'un item du coffre vers l'inventaire: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean transfererDeInventaireVersCoffre(Joueur joueur, int itemId) {
        if (joueur == null) {
            System.err.println("Impossible de transférer un item: joueur null");
            return false;
        }

        try {
            // Vérifier si l'item existe dans l'inventaire
            List<Item> itemsInventaire = inventaireDAO.obtenirItemsInventaire(joueur.getPseudo());
            Item itemATrouver = null;

            for (Item item : itemsInventaire) {
                if (item.getId() == itemId) {
                    itemATrouver = item;
                    break;
                }
            }

            if (itemATrouver == null) {
                System.err.println("Item non trouvé dans l'inventaire");
                return false;
            }

            // Ajouter l'item au coffre
            //TODO : verifier si c'est bien 1 a passer en parametre
            boolean ajoutReussi = coffreMongoDAO.ajouterItemAuCoffre(joueur.getId(), itemATrouver, 1);

            if (!ajoutReussi) {
                System.err.println("Échec de l'ajout au coffre");
                return false;
            }

            // Supprimer l'item de l'inventaire
            boolean suppressionReussie = inventaireDAO.supprimerItemInventaire(joueur.getPseudo(), itemId);

            if (!suppressionReussie) {
                // Si la suppression échoue, on essaie de retirer l'item du coffre pour revenir à l'état initial
                coffreMongoDAO.supprimerItemDuCoffre(joueur.getPseudo(), itemId);
                System.err.println("Échec de la suppression de l'inventaire");
                return false;
            }

            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors du transfert d'un item de l'inventaire vers le coffre: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}