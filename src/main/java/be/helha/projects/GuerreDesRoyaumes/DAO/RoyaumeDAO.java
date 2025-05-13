package be.helha.projects.GuerreDesRoyaumes.DAO;

import be.helha.projects.GuerreDesRoyaumes.Model.Royaume;
import java.util.List;

/**
 * Interface définissant les opérations de persistance pour les entités Royaume.
 * Fournit des méthodes CRUD (Create, Read, Update, Delete) pour manipuler les données des royaumes.
 */
public interface RoyaumeDAO {
    /**
     * Ajoute un nouveau royaume dans la base de données.
     *
     * @param royaume Le royaume à ajouter
     */
    void ajouterRoyaume(Royaume royaume);

    /**
     * Récupère un royaume par son identifiant.
     *
     * @param id L'identifiant du royaume à récupérer
     * @return Le royaume correspondant à l'identifiant ou null si aucun royaume n'est trouvé
     */
    Royaume obtenirRoyaumeParId(int id);

    /**
     * Récupère tous les royaumes enregistrés dans la base de données.
     *
     * @return Une liste de tous les royaumes
     */
    List<Royaume> obtenirTousLesRoyaumes();

    /**
     * Récupère tous les royaumes d'un joueur spécifique.
     *
     * @param joueurId L'identifiant du joueur
     * @return Une liste des royaumes du joueur
     */
    List<Royaume> obtenirRoyaumesParJoueurId(int joueurId);

    /**
     * Met à jour les informations d'un royaume existant.
     *
     * @param royaume Le royaume avec les nouvelles informations
     */
    void mettreAJourRoyaume(Royaume royaume);

    /**
     * Supprime un royaume de la base de données.
     *
     * @param id L'identifiant du royaume à supprimer
     */
    void supprimerRoyaume(int id);
}
