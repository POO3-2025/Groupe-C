package be.helha.projects.GuerreDesRoyaumes.ServiceImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.CoffreMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.InventaireMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.CoffreMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.InventairePersonnageMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.MongoDBConnectionException;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;
import be.helha.projects.GuerreDesRoyaumes.Service.InventaireService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implémentation du service {@link InventaireService} gérant
 * les transferts d'items entre le coffre du joueur et son inventaire de combat.
 * <p>
 * Cette classe utilise MongoDB pour la persistance via les DAO correspondants.
 * Elle vérifie la présence des items, les capacités des inventaires,
 * et assure la cohérence des données en cas d'erreur.
 * </p>
 */
@Service
public class InventaireServiceImpl implements InventaireService {

    private static InventaireServiceImpl instance;
    private InventaireMongoDAO inventairePersonnageDAO;
    private CoffreMongoDAO coffreMongoDAO;

    /**
     * Constructeur par défaut.
     * Initialise les DAO MongoDB.
     *
     * @throws MongoDBConnectionException en cas de problème de connexion.
     */
    public InventaireServiceImpl() throws MongoDBConnectionException {
        this.inventairePersonnageDAO = InventairePersonnageMongoDAOImpl.getInstance();
        this.coffreMongoDAO = CoffreMongoDAOImpl.getInstance();
    }

    /**
     * Obtient l'instance singleton de ce service.
     *
     * @return Instance unique de InventaireServiceImpl.
     * @throws MongoDBConnectionException en cas d'erreur MongoDB.
     */
    public static synchronized InventaireServiceImpl getInstance() throws MongoDBConnectionException {
        if (instance == null) {
            instance = new InventaireServiceImpl();
        }
        return instance;
    }

    /**
     * Transfère un item du coffre vers l'inventaire de combat du joueur.
     * <p>
     * Vérifie que l'item existe dans le coffre, que l'inventaire n'est pas plein,
     * ajoute l'item à l'inventaire et le retire du coffre.
     * </p>
     *
     * @param joueur Le joueur concerné.
     * @param itemId L'identifiant de l'item à transférer.
     * @return L'item transféré.
     * @throws Exception En cas d'erreur (item absent, inventaire plein, échec ajout/suppression).
     */
    @Override
    public Item transfererDuCoffreVersInventaire(Joueur joueur, int itemId) throws Exception {
        if (joueur == null) {
            throw new Exception("Impossible de transférer un item: joueur non défini");
        }

        try {
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
            if (joueur.getPersonnage() == null) {
                throw new Exception("Le personnage du joueur n'est pas initialisé");
            }
            if (joueur.getPersonnage().getInventaire() == null) {
                joueur.getPersonnage().setInventaire(new Inventaire());
            }
            List<Item> itemsInventaire = inventairePersonnageDAO.obtenirItemsInventaire(joueur.getPseudo());
            if (itemsInventaire.size() >= 5) {
                throw new Exception("Inventaire de combat plein, impossible d'ajouter plus d'items");
            }
            boolean ajoutReussi = inventairePersonnageDAO.ajouterItemAInventaire(joueur.getId(), itemATrouver, 1);
            if (!ajoutReussi) {
                throw new Exception("Échec de l'ajout à l'inventaire");
            }
            boolean suppressionReussie = coffreMongoDAO.supprimerItemDuCoffre(joueur.getPseudo(), itemId);
            if (!suppressionReussie) {
                inventairePersonnageDAO.supprimerItemInventaire(joueur.getPseudo(), itemId);
                throw new Exception("Échec de la suppression du coffre");
            }
            return itemATrouver;
        } catch (Exception e) {
            System.err.println("Erreur lors du transfert d'un item du coffre vers l'inventaire: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Transfère un item de l'inventaire de combat vers le coffre du joueur.
     * <p>
     * Vérifie que l'item est présent dans l'inventaire, ajoute l'item au coffre,
     * puis le retire de l'inventaire.
     * </p>
     *
     * @param joueur Le joueur concerné.
     * @param itemId L'identifiant de l'item à transférer.
     * @return true si le transfert a réussi.
     * @throws Exception En cas d'erreur (item absent, échec ajout/suppression).
     */
    @Override
    public boolean transfererDeInventaireVersCoffre(Joueur joueur, int itemId) throws Exception {
        if (joueur == null) {
            throw new Exception("Impossible de transférer un item: joueur non défini");
        }

        try {
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
            boolean ajoutReussi = coffreMongoDAO.ajouterItemAuCoffre(joueur.getId(), itemATrouver, 1);
            if (!ajoutReussi) {
                throw new Exception("Échec de l'ajout au coffre");
            }
            boolean suppressionReussie = inventairePersonnageDAO.supprimerItemInventaire(joueur.getPseudo(), itemId);
            if (!suppressionReussie) {
                coffreMongoDAO.supprimerItemDuCoffre(joueur.getPseudo(), itemId);
                throw new Exception("Échec de la suppression de l'inventaire");
            }
            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors du transfert d'un item de l'inventaire vers le coffre: " + e.getMessage());
            throw e;
        }
    }
}
