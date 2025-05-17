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
}
