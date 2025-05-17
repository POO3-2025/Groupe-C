package be.helha.projects.GuerreDesRoyaumes.DAO;

import be.helha.projects.GuerreDesRoyaumes.Model.Combat.Combat;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;

import java.util.List;

public interface CombatDAO {
    void enregistrerCombat(Combat combat);
    void enregistrerVictoire(Joueur joueur);

    Combat obtenirCombatParId(int id);

    List<Combat> obtenirTousLesCombats();

    List<Combat> obtenirCombatsParJoueurId(int joueurId);

    void enregistrerDefaite(Joueur joueur);
    List<Joueur> getClassementParVictoires();
    List<Joueur> getClassementParDefaites();

    /**
     * Envoie une demande de combat à un joueur
     *
     * @param idDemandeur L'identifiant du joueur qui fait la demande
     * @param idAdversaire L'identifiant du joueur à qui la demande est adressée
     * @return true si la demande a été enregistrée avec succès, false sinon
     */
    boolean envoyerDemandeCombat(int idDemandeur, int idAdversaire);

    /**
     * Vérifie si un joueur a des demandes de combat en attente
     *
     * @param idJoueur L'identifiant du joueur à vérifier
     * @return L'identifiant du demandeur si une demande existe, 0 sinon
     */
    int verifierDemandesCombat(int idJoueur);

    /**
     * Accepte une demande de combat
     *
     * @param idDemandeur L'identifiant du joueur qui a fait la demande
     * @param idAdversaire L'identifiant du joueur qui accepte la demande
     * @return true si la demande a été acceptée avec succès, false sinon
     */
    boolean accepterDemandeCombat(int idDemandeur, int idAdversaire);

    /**
     * Supprime une demande de combat
     *
     * @param idDemandeur L'identifiant du joueur qui a fait la demande
     * @param idAdversaire L'identifiant du joueur qui a reçu la demande
     * @return true si la demande a été supprimée avec succès, false sinon
     */
    boolean supprimerDemandeCombat(int idDemandeur, int idAdversaire);

    /**
     * Ajoute un combat en cours dans la table CombatEnCours
     * 
     * @param idJoueur1 L'identifiant du premier joueur
     * @param idJoueur2 L'identifiant du second joueur
     * @return true si l'ajout a réussi, false sinon
     */
    boolean ajouterCombatEnCours(int idJoueur1, int idJoueur2);
    
    /**
     * Vérifie si un joueur est impliqué dans un combat en cours
     * 
     * @param idJoueur L'identifiant du joueur à vérifier
     * @return l'identifiant de l'adversaire si le joueur est en combat, 0 sinon
     */
    int verifierCombatEnCours(int idJoueur);
    
    /**
     * Supprime un combat en cours
     * 
     * @param idJoueur1 L'identifiant du premier joueur
     * @param idJoueur2 L'identifiant du second joueur
     * @return true si la suppression a réussi, false sinon
     */
    boolean supprimerCombatEnCours(int idJoueur1, int idJoueur2);

    /**
     * Applique une sanction financière à un joueur qui annule un combat
     * 
     * @param idJoueur L'identifiant du joueur à sanctionner
     * @param montant Le montant de la sanction en TerraCoins
     * @return true si la sanction a été appliquée avec succès, false sinon
     */
    boolean appliquerSanctionFinanciere(int idJoueur, int montant);

    /**
     * Obtient le pseudonyme d'un joueur à partir de son ID
     * 
     * @param idJoueur L'identifiant du joueur
     * @return Le pseudonyme du joueur ou null si le joueur n'est pas trouvé
     */
    String obtenirPseudonyme(int idJoueur);
}
