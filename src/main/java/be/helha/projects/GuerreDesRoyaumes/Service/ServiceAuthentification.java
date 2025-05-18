package be.helha.projects.GuerreDesRoyaumes.Service;

import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Model.Royaume;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;

public interface ServiceAuthentification {
    void inscrireJoueur(String nom, String prenom, String pseudo, String motDePasse);
    boolean authentifierJoueur(String pseudo, String motDePasse);
    void gererProfil(int id, String pseudo, String motDePasse);
    void choisirPersonnage(int joueurId, int personnageId);
    void initialiserJoueur(String pseudo, Royaume royaume, Personnage personnage);

    // Méthodes ajoutées
    Joueur obtenirJoueurParPseudo(String pseudo);
    void mettreAJourJoueur(Joueur joueur);
    
    /**
     * Récupère le personnage d'un joueur par son ID.
     *
     * @param idJoueur L'ID du joueur
     * @return Le personnage du joueur ou null si non trouvé
     */
    Personnage obtenirPersonnage(int idJoueur);

    /**
     * Connecte un joueur (définit son statut comme actif).
     *
     * @param pseudo Le pseudo du joueur à connecter
     * @return true si la connexion a réussi, false sinon
     */
    boolean connecterJoueur(String pseudo);

    /**
     * Déconnecte un joueur (définit son statut comme inactif).
     *
     * @param pseudo Le pseudo du joueur à déconnecter
     * @return true si la déconnexion a réussi, false sinon
     */
    boolean deconnecterJoueur(String pseudo);
}