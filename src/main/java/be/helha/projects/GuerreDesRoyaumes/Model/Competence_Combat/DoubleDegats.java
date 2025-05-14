package be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat;

import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;

public class DoubleDegats extends Competence {
    public DoubleDegats() {
        super("CompetenceDegats", "Puissance Furieuse", 100, "Double les dégâts infligés par le personnage.");
    }

    @Override
    public void appliquerEffet(Personnage personnage) {
        personnage.setDegats(personnage.getDegats() * 2);
    }
}

