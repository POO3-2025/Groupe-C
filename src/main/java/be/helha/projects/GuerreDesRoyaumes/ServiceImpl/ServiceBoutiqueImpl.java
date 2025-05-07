package be.helha.projects.GuerreDesRoyaumes.ServiceImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.ItemDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceBoutique;

import java.util.List;

public class ServiceBoutiqueImpl implements ServiceBoutique {

    private ItemDAO itemDAO;
    private JoueurDAO joueurDAO;
    private static final int PRIX_BASE = 100;

    public ServiceBoutiqueImpl() {
        this.itemDAO = itemDAO;
        this.joueurDAO = joueurDAO;
    }

    @Override
    public List<Item> obtenirTousLesItems() {
        return itemDAO.obtenirTousLesItems();
    }

    @Override
    public Item obtenirItemParId(int id) {
        return itemDAO.obtenirItemParId(id);
    }

    @Override
    public List<Item> obtenirItemsParType(String type) {
        return itemDAO.obtenirItemsParType(type);
    }

    @Override
    public boolean acheterItem(int joueurId, int itemId, int quantite) {
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
        Inventaire inventaire = joueur.getInventaire();
        if (inventaire == null) {
            throw new IllegalArgumentException("Inventaire non trouvé");
        }

        boolean ajoutReussi = inventaire.ajouterItem(item, quantite);
        if (!ajoutReussi) {
            throw new IllegalArgumentException("Inventaire plein ou quantité trop élevée");
        }

        // Déduire le prix
        joueur.retirerArgent(prixTotal);

        // Persister les changements
        joueurDAO.mettreAJourJoueur(joueur);

        return true;
    }

    private int calculerPrixAchat(Item item, int quantite) {
        // Logique de calcul du prix d'achat
        return PRIX_BASE * quantite;
    }
}

