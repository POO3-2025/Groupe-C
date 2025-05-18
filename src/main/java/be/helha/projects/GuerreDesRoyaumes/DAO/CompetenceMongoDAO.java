package be.helha.projects.GuerreDesRoyaumes.DAO;

import be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat.Competence;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;

import java.util.List;

/**
 * Interface définissant les opérations d'accès aux données pour les compétences de combat dans MongoDB.
 */
public interface CompetenceMongoDAO {
    
    /**
     * Sauvegarde une compétence pour un joueur dans MongoDB.
     * 
     * @param joueur Le joueur qui achète la compétence
     * @param competence La compétence à sauvegarder
     * @return true si la sauvegarde a réussi, false sinon
     */
    boolean sauvegarderCompetence(Joueur joueur, Competence competence);
    
    /**
     * Récupère toutes les compétences achetées par un joueur.
     * 
     * @param joueurId L'identifiant du joueur
     * @return La liste des compétences achetées par le joueur
     */
    List<Competence> obtenirCompetencesParJoueurId(int joueurId);
    
    /**
     * Récupère toutes les compétences achetées par un joueur via son pseudo.
     * 
     * @param pseudo Le pseudo du joueur
     * @return La liste des compétences achetées par le joueur
     */
    List<Competence> obtenirCompetencesParPseudo(String pseudo);
    
    /**
     * Vérifie si un joueur possède déjà une compétence spécifique.
     * 
     * @param joueurId L'identifiant du joueur
     * @param competenceId L'identifiant de la compétence
     * @return true si le joueur possède la compétence, false sinon
     */
    boolean joueurPossedeCompetence(int joueurId, String competenceId);
    
    /**
     * Supprime une compétence spécifique d'un joueur.
     * 
     * @param joueurId L'identifiant du joueur
     * @param competenceId L'identifiant de la compétence à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    boolean supprimerCompetence(int joueurId, String competenceId);
    
    /**
     * Supprime toutes les compétences d'un joueur.
     * 
     * @param joueurId L'identifiant du joueur
     * @return true si la suppression a réussi, false sinon
     */
    boolean supprimerToutesCompetences(int joueurId);
    
    /**
     * Compte le nombre de compétences que possède un joueur.
     * 
     * @param joueurId L'identifiant du joueur
     * @return Le nombre de compétences que possède le joueur
     */
    int compterCompetences(int joueurId);

}