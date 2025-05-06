package be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat;

import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;

public class DoubleArgent extends Competence {
    public DoubleArgent() {
        super("C3", "Avarice Bénie", 100, "Double l'argent gagné après le combat.");
    }

    @Override
    public void appliquerEffet(Personnage personnage) {
        // Ce bonus ne change pas les stats, mais devra être appliqué lors du gain de récompense
        // Ex : dans le code qui donne l'argent après combat, tu vérifies si cette compétence est active et tu fait *2 a this.argent ca devrait ressembler a ca.
    }
}
