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

@Service
public class ServiceBoutiqueImpl implements ServiceBoutique {

    private ItemMongoDAO itemMongoDAO;
    private JoueurDAO joueurDAO;
    private CoffreService coffreService;
    private static final int PRIX_BASE = 100;
    private static ServiceBoutiqueImpl instance;

    public ServiceBoutiqueImpl() {
        this.itemMongoDAO = ItemMongoDAOImpl.getInstance();
        this.joueurDAO = JoueurDAOImpl.getInstance();
        this.coffreService = CoffreServiceMongoImpl.getInstance();
    }

    public static synchronized ServiceBoutiqueImpl getInstance() {
        if (instance == null) {
            instance = new ServiceBoutiqueImpl();
        }
        return instance;
    }

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

            // IMPORTANT: Toujours recharger le coffre depuis MongoDB pour éviter d'écraser les achats précédents
            coffreService.chargerCoffre(joueur);
            
            // Vérifier que le coffre est bien présent
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

            // Ajouter l'item à l'inventaire
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

            // Déduire le prix
            joueur.retirerArgent(prixTotal);

            // Persister les changements en base de données
            joueurDAO.mettreAJourJoueur(joueur);

            // Sauvegarder les items du coffre avec CoffreServiceMongo
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
     * Trouve un item par son ID en cherchant dans MongoDB
     * 
     * @param itemId L'ID de l'item à rechercher
     * @return L'item trouvé ou null
     */
    private Item trouverItemParId(int itemId) {
        // Chercher dans tous les items de MongoDB
        List<Item> itemsMongo = itemMongoDAO.obtenirTousLesItems();
        for (Item item : itemsMongo) {
            if (item.getId() == itemId) {
                return item;
            }
        }
        
        // Si non trouvé
        return null;
    }

    private int calculerPrixAchat(Item item, int quantite) {
        // Logique de calcul du prix d'achat
        return item.getPrix() * quantite;
    }
}

