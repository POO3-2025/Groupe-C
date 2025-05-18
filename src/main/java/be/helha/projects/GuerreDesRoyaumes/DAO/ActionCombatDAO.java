package be.helha.projects.GuerreDesRoyaumes.DAO;

/**
 * Interface définissant les opérations CRUD pour les actions de combat
 */
public interface ActionCombatDAO {
    
    /**
     * Enregistre une action de combat dans la base de données
     * 
     * @param idCombat L'identifiant du combat
     * @param numeroTour Le numéro du tour (1-5)
     * @param joueurId L'identifiant du joueur qui effectue l'action
     * @param typeAction Le type d'action (attaque, defense, competence, potion)
     * @param parametres Détails supplémentaires sur l'action (JSON ou texte)
     * @return true si l'action a été enregistrée avec succès, false sinon
     */
    boolean enregistrerAction(String idCombat, int numeroTour, int joueurId, String typeAction, String parametres);
    
    /**
     * Récupère l'action effectuée par un joueur pour un tour spécifique
     * 
     * @param idCombat L'identifiant du combat
     * @param numeroTour Le numéro du tour
     * @param joueurId L'identifiant du joueur
     * @return Le type d'action ou null si aucune action trouvée
     */
    String obtenirTypeAction(String idCombat, int numeroTour, int joueurId);
    
    /**
     * Récupère les paramètres d'une action pour un joueur et un tour spécifiques
     * 
     * @param idCombat L'identifiant du combat
     * @param numeroTour Le numéro du tour
     * @param joueurId L'identifiant du joueur
     * @return Les paramètres de l'action ou null si aucune action trouvée
     */
    String obtenirParametresAction(String idCombat, int numeroTour, int joueurId);
    
    /**
     * Vérifie si un joueur a déjà effectué une action pour un tour spécifique
     * 
     * @param idCombat L'identifiant du combat
     * @param numeroTour Le numéro du tour
     * @param joueurId L'identifiant du joueur
     * @return true si le joueur a déjà effectué une action, false sinon
     */
    boolean joueurAEffectueAction(String idCombat, int numeroTour, int joueurId);
    
    /**
     * Vérifie si les deux joueurs ont effectué leurs actions pour un tour spécifique
     * 
     * @param idCombat L'identifiant du combat
     * @param numeroTour Le numéro du tour
     * @param joueur1Id L'identifiant du premier joueur
     * @param joueur2Id L'identifiant du deuxième joueur
     * @return true si les deux joueurs ont effectué leurs actions, false sinon
     */
    boolean tourEstComplet(String idCombat, int numeroTour, int joueur1Id, int joueur2Id);
    
    /**
     * Supprime toutes les actions d'un combat
     * 
     * @param idCombat L'identifiant du combat
     * @return true si les actions ont été supprimées avec succès, false sinon
     */
    boolean supprimerActionsCombat(String idCombat);
}
