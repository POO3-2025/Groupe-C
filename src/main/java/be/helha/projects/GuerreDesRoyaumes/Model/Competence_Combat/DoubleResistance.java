package be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat;

import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;

/**
 * Classe représentant la compétence "DoubleResistance" dans le jeu Guerre des Royaumes.
 * <p>
 * Cette compétence double la résistance aux dégâts du personnage.
 * </p>
 * <p>
 * Elle hérite de la classe {@link Competence} et applique l'effet spécifique
 * via la méthode {@code appliquerEffet} qui modifie la résistance du personnage.
 * </p>
 */
public class DoubleResistance extends Competence {

    /**
     * Constructeur par défaut.
     * Initialise la compétence avec un identifiant, un nom, un prix et une description.
     */
    public DoubleResistance() {
        super("CompetenceResistance", "Peau de Fer", 100, "Double la résistance du personnage.");
    }

    /**
     * Applique l'effet de la compétence sur le personnage.
     * <p>
     * Double la valeur actuelle de la résistance du personnage.
     * </p>
     *
     * @param personnage Le personnage cible dont la résistance est doublée.
     */
    @Override
    public void appliquerEffet(Personnage personnage) {
        personnage.setResistance(personnage.getResistance() * 2);
    }
}
