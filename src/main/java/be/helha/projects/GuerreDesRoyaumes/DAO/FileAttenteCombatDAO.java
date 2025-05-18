package be.helha.projects.GuerreDesRoyaumes.DAO;

import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;

import java.util.List;

/**
 * Interface pour gérer la file d'attente des joueurs prêts à combattre.
 */
public interface FileAttenteCombatDAO {
    
    /**
     * Ajoute un joueur à la file d'attente des combats.
     * 
     * @param joueur Le joueur à ajouter à la file d'attente
     * @return true si l'ajout a réussi, false sinon
     */
    boolean ajouterJoueurEnAttente(Joueur joueur);
    
    /**
     * Vérifie si un joueur est déjà dans la file d'attente.
     * 
     * @param joueurId L'ID du joueur à vérifier
     * @return true si le joueur est en attente, false sinon
     */
    boolean estJoueurEnAttente(int joueurId);
    
    /**
     * Retire un joueur de la file d'attente.
     * 
     * @param joueurId L'ID du joueur à retirer
     * @return true si le retrait a réussi, false sinon
     */
    boolean retirerJoueurEnAttente(int joueurId);
    
    /**
     * Récupère la liste des joueurs en attente de combat.
     * 
     * @return La liste des joueurs en attente
     */
    List<Joueur> obtenirJoueursEnAttente();
    
    /**
     * Cherche un adversaire disponible pour le joueur donné.
     * 
     * @param joueur Le joueur qui cherche un adversaire
     * @return Un adversaire disponible ou null si aucun n'est trouvé
     */
    Joueur trouverAdversaire(Joueur joueur);
    
    /**
     * Met à jour le statut d'un joueur dans la file d'attente.
     * 
     * @param joueurId L'ID du joueur
     * @param statut Le nouveau statut (ex: "EN_ATTENTE", "MATCHMAKING", "EN_COMBAT")
     * @return true si la mise à jour a réussi, false sinon
     */
    boolean mettreAJourStatut(int joueurId, String statut);
    
    /**
     * Vérifie si un match a été trouvé pour le joueur.
     * 
     * @param joueurId L'ID du joueur
     * @return L'ID de l'adversaire si un match a été trouvé, 0 sinon
     */
    int verifierMatchTrouve(int joueurId);
} 