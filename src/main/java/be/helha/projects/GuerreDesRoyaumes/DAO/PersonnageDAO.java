package be.helha.projects.GuerreDesRoyaumes.DAO;

import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;
import java.util.List;

/**
 * Interface définissant les opérations de persistance pour les entités Personnage.
 * Fournit des méthodes CRUD (Create, Read, Update, Delete) pour manipuler les données des personnages.
 */
public interface PersonnageDAO {
    /**
     * Ajoute un nouveau personnage dans la base de données.
     *
     * @param personnage Le personnage à ajouter
     */
    void ajouterPersonnage(Personnage personnage);

    /**
     * Récupère un personnage par son identifiant.
     *
     * @param id L'identifiant du personnage à récupérer
     * @return Le personnage correspondant à l'identifiant ou null si aucun personnage n'est trouvé
     */
    Personnage obtenirPersonnageParId(int id);

    /**
     * Récupère tous les personnages enregistrés dans la base de données.
     *
     * @return Une liste de tous les personnages
     */
    List<Personnage> obtenirTousLesPersonnages();

    /**
     * Met à jour les informations d'un personnage existant.
     *
     * @param personnage Le personnage avec les nouvelles informations
     */
    void mettreAJourPersonnage(Personnage personnage);

    /**
     * Supprime un personnage de la base de données.
     *
     * @param id L'identifiant du personnage à supprimer
     */
    void supprimerPersonnage(int id);
}