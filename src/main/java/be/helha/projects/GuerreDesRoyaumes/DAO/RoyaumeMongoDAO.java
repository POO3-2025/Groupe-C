package be.helha.projects.GuerreDesRoyaumes.DAO;

import be.helha.projects.GuerreDesRoyaumes.Model.Royaume;
import java.util.List;

/**
 * Interface définissant les opérations de persistance pour les entités Royaume dans MongoDB.
 * Fournit des méthodes CRUD pour manipuler les documents Royaume.
 */
public interface RoyaumeMongoDAO {
    /**
     * Ajoute un nouveau royaume dans la collection MongoDB.
     *
     * @param royaume Le royaume à ajouter
     * @param joueurId L'identifiant du joueur propriétaire du royaume
     */
    void ajouterRoyaume(Royaume royaume, int joueurId);

    /**
     * Récupère un royaume par l'identifiant du joueur.
     *
     * @param joueurId L'identifiant du joueur
     * @return Le royaume correspondant au joueur ou null si aucun royaume n'est trouvé
     */
    Royaume obtenirRoyaumeParJoueurId(int joueurId);

    /**
     * Met à jour les informations d'un royaume existant.
     *
     * @param royaume Le royaume avec les nouvelles informations
     * @param joueurId L'identifiant du joueur propriétaire du royaume
     */
    void mettreAJourRoyaume(Royaume royaume, int joueurId);

    /**
     * Supprime un royaume de la collection MongoDB.
     *
     * @param joueurId L'identifiant du joueur propriétaire du royaume
     */
    void supprimerRoyaume(int joueurId);
} 