package be.helha.projects.GuerreDesRoyaumes.ServiceImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.ItemDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.ItemMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.JoueurDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Coffre;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Service.CoffreService;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceBoutique;
import org.springframework.stereotype.Service;

@Service
public class ServiceBoutiqueImpl implements ServiceBoutique {

    private ItemDAO itemDAO;
    private JoueurDAO joueurDAO;
    private CoffreService coffreService;
    private static final int PRIX_BASE = 100;
    private static ServiceBoutiqueImpl instance;

    public ServiceBoutiqueImpl() {
        this.itemDAO = ItemMongoDAOImpl.getInstance();
        this.joueurDAO = JoueurDAOImpl.getInstance();
        this.coffreService = CoffreServiceImpl.getInstance();
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
            Joueur joueur = joueurDAO.obtenirJoueurParId(joueurId);
            if (joueur == null) {
                throw new IllegalArgumentException("Joueur non trouvé");
            }

            Item item = itemDAO.obtenirItemParId(itemId);
            if (item == null) {
                throw new IllegalArgumentException("Item non trouvé");
            }

            // Calculer le prix total
            int prixTotal = calculerPrixAchat(item, quantite);

            // Vérifier si le joueur a assez d'argent
            if (joueur.getArgent() < prixTotal) {
                throw new IllegalArgumentException("Fonds insuffisants");
            }

            // Ajouter l'item à l'inventaire
            Coffre coffre = joueur.getCoffre();
            if (coffre == null) {
                throw new IllegalArgumentException("Inventaire non trouvé");
            }

            boolean ajoutReussi = coffre.ajouterItem(item, quantite);
            if (!ajoutReussi) {
                throw new IllegalArgumentException("Coffre plein ou quantité trop élevée");
            }

            // Déduire le prix
            joueur.retirerArgent(prixTotal);

            // Persister les changements en base de données
            joueurDAO.mettreAJourJoueur(joueur);

            // Sauvegarder les items du coffre avec le nouveau CoffreService
            coffreService.sauvegarderCoffre(joueur);

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private int calculerPrixAchat(Item item, int quantite) {
        // Logique de calcul du prix d'achat
        return item.getPrix() * quantite;
    }
}

