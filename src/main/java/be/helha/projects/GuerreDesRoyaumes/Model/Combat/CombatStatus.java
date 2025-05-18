package be.helha.projects.GuerreDesRoyaumes.Model.Combat;

/**
 * Énumération représentant les différents statuts possibles d'une session de combat.
 */
public enum CombatStatus {
    /**
     * Le combat est en cours d'initialisation.
     */
    INITIALISATION,
    
    /**
     * Le combat est en cours d'exécution, les joueurs peuvent soumettre des actions.
     */
    EN_COURS,
    
    /**
     * Le combat est en attente de la soumission des actions des joueurs pour le tour en cours.
     */
    ATTENTE_ACTIONS,
    
    /**
     * Le combat est en cours de résolution, les actions des joueurs sont traitées.
     */
    RESOLUTION,
    
    /**
     * Le combat est terminé.
     */
    TERMINE,
    
    /**
     * Le combat a été abandonné avant sa fin.
     */
    ABANDONNE;
} 