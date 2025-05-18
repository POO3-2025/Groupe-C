package be.helha.projects.GuerreDesRoyaumes.ServiceImpl;

import be.helha.projects.GuerreDesRoyaumes.Config.InitialiserAPP;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.CoffreMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.ItemMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.MongoDBConnectionException;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Coffre;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Slot;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Service.CoffreService;
import com.mongodb.client.MongoDatabase;
import org.springframework.stereotype.Service;

/**
 * Implémentation du service {@link CoffreService} utilisant MongoDB
 * pour la gestion des coffres des joueurs.
 * <p>
 * Cette classe est un singleton et gère la sauvegarde, le chargement,
 * le vidage ainsi que l'ajout et le retrait d'items dans les coffres.
 * </p>
 */
@Service
public class CoffreServiceMongoImpl implements CoffreService {

    private static CoffreServiceMongoImpl instance;
    private final CoffreMongoDAOImpl coffreMongoDAO;
    private final ItemMongoDAOImpl itemMongoDAO;

    /**
     * Constructeur privé du singleton.
     * Initialise les DAO MongoDB et la connexion.
     */
    private CoffreServiceMongoImpl() {
        try {
            MongoDatabase mongoDB = InitialiserAPP.getMongoConnexion();
            this.coffreMongoDAO = CoffreMongoDAOImpl.getInstance();
            this.itemMongoDAO = ItemMongoDAOImpl.getInstance();
        } catch (MongoDBConnectionException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Obtient l'instance unique du singleton.
     *
     * @return instance unique de CoffreServiceMongoImpl.
     */
    public static synchronized CoffreServiceMongoImpl getInstance() {
        if (instance == null) {
            instance = new CoffreServiceMongoImpl();
        }
        return instance;
    }

    /**
     * Sauvegarde le coffre d'un joueur dans MongoDB.
     *
     * @param joueur Le joueur dont le coffre sera sauvegardé.
     * @return true si la sauvegarde a réussi, false sinon.
     */
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

    /**
     * Charge le coffre d'un joueur depuis MongoDB.
     *
     * @param joueur Le joueur dont le coffre sera chargé.
     * @return true si le chargement a réussi, false sinon.
     */
    @Override
    public boolean chargerCoffre(Joueur joueur) {
        if (joueur == null) {
            System.err.println("Impossible de charger le coffre: joueur null");
            return false;
        }
        try {
            Coffre coffre = coffreMongoDAO.obtenirCoffreParJoueurId(joueur.getId());
            if (coffre != null) {
                joueur.setCoffre(coffre);
            } else {
                joueur.setCoffre(new Coffre());
            }
            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du coffre depuis MongoDB: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Vide le coffre d'un joueur dans MongoDB et en mémoire.
     *
     * @param joueur Le joueur dont le coffre sera vidé.
     * @return true si l'opération a réussi, false sinon.
     */
    @Override
    public boolean viderCoffre(Joueur joueur) {
        if (joueur == null) {
            System.err.println("Impossible de vider le coffre: joueur null");
            return false;
        }
        try {
            boolean success = coffreMongoDAO.viderCoffre(joueur.getId());
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
     * Ajoute un item au coffre d'un joueur, puis sauvegarde.
     *
     * @param joueur   Le joueur concerné.
     * @param item     L'item à ajouter.
     * @param quantite La quantité à ajouter.
     * @return true si l'ajout et la sauvegarde ont réussi, false sinon.
     */
    public boolean ajouterItemAuCoffre(Joueur joueur, Item item, int quantite) {
        if (joueur == null || item == null || quantite <= 0) {
            System.err.println("Paramètres invalides pour l'ajout d'item au coffre");
            return false;
        }
        try {
            if (joueur.getCoffre() == null) {
                chargerCoffre(joueur);
            }
            boolean success = joueur.getCoffre().ajouterItem(item, quantite);
            if (success) {
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
     * Retire un item du coffre d'un joueur, puis sauvegarde.
     *
     * @param joueur   Le joueur concerné.
     * @param itemId   L'identifiant de l'item à retirer.
     * @param quantite La quantité à retirer.
     * @return true si le retrait et la sauvegarde ont réussi, false sinon.
     */
    public boolean retirerItemDuCoffre(Joueur joueur, int itemId, int quantite) {
        if (joueur == null || joueur.getCoffre() == null || quantite <= 0) {
            System.err.println("Paramètres invalides pour le retrait d'item du coffre");
            return false;
        }
        try {
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
            boolean success = joueur.getCoffre().enleverItem(itemToRemove, quantite);
            if (success) {
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
