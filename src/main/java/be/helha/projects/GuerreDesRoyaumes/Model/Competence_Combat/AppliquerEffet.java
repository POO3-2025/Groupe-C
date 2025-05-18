package be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat;

import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;

/**
 * Interface représentant un effet pouvant être appliqué à un personnage.
 * <p>
 * Toute compétence implémentant cette interface doit définir la méthode
 * {@code appliquerEffet} qui modifie l'état d'un personnage selon l'effet spécifique.
 * </p>
 */
public interface AppliquerEffet {

    /**
     * Applique l'effet de la compétence au personnage passé en paramètre.
     *
     * @param personnage Le personnage sur lequel appliquer l'effet.
     */
    void appliquerEffet(Personnage personnage);
}
