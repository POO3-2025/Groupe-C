package be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat;

import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;

/**
 * Classe représentant la compétence "DoubleDegats" dans le jeu Guerre des Royaumes.
 * <p>
 * Cette compétence double les dégâts infligés par le personnage.
 * </p>
 * <p>
 * Elle hérite de la classe {@link Competence} et applique l'effet spécifique
 * via la méthode {@code appliquerEffet} qui modifie les dégâts du personnage.
 * </p>
 */
public class DoubleDegats extends Competence {

    /**
     * Constructeur par défaut.
     * Initialise la compétence avec un identifiant, un nom, un prix et une description.
     */
    public DoubleDegats() {
        super("CompetenceDegats", "Puissance Furieuse", 100, "Double les dégâts infligés par le personnage.");
    }

    /**
     * Applique l'effet de la compétence sur le personnage.
     * <p>
     * Double la valeur des dégâts actuels du personnage.
     * </p>
     *
     * @param personnage Le personnage cible dont les dégâts sont doublés.
     */
    @Override
    public void appliquerEffet(Personnage personnage) {
        personnage.setDegats(personnage.getDegats() * 2);
    }
}
