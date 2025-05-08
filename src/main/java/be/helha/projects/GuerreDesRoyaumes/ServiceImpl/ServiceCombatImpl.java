package be.helha.projects.GuerreDesRoyaumes.ServiceImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.CombatDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.ItemDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Combat;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Slot;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceCombat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ServiceCombatImpl implements ServiceCombat {

    private CombatDAO combatDAO;
    private JoueurDAO joueurDAO;
    private ItemDAO itemDAO;
    private Random random;

    public ServiceCombatImpl(CombatDAO combatDAO, JoueurDAO joueurDAO, ItemDAO itemDAO) {
        this.combatDAO = combatDAO;
        this.joueurDAO = joueurDAO;
        this.itemDAO = itemDAO;
        this.random = new Random();
    }


    @Override
    public boolean choisirItemsPourCombat(int joueurId, List<Integer> itemIds) {
        if (itemIds.size() > 3) {
            throw new IllegalArgumentException("Maximum 3 items autorisés pour le combat");
        }

        Joueur joueur = joueurDAO.obtenirJoueurParId(joueurId);
        if (joueur == null) {
            throw new IllegalArgumentException("Joueur non trouvé");
        }

        // On suppose que la vérification des items est effectuée par l'interface utilisateur
        // ou sera implémentée ultérieurement via un autre mécanisme

        // Vérifier simplement que les items existent dans la base de données
        for (Integer itemId : itemIds) {
            Item item = itemDAO.obtenirItemParId(itemId);
            if (item == null) {
                throw new IllegalArgumentException("L'item avec l'ID " + itemId + " n'existe pas");
            }
        }

        // Les items sont valides
        return true;
    }

    @Override
    public boolean lancerCombat(int joueurId, int adversaireId, List<Integer> itemsChoisis) {
        Joueur joueur = joueurDAO.obtenirJoueurParId(joueurId);
        Joueur adversaire = joueurDAO.obtenirJoueurParId(adversaireId);

        if (joueur == null || adversaire == null) {
            throw new IllegalArgumentException("Joueur ou adversaire non trouvé");
        }

        // Vérifier les items choisis
        if (!choisirItemsPourCombat(joueurId, itemsChoisis)) {
            throw new IllegalArgumentException("Sélection d'items invalide");
        }

        // Calcul basique du résultat du combat
        int nbTours = 0;
        int forceJoueur = calculerForce(joueur, itemsChoisis);
        int forceAdversaire = calculerForce(adversaire, new ArrayList<>()); // L'adversaire n'a pas choisi d'items pour simplifier

        // Simulation du combat
        boolean victoire = forceJoueur > forceAdversaire;

        // Créer l'enregistrement du combat
        Combat combat = new Combat(0, nbTours, victoire, joueur);
        combatDAO.ajouterCombat(combat);

        // Gérer les conséquences du combat (argent, territoires, etc.)
        if (victoire) {
            // Le joueur gagne de l'argent
            joueur.ajouterArgent(500);

            // Mettre à jour le joueur
            joueurDAO.mettreAJourJoueur(joueur);
        }

        return victoire;
    }

    private int calculerForce(Joueur joueur, List<Integer> itemIds) {
        int force = 0;

        // Ajouter la force du personnage
        Personnage personnage = joueur.getPersonnage();
        if (personnage != null) {
            force += personnage.getDegats();
        }

        // Ajouter le niveau du royaume
        if (joueur.getRoyaume() != null) {
            force += joueur.getRoyaume().getNiveau() * 50;
        }

        // Ajouter les bonus des items
        for (Integer itemId : itemIds) {
            Item item = itemDAO.obtenirItemParId(itemId);
            if (item != null) {
                // Ajouter un bonus basé sur l'item
                force += 100; // Valeur arbitraire pour l'exemple
            }
        }

        // Ajouter un facteur aléatoire
        force += random.nextInt(100);

        return force;
    }

    @Override
    public void distribuerRecompenses(int combatId) {
        Combat combat = combatDAO.obtenirCombatParId(combatId);
        if (combat == null) {
            throw new IllegalArgumentException("Combat non trouvé");
        }

        // Vérifier si le combat est une victoire
        if (combat.isVictoire()) {
            Joueur joueur = combat.getJoueur();
            if (joueur != null) {
                // Ajouter de l'argent
                joueur.ajouterArgent(300);

                // Augmenter le niveau du royaume du joueur
                if (joueur.getRoyaume() != null) {
                    int niveauActuel = joueur.getRoyaume().getNiveau();
                    joueur.getRoyaume().setNiveau(niveauActuel + 1);
                    System.out.println("Le royaume de " + joueur.getRoyaume().getNom() +
                            " est passé au niveau " + joueur.getRoyaume().getNiveau() + " !");
                }

                // Mettre à jour le joueur
                joueurDAO.mettreAJourJoueur(joueur);
            }
        }
    }
}
