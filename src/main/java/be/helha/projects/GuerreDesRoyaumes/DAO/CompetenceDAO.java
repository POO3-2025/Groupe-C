package be.helha.projects.GuerreDesRoyaumes.DAO;

import be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat.Competence;
import java.util.List;

/**
 * Interface définissant les opérations de persistance pour les entités Competence.
 * Fournit des méthodes CRUD (Create, Read, Update, Delete) pour manipuler les données des compétences.
 */
public interface CompetenceDAO {
    /**
     * Ajoute une nouvelle compétence dans la base de données.
     *
     * @param competence La compétence à ajouter
     */
    void addCompetence(Competence competence);

    /**
     * Récupère une compétence par son identifiant.
     *
     * @param id L'identifiant de la compétence à récupérer
     * @return La compétence correspondant à l'identifiant ou null si aucune compétence n'est trouvée
     */
    Competence getCompetenceById(int id);

    /**
     * Récupère toutes les compétences enregistrées dans la base de données.
     *
     * @return Une liste de toutes les compétences
     */
    List<Competence> getAllCompetences();

    /**
     * Met à jour les informations d'une compétence existante.
     *
     * @param competence La compétence avec les nouvelles informations
     */
    void updateCompetence(Competence competence);

    /**
     * Supprime une compétence de la base de données.
     *
     * @param id L'identifiant de la compétence à supprimer
     */
    void deleteCompetence(int id);
}
