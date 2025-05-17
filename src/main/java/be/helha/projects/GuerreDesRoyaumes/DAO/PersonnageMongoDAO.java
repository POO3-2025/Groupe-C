package be.helha.projects.GuerreDesRoyaumes.DAO;

import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;
import java.util.List;

/**
 * Interface définissant les opérations de persistance pour les entités Personnage dans MongoDB.
 * Fournit des méthodes CRUD pour manipuler les documents Personnage.
 */
public interface PersonnageMongoDAO {
    /**
     * Ajoute un nouveau personnage dans la collection MongoDB.
     *
     * @param personnage Le personnage à ajouter
     * @param joueurId L'identifiant du joueur propriétaire du personnage
     */
    void ajouterPersonnage(Personnage personnage, int joueurId);

    /**
     * Récupère un personnage par l'identifiant du joueur.
     *
     * @param joueurId L'identifiant du joueur
     * @return Le personnage correspondant au joueur ou null si aucun personnage n'est trouvé
     */
    Personnage obtenirPersonnageParJoueurId(int joueurId);

    /**
     * Met à jour les informations d'un personnage existant.
     *
     * @param personnage Le personnage avec les nouvelles informations
     * @param joueurId L'identifiant du joueur propriétaire du personnage
     */
    void mettreAJourPersonnage(Personnage personnage, int joueurId);

    /**
     * Supprime un personnage de la collection MongoDB.
     *
     * @param joueurId L'identifiant du joueur propriétaire du personnage
     */
    void supprimerPersonnage(int joueurId);
} 