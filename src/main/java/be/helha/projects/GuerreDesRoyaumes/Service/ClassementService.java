package be.helha.projects.GuerreDesRoyaumes.Service;

import java.util.List;
import java.util.Map;

/**
 * Interface définissant les services de classement pour le jeu Guerre des Royaumes.
 * <p>
 * Elle fournit des méthodes pour obtenir différents classements basés sur
 * les statistiques des joueurs et des royaumes.
 * </p>
 */
public interface ClassementService {

    /**
     * Obtient un classement des joueurs basé sur leur nombre de victoires et défaites.
     * <p>
     * Chaque élément de la liste est une map contenant les informations pertinentes
     * (ex: pseudo, victoires, défaites).
     * </p>
     *
     * @return Une liste de maps représentant le classement victoires/défaites.
     */
    List<Map<String, Object>> getClassementVictoiresDefaites();

    /**
     * Obtient un classement des joueurs basé sur leur richesse (argent).
     * <p>
     * Chaque élément de la liste est une map contenant les informations pertinentes
     * (ex: pseudo, montant d'argent).
     * </p>
     *
     * @return Une liste de maps représentant le classement de la richesse.
     */
    List<Map<String, Object>> getClassementRichesse();

    /**
     * Obtient un classement des royaumes basé sur leur niveau.
     * <p>
     * Chaque élément de la liste est une map contenant les informations pertinentes
     * (ex: nom du royaume, niveau).
     * </p>
     *
     * @return Une liste de maps représentant le classement des niveaux de royaumes.
     */
    List<Map> getClassementNiveauRoyaumes();
}
