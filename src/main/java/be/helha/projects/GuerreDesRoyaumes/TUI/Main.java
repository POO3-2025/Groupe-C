package be.helha.projects.GuerreDesRoyaumes.TUI;

import be.helha.projects.GuerreDesRoyaumes.Model.*;
import be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat.Competence;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Golem;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Voleur;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Demander le nom du joueur
        System.out.print("Entrez le nom de votre joueur : ");
        String joueurNom = scanner.nextLine();

        // Créer le joueur
        Joueur joueur = new Joueur(joueurNom);
        System.out.println("Bienvenue, " + joueur.getNom() + " !");

        // Choisir le personnage
        System.out.println("\nChoisissez un personnage : ");
        System.out.println("1 - Guerrier");
        System.out.println("2 - Mage");
        System.out.print("Entrez le numéro du personnage : ");
        int choixPersonnage = scanner.nextInt();
        scanner.nextLine();  // Pour consommer le '\n' restant après nextInt()

        Personnage personnage = null;
        Competence competence = null;
        Item arme = null;

        // Affecter le personnage, l'arme et la compétence en fonction du choix
        switch (choixPersonnage) {
            case 1: // Guerrier
                personnage = new Voleur("Arthur", "Un guerrier féroce avec une grande hache.");
                break;
            case 2: // Mage
                personnage = new Golem("Merlin", "Un mage puissant maîtrisant les éléments.");
                break;
            default:
                System.out.println("Choix invalide.");
                return;  // Fin du programme si choix invalide
        }

        // Affecter l'arme et la compétence au personnage
        personnage.getInventaire().ajouterItem(arme);  // Ajout de l'arme à l'inventaire du personnage

        // Afficher les détails du personnage
        System.out.println("\nPersonnage choisi : " + personnage.getNom());
        System.out.println("Description : " + personnage.getDescription());
        System.out.println("Compétence bonus : " + competence.getNom() + " - " + competence.getDescription());
        personnage.getInventaire().afficherInventaire();

        // Simuler une attaque avec l'arme
        System.out.println("\nLe personnage attaque !");
        personnage.attaquer();
    }
}
