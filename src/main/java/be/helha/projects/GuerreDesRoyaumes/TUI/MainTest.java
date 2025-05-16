package be.helha.projects.GuerreDesRoyaumes.TUI;

import be.helha.projects.GuerreDesRoyaumes.Model.Combat.ActionCombatSimple;
import be.helha.projects.GuerreDesRoyaumes.Model.Combat.ActionCombatSimple.TypeAction;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Arme;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Bouclier;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Golem;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Titan;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainTest {

    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        Golem golem = new Golem();
        Titan titan = new Titan();

        Arme epee = new Arme(1, "Épée", 1, 1000, 15);
        Bouclier bouclier = new Bouclier(2, "Bouclier en acier", 1, 500, 5);

        // Boucle sur 5 tours max
        for (int tour = 1; tour <= 5; tour++) {
            System.out.println("\n========= TOUR " + tour + " =========");

            List<ActionCombatSimple> actions = new ArrayList<>();

            // ----- Choix du joueur pour le Titan -----
            System.out.println("\n--- Choix pour TITAN ---");
            System.out.println("1. Attaquer");
            System.out.println("2. Équiper le bouclier");
            int choixTitan = scanner.nextInt();

            if (choixTitan == 1) {
                actions.add(new ActionCombatSimple(TypeAction.ATTAQUER, titan, golem, null, null));
            } else if (choixTitan == 2) {
                actions.add(new ActionCombatSimple(TypeAction.EQUIPER_BOUCLIER, titan, null, null, bouclier));
            } else {
                System.out.println("Choix invalide pour Titan. Il ne fait rien.");
            }

            // ----- Choix du joueur pour le Golem -----
            System.out.println("\n--- Choix pour GOLEM ---");
            System.out.println("1. Attaquer");
            System.out.println("2. Équiper le bouclier (inutile ici)");
            int choixGolem = scanner.nextInt();

            if (choixGolem == 1) {
                actions.add(new ActionCombatSimple(TypeAction.ATTAQUER, golem, titan, epee, null));
            } else if (choixGolem == 2) {
                System.out.println("Le golem n'a pas de bouclier... action ignorée.");
            } else {
                System.out.println("Choix invalide pour le golem. Il ne fait rien.");
            }

            // Résolution automatique
            resoudreTour(actions);

            // Afficher l'état après le tour
            afficherEtat(titan, golem);

            // Fin anticipée si l’un est mort
            if (titan.getVie() <= 0 || golem.getVie() <= 0) {
                break;
            }
        }

        // Détermination du gagnant
        System.out.println("\n========= FIN DU COMBAT =========");

        if (titan.getVie() <= 0 && golem.getVie() <= 0) {
            System.out.println("Égalité parfaite ! Les deux combattants sont tombés.");
        } else if (titan.getVie() <= 0) {
            System.out.println("Victoire du Golem !");
        } else if (golem.getVie() <= 0) {
            System.out.println("Victoire du Titan !");
        } else {
            if (titan.getVie() > golem.getVie()) {
                System.out.println("Temps écoulé. Victoire du Titan avec " + titan.getVie() + " PV !");
            } else if (golem.getVie() > titan.getVie()) {
                System.out.println("Temps écoulé. Victoire du Golem avec " + golem.getVie() + " PV !");
            } else {
                System.out.println("Match nul après 5 tours !");
            }
        }

        scanner.close();
    }

    public static void resoudreTour(List<ActionCombatSimple> actions) {
        actions.stream()
                .filter(a -> a.getType() == TypeAction.EQUIPER_BOUCLIER)
                .forEach(MainTest::resoudreAction);

        actions.stream()
                .filter(a -> a.getType() == TypeAction.ATTAQUER)
                .forEach(MainTest::resoudreAction);
    }

    public static void resoudreAction(ActionCombatSimple action) {
        switch (action.getType()) {
            case ATTAQUER:
                action.getActeur().attaquer(action.getCible(), action.getArme());
                break;
            case EQUIPER_BOUCLIER:
                action.getBouclier().use(action.getActeur());
                break;
        }
    }

    public static void afficherEtat(Personnage titan, Personnage golem) {
        System.out.println("\nÉTAT ACTUEL :");
        System.out.println("Titan → Vie : " + titan.getVie() + " | Défense : " + titan.getDefense());
        System.out.println("Golem → Vie : " + golem.getVie() + " | Défense : " + golem.getDefense());
    }
}
