package be.helha.projects.GuerreDesRoyaumes.Service;

import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;

/**
 * Interface définissant les services de gestion des coffres des joueurs
 * dans le jeu Guerre des Royaumes.
 * <p>
 * Cette interface gère les opérations de sauvegarde, chargement et vidage
 * des coffres des joueurs dans la base de données.
 * </p>
 */
public interface CoffreService {

    /**
     * Sauvegarde les items du coffre d'un joueur dans la base de données.
     *
     * @param joueur Le joueur dont le coffre sera sauvegardé.
     * @return true si la sauvegarde a réussi, false sinon.
     */
    boolean sauvegarderCoffre(Joueur joueur);

    /**
     * Charge les items du coffre d'un joueur depuis la base de données.
     *
     * @param joueur Le joueur dont le coffre sera chargé.
     * @return true si le chargement a réussi, false sinon.
     */
    boolean chargerCoffre(Joueur joueur);

    /**
     * Vide le coffre d'un joueur dans la base de données.
     *
     * @param joueur Le joueur dont le coffre sera vidé.
     * @return true si l'opération a réussi, false sinon.
     */
    boolean viderCoffre(Joueur joueur);
}
