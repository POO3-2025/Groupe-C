package be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat;

import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;

/**
 * Classe représentant la compétence "DoubleArgent" dans le jeu Guerre des Royaumes.
 * <p>
 * Cette compétence double l'argent gagné par le joueur après un combat.
 * </p>
 * <p>
 * Elle hérite de la classe {@link Competence} et implémente l'effet spécifique
 * via la méthode {@code appliquerEffet}.
 * </p>
 * <p>
 * Note : Cette compétence n'affecte pas directement les statistiques du personnage,
 * mais doit être prise en compte lors du calcul des récompenses monétaires.
 * </p>
 */
public class DoubleArgent extends Competence {

    /**
     * Constructeur par défaut.
     * Initialise la compétence avec un identifiant, un nom, un prix et une description.
     */
    public DoubleArgent() {
        super("CompetenceDouble", "Avarice Bénie", 100, "Double l'argent gagné après le combat.");
    }

    /**
     * Applique l'effet de la compétence sur le personnage.
     * <p>
     * Ici, cette méthode est vide car l'effet est appliqué
     * lors du calcul des gains d'argent après combat.
     * </p>
     *
     * @param personnage Le personnage cible (non modifié directement).
     */
    @Override
    public void appliquerEffet(Personnage personnage) {
        // Effet à gérer lors du gain d'argent après combat
    }
}
