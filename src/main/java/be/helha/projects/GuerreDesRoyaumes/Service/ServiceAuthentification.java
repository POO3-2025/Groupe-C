package be.helha.projects.GuerreDesRoyaumes.Service;

import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Model.Royaume;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;

/**
 * Interface définissant les services d'authentification et de gestion
 * des profils des joueurs dans le jeu Guerre des Royaumes.
 * <p>
 * Cette interface inclut l'inscription, l'authentification, la gestion du profil,
 * le choix du personnage, et la gestion du statut de connexion.
 * </p>
 */
public interface ServiceAuthentification {

    /**
     * Inscrit un nouveau joueur avec les informations fournies.
     *
     * @param nom         Nom du joueur.
     * @param prenom      Prénom du joueur.
     * @param pseudo      Pseudonyme unique du joueur.
     * @param motDePasse  Mot de passe du joueur.
     */
    void inscrireJoueur(String nom, String prenom, String pseudo, String motDePasse);

    /**
     * Authentifie un joueur avec son pseudo et mot de passe.
     *
     * @param pseudo      Pseudonyme du joueur.
     * @param motDePasse  Mot de passe du joueur.
     * @return true si l'authentification est réussie, false sinon.
     */
    boolean authentifierJoueur(String pseudo, String motDePasse);

    /**
     * Gère la modification du profil d'un joueur.
     *
     * @param id          Identifiant unique du joueur.
     * @param pseudo      Nouveau pseudonyme.
     * @param motDePasse  Nouveau mot de passe.
     */
    void gererProfil(int id, String pseudo, String motDePasse);

    /**
     * Permet à un joueur de choisir un personnage.
     *
     * @param joueurId      Identifiant du joueur.
     * @param personnageId  Identifiant du personnage choisi.
     */
    void choisirPersonnage(int joueurId, int personnageId);

    /**
     * Initialise un joueur avec un royaume et un personnage.
     *
     * @param pseudo      Pseudonyme du joueur.
     * @param royaume     Royaume associé au joueur.
     * @param personnage  Personnage associé au joueur.
     */
    void initialiserJoueur(String pseudo, Royaume royaume, Personnage personnage);

    /**
     * Récupère un joueur par son pseudonyme.
     *
     * @param pseudo Pseudonyme du joueur.
     * @return Le joueur correspondant, ou null s'il n'existe pas.
     */
    Joueur obtenirJoueurParPseudo(String pseudo);

    /**
     * Met à jour les informations d'un joueur.
     *
     * @param joueur Le joueur avec les informations mises à jour.
     */
    void mettreAJourJoueur(Joueur joueur);

    /**
     * Connecte un joueur en définissant son statut comme actif.
     *
     * @param pseudo Pseudonyme du joueur à connecter.
     * @return true si la connexion a réussi, false sinon.
     */
    boolean connecterJoueur(String pseudo);

    /**
     * Déconnecte un joueur en définissant son statut comme inactif.
     *
     * @param pseudo Pseudonyme du joueur à déconnecter.
     * @return true si la déconnexion a réussi, false sinon.
     */
    boolean deconnecterJoueur(String pseudo);
}
