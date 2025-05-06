package be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat;

import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;

public class DoubleResistance extends Competence {
    public DoubleResistance() {
        super("C2", "Peau de Fer", 100, "Double la résistance du personnage.");
    }

    @Override
    public void appliquerEffet(Personnage personnage) {
        personnage.setResistance(personnage.getResistance() * 2);
    }
}
