package be.helha.projects.GuerreDesRoyaumes.TUI;

import be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat.Competence;

public class TestCompetence {

    public static void main(String[] args) {
        // Create a new Competence object
        Competence competence = new Competence("Test", "Test description", 10, 5, 3, 100);

        // Print the details of the competence
        System.out.println("Nom: " + competence.getNom());
        System.out.println("Description: " + competence.getDescription());
        System.out.println("Bonus Vie: " + competence.getBonusVie());
        System.out.println("Bonus Attaque: " + competence.getBonusAttaque());
        System.out.println("Bonus Defense: " + competence.getBonusDefense());
        System.out.println("Bonus Argent: " + competence.getBonusArgent());
    }

}
