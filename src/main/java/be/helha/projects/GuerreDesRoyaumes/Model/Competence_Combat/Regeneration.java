package be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat;

import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;

public class Regeneration extends Competence {
    public Regeneration() {
        super("CompetenceRegen", "Régénération", 120, "Rend 5 points de vie à chaque tour.");
    }

    @Override
    public void appliquerEffet(Personnage personnage) {
        // Idéalement, on attache un effet dans la boucle de combat
        // Exemple : personnage.setRegenActive(true); ou ajouter une logique dans un contrôleur de combat
    }
}
