package be.helha.projects.GuerreDesRoyaumes.DAO;

import be.helha.projects.GuerreDesRoyaumes.Model.Combat.CombatSession;
import be.helha.projects.GuerreDesRoyaumes.Model.Combat.CombatStatus;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;

import java.util.List;
import java.util.Map;

/**
 * Interface définissant les opérations d'accès aux données pour les sessions de combat dans MongoDB.
 * Permet de gérer la persistance des combats, des actions des joueurs et des résultats.
 */
public interface CombatSessionMongoDAO {
    
    /**
     * Crée une nouvelle session de combat et l'enregistre dans MongoDB.
     * 
     * @param joueur1 Le premier joueur du combat
     * @param joueur2 Le second joueur du combat
     * @return L'ID de la session de combat créée
     */
    String creerSession(Joueur joueur1, Joueur joueur2);
    
    /**
     * Sauvegarde une session de combat existante dans MongoDB.
     * 
     * @param session La session de combat à sauvegarder
     * @return true si la sauvegarde a réussi, false sinon
     */
    boolean sauvegarderSession(CombatSession session);
    
    /**
     * Charge une session de combat depuis MongoDB.
     * 
     * @param combatId L'identifiant de la session de combat
     * @return La session de combat chargée ou null si non trouvée
     */
    CombatSession chargerSession(String combatId);
    
    /**
     * Trouve l'ID de la session de combat en cours entre deux joueurs.
     * 
     * @param joueur1Id L'ID du premier joueur
     * @param joueur2Id L'ID du second joueur
     * @return L'ID de la session de combat ou null si aucune session n'est trouvée
     */
    String trouverSessionId(int joueur1Id, int joueur2Id);
    
    /**
     * Sauvegarde l'action d'un joueur pour un tour spécifique dans une session de combat.
     * 
     * @param combatId L'identifiant de la session de combat
     * @param joueurId L'identifiant du joueur qui effectue l'action
     * @param tour Le numéro du tour
     * @param typeAction Le type d'action (attaque, defense, special, utiliser_item)
     * @param parametres Paramètres supplémentaires (comme l'ID d'un item)
     * @return true si la sauvegarde a réussi, false sinon
     */
    boolean sauvegarderAction(String combatId, int joueurId, int tour, String typeAction, Map<String, Object> parametres);
    
    /**
     * Récupère les actions des deux joueurs pour un tour spécifique.
     * 
     * @param combatId L'identifiant de la session de combat
     * @param tour Le numéro du tour
     * @return Une map avec les actions des joueurs (clé = ID du joueur, valeur = détails de l'action)
     */
    Map<Integer, Map<String, Object>> obtenirActions(String combatId, int tour);
    
    /**
     * Vérifie si les deux joueurs ont soumis leurs actions pour un tour spécifique.
     * 
     * @param combatId L'identifiant de la session de combat
     * @param tour Le numéro du tour
     * @return true si les deux joueurs ont soumis leurs actions, false sinon
     */
    boolean actionsCompletes(String combatId, int tour);
    
    /**
     * Sauvegarde le résultat d'un tour de combat.
     * 
     * @param combatId L'identifiant de la session de combat
     * @param tour Le numéro du tour
     * @param resultats Les résultats du tour (dégâts infligés, vie restante, etc.)
     * @return true si la sauvegarde a réussi, false sinon
     */
    boolean sauvegarderResultatTour(String combatId, int tour, Map<String, Object> resultats);
    
    /**
     * Récupère le résultat d'un tour de combat.
     * 
     * @param combatId L'identifiant de la session de combat
     * @param tour Le numéro du tour
     * @return Les résultats du tour
     */
    Map<String, Object> obtenirResultatTour(String combatId, int tour);
    
    /**
     * Met à jour le statut d'une session de combat.
     * 
     * @param combatId L'identifiant de la session de combat
     * @param status Le nouveau statut de la session
     * @return true si la mise à jour a réussi, false sinon
     */
    boolean mettreAJourStatus(String combatId, CombatStatus status);
    
    /**
     * Vérifie si c'est au tour du joueur spécifié de jouer.
     * 
     * @param combatId L'identifiant de la session de combat
     * @param joueurId L'identifiant du joueur
     * @return true si c'est au tour du joueur de jouer, false sinon
     */
    boolean estTourDuJoueur(String combatId, int joueurId);
    
    /**
     * Obtient le numéro du tour actuel d'une session de combat.
     * 
     * @param combatId L'identifiant de la session de combat
     * @return Le numéro du tour actuel
     */
    int obtenirTourActuel(String combatId);
    
    /**
     * Archive une session de combat terminée.
     * 
     * @param combatId L'identifiant de la session de combat
     * @return true si l'archivage a réussi, false sinon
     */
    boolean archiverSession(String combatId);
}
