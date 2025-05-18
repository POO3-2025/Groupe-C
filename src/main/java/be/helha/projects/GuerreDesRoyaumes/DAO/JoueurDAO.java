package be.helha.projects.GuerreDesRoyaumes.DAO;

import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;

import java.sql.SQLException;
import java.util.List;

/**
 * Interface définissant les opérations de persistance pour les entités Joueur.
 * Fournit des méthodes CRUD (Create, Read, Update, Delete) pour manipuler les données des joueurs.
 */
public interface JoueurDAO {

    /**
     * Récupère le prochain identifiant disponible pour un joueur.
     *
     * @return Le prochain ID disponible pour un joueur
     * @throws SQLException Si une erreur survient lors de l'accès à la base de données
     */
    int getNextJoueurID() throws SQLException;

    /**
     * Ajoute un nouveau joueur dans la base de données.
     *
     * @param joueur Le joueur à ajouter
     */
    void ajouterJoueur(Joueur joueur);

    /**
     * Récupère un joueur par son identifiant.
     *
     * @param id L'identifiant du joueur à récupérer
     * @return Le joueur correspondant à l'identifiant ou null si aucun joueur n'est trouvé
     */
    Joueur obtenirJoueurParId(int id);

    /**
     * Récupère un joueur par son pseudo.
     *
     * @param pseudo Le pseudo du joueur à récupérer
     * @return Le joueur correspondant au pseudo ou null si aucun joueur n'est trouvé
     */
    Joueur obtenirJoueurParPseudo(String pseudo);

    /**
     * Récupère tous les joueurs enregistrés dans la base de données.
     *
     * @return Une liste de tous les joueurs
     */
    List<Joueur> obtenirTousLesJoueurs();

    /**
     * Récupère tous les joueurs actifs (connectés) enregistrés dans la base de données.
     *
     * @return Une liste de tous les joueurs actifs
     */
    List<Joueur> obtenirJoueursActifs();

    boolean estJoueurActif(int id);

    /**
     * Met à jour les informations d'un joueur existant.
     *
     * @param joueur Le joueur avec les nouvelles informations
     */
    void mettreAJourJoueur(Joueur joueur);

    /**
     * Supprime un joueur de la base de données.
     *
     * @param id L'identifiant du joueur à supprimer
     */
    void supprimerJoueur(int id);

    boolean verifierIdentifiants(String pseudo, String motDePasse);

    /**
     * Définit le statut de connexion d'un joueur.
     *
     * @param id L'identifiant du joueur
     * @param estActif true si le joueur est connecté, false sinon
     */
    void definirStatutConnexion(int id, boolean estActif);

    /**
     * Incrémente le compteur de victoires d'un joueur de 1
     *
     * @param id L'identifiant du joueur
     * @return true si l'opération a réussi, false sinon
     */
    boolean incrementerVictoires(int id);

    /**
     * Incrémente le compteur de défaites d'un joueur de 1
     *
     * @param id L'identifiant du joueur
     * @return true si l'opération a réussi, false sinon
     */
    boolean incrementerDefaites(int id);
}