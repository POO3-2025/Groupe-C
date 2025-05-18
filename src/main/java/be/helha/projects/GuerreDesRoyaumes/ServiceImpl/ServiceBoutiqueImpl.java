package be.helha.projects.GuerreDesRoyaumes.ServiceImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.ItemMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.ItemMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.JoueurDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Coffre;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Slot;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Service.CoffreService;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceBoutique;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implémentation du service de gestion de la boutique pour l'achat d'items par les joueurs.
 * <p>
 * Ce service permet à un joueur d'acheter un item en vérifiant les fonds,
 * en ajoutant l'item dans son coffre (persisté dans MongoDB), et en déduisant le coût.
 * </p>
 * <p>
 * Utilise {@link ItemMongoDAO} pour accéder aux items disponibles,
 * {@link JoueurDAO} pour manipuler les données du joueur,
 * et {@link CoffreService} pour gérer la persistance du coffre du joueur.
 * </p>
 */
@Service
public class ServiceBoutiqueImpl implements ServiceBoutique {

    private ItemMongoDAO itemMongoDAO;
    private JoueurDAO joueurDAO;
    private CoffreService coffreService;
    private static final int PRIX_BASE = 100;
    private static ServiceBoutiqueImpl instance;

    /**
     * Constructeur par défaut qui initialise les DAO nécessaires.
     */
    public ServiceBoutiqueImpl() {
        this.itemMongoDAO = ItemMongoDAOImpl.getInstance();
        this.joueurDAO = JoueurDAOImpl.getInstance();
        this.coffreService = CoffreServiceMongoImpl.getInstance();
    }

    /**
     * Obtient l'instance singleton du service.
     *
     * @return instance unique de {@link ServiceBoutiqueImpl}
     */
    public static synchronized ServiceBoutiqueImpl getInstance() {
        if (instance == null) {
            instance = new ServiceBoutiqueImpl();
        }
        return instance;
    }

    /**
     * Permet à un joueur d'acheter un item.
     * <p>
     * Cette méthode vérifie l'existence du joueur et de l'item,
     * calcule le prix total en fonction de la quantité,
     * vérifie les fonds disponibles,
     * ajoute l'item au coffre du joueur,
     * débite le joueur,
     * et sauvegarde les changements dans les bases de données respectives.
     * </p>
     *
     * @param joueurId Identifiant du joueur acheteur
     * @param itemId   Identifiant de l'item à acheter
     * @param quantite Quantité d'items à acheter
     * @return true si l'achat s'est déroulé avec succès, false sinon
     */
    @Override
    public boolean acheterItem(int joueurId, int itemId, int quantite) {
        try {
            System.out.println("Début d'achat - Item ID: " + itemId + ", Quantité: " + quantite);

            Joueur joueur = joueurDAO.obtenirJoueurParId(joueurId);
            if (joueur == null) {
                throw new IllegalArgumentException("Joueur non trouvé");
            }

            // Récupérer l'item depuis MongoDB
            Item item = trouverItemParId(itemId);
            if (item == null) {
                throw new IllegalArgumentException("Item non trouvé");
            }

            // Calculer le prix total
            int prixTotal = calculerPrixAchat(item, quantite);

            // Vérifier si le joueur a assez d'argent
            if (joueur.getArgent() < prixTotal) {
                throw new IllegalArgumentException("Fonds insuffisants");
            }

            // Recharger le coffre depuis MongoDB pour éviter d'écraser les données existantes
            coffreService.chargerCoffre(joueur);

            // Vérifier que le coffre est bien initialisé
            Coffre coffre = joueur.getCoffre();
            if (coffre == null) {
                joueur.setCoffre(new Coffre());
                coffre = joueur.getCoffre();
            }

            System.out.println("Contenu du coffre avant ajout:");
            for (int i = 0; i < coffre.getSlots().size(); i++) {
                Slot slot = coffre.getSlots().get(i);
                if (slot != null && slot.getItem() != null) {
                    System.out.println("  Slot " + i + ": " + slot.getItem().getNom() + " x" + slot.getQuantity());
                }
            }

            // Ajouter l'item au coffre
            boolean ajoutReussi = coffre.ajouterItem(item, quantite);
            if (!ajoutReussi) {
                throw new IllegalArgumentException("Coffre plein ou quantité trop élevée");
            }

            System.out.println("Contenu du coffre après ajout:");
            for (int i = 0; i < coffre.getSlots().size(); i++) {
                Slot slot = coffre.getSlots().get(i);
                if (slot != null && slot.getItem() != null) {
                    System.out.println("  Slot " + i + ": " + slot.getItem().getNom() + " x" + slot.getQuantity());
                }
            }

            // Déduire le prix de l'achat sur le compte du joueur
            joueur.retirerArgent(prixTotal);

            // Mettre à jour le joueur en base de données SQL
            joueurDAO.mettreAJourJoueur(joueur);

            // Sauvegarder le coffre dans MongoDB
            boolean sauvegardeCoffre = coffreService.sauvegarderCoffre(joueur);
            if (!sauvegardeCoffre) {
                System.err.println("Avertissement: L'achat a réussi mais la sauvegarde du coffre a échoué");
            } else {
                System.out.println("Coffre sauvegardé avec succès dans MongoDB");
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'achat: " + e.getMessage());
            return false;
        }
    }

    /**
     * Recherche un item par son identifiant dans la base MongoDB.
     *
     * @param itemId Identifiant de l'item recherché
     * @return L'objet {@link Item} correspondant, ou null si non trouvé
     */
    private Item trouverItemParId(int itemId) {
        List<Item> itemsMongo = itemMongoDAO.obtenirTousLesItems();
        for (Item item : itemsMongo) {
            if (item.getId() == itemId) {
                return item;
            }
        }
        return null;
    }

    /**
     * Calcule le prix total pour l'achat d'un certain nombre d'items.
     *
     * @param item    L'item acheté
     * @param quantite La quantité désirée
     * @return Le prix total à payer
     */
    private int calculerPrixAchat(Item item, int quantite) {
        return item.getPrix() * quantite;
    }
}
