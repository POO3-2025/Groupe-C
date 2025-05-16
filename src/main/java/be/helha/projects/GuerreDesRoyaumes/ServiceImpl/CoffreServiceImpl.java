package be.helha.projects.GuerreDesRoyaumes.ServiceImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.CoffreDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.ItemDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.CoffreMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.ItemMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Coffre;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Slot;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Service.CoffreService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implémentation de l'interface CoffreService pour la gestion des coffres des joueurs.
 * Utilise MongoDB pour la persistance des coffres.
 */
@Service
public class CoffreServiceImpl implements CoffreService {

    private static CoffreServiceImpl instance;
    private CoffreDAO coffreDAO;
    private ItemDAO itemDAO;

    /**
     * Constructeur par défaut
     */
    public CoffreServiceImpl() {
        this.coffreDAO = CoffreMongoDAOImpl.getInstance();
        this.itemDAO = ItemMongoDAOImpl.getInstance();
    }

    /**
     * Obtient l'instance unique de CoffreServiceImpl (Singleton)
     * @return L'instance unique de CoffreServiceImpl
     */
    public static synchronized CoffreServiceImpl getInstance() {
        if (instance == null) {
            instance = new CoffreServiceImpl();
        }
        return instance;
    }

    /**
     * Définit le DAO des coffres à utiliser
     * @param coffreDAO Le CoffreDAO à utiliser
     */
    public void setCoffreDAO(CoffreDAO coffreDAO) {
        this.coffreDAO = coffreDAO;
    }

    /**
     * Définit le DAO des items à utiliser
     * @param itemDAO L'ItemDAO à utiliser
     */
    public void setItemDAO(ItemDAO itemDAO) {
        this.itemDAO = itemDAO;
    }

    @Override
    public boolean sauvegarderCoffre(Joueur joueur) {
        if (joueur == null || joueur.getCoffre() == null) {
            System.err.println("Impossible de sauvegarder le coffre: joueur ou coffre null");
            return false;
        }

        try {
            // Parcourir tous les slots du coffre
            for (Slot slot : joueur.getCoffre().getSlots()) {
                if (slot != null && slot.getItem() != null && slot.getQuantity() > 0) {
                    // Ajouter l'item au coffre dans MongoDB
                    for (int i = 0; i < slot.getQuantity(); i++) {
                        coffreDAO.ajouterItemAuCoffre(joueur.getPseudo(), slot.getItem());
                    }
                }
            }

            System.out.println("Coffre sauvegardé pour le joueur: " + joueur.getPseudo());
            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde du coffre: " + e.getMessage());
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
            // Vider le coffre actuel ou créer un nouveau si nécessaire
            if (joueur.getCoffre() == null) {
                joueur.setCoffre(new Coffre());
            } else {
                // Créer un nouveau coffre vide
                joueur.setCoffre(new Coffre());
            }

            // Récupérer les items du coffre depuis MongoDB
            List<Item> itemsCoffre = coffreDAO.obtenirItemsDuCoffre(joueur.getPseudo());

            // Ajouter chaque item au coffre
            for (Item item : itemsCoffre) {
                joueur.getCoffre().ajouterItem(item, 1);
            }

            System.out.println("Coffre chargé pour le joueur: " + joueur.getPseudo());
            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement du coffre: " + e.getMessage());
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
            // Récupérer d'abord tous les items
            List<Item> itemsCoffre = coffreDAO.obtenirItemsDuCoffre(joueur.getPseudo());

            // Supprimer chaque item
            for (Item item : itemsCoffre) {
                coffreDAO.supprimerItemDuCoffre(joueur.getPseudo(), item.getId());
            }

            // Vider également l'objet coffre en mémoire
            if (joueur.getCoffre() != null) {
                joueur.setCoffre(new Coffre());
            }

            System.out.println("Coffre vidé pour le joueur: " + joueur.getPseudo());
            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors du vidage du coffre: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}