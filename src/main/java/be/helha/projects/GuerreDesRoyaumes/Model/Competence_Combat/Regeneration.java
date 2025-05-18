package be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat;

import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;

/**
 * Classe représentant la compétence "Régénération" dans le jeu Guerre des Royaumes.
 * <p>
 * Cette compétence permet au personnage de récupérer 5 points de vie à chaque tour de combat.
 * </p>
 * <p>
 * Elle hérite de la classe {@link Competence} et doit appliquer son effet via la méthode {@code appliquerEffet}.
 * </p>
 * <p>
 * Note : L'effet de régénération doit être géré dans la boucle de combat, par exemple
 * en activant un flag sur le personnage ou via un contrôleur dédié.
 * </p>
 */
public class Regeneration extends Competence {

    /**
     * Constructeur par défaut.
     * Initialise la compétence avec un identifiant, un nom, un prix et une description.
     */
    public Regeneration() {
        super("CompetenceRegen", "Régénération", 120, "Rend 5 points de vie à chaque tour.");
    }

    /**
     * Applique l'effet de la compétence sur le personnage.
     * <p>
     * Cette méthode est prévue pour activer un effet de régénération
     * à gérer dans la boucle de combat.
     * </p>
     *
     * @param personnage Le personnage cible.
     */
    @Override
    public void appliquerEffet(Personnage personnage) {
        // Exemple d'activation d'un flag ou autre mécanisme dans la logique de combat
        // personnage.setRegenActive(true);
    }
}
