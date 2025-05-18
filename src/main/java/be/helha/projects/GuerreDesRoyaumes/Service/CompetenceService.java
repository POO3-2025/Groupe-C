package be.helha.projects.GuerreDesRoyaumes.Service;

import be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat.Competence;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;

import java.util.List;

/**
 * Interface définissant les services de gestion des compétences de combat.
 */
public interface CompetenceService {

    /**
     * Achète une compétence pour un joueur.
     * Cette méthode vérifie si le joueur a assez d'argent, puis débite le montant et sauvegarde la compétence.
     *
     * @param joueur Le joueur qui achète la compétence
     * @param competence La compétence à acheter
     * @return true si l'achat a réussi, false sinon
     * @throws Exception Si une erreur survient pendant l'achat
     */
    boolean acheterCompetence(Joueur joueur, Competence competence) throws Exception;

    /**
     * Obtient la liste des compétences achetées par un joueur.
     *
     * @param joueur Le joueur dont on veut obtenir les compétences
     * @return La liste des compétences achetées par le joueur
     */
    List<Competence> obtenirCompetencesJoueur(Joueur joueur);

    /**
     * Vérifie si un joueur peut acheter une nouvelle compétence.
     * Cette méthode vérifie si le joueur a déjà 4 compétences (maximum autorisé).
     *
     * @param joueur Le joueur à vérifier
     * @return true si le joueur peut acheter une nouvelle compétence, false sinon
     */
    boolean peutAcheterNouvelleCompetence(Joueur joueur);

    /**
     * Réinitialise toutes les compétences d'un joueur (les supprime).
     * Utile après un combat ou lors de l'annulation d'une préparation au combat.
     *
     * @param joueur Le joueur dont on veut réinitialiser les compétences
     * @return true si la réinitialisation a réussi, false sinon
     */
    boolean reinitialiserCompetences(Joueur joueur);

    /**
     * Applique les effets de toutes les compétences achetées par un joueur à son personnage.
     *
     * @param joueur Le joueur dont on veut appliquer les compétences
     * @return true si l'application a réussi, false sinon
     */
    boolean appliquerCompetences(Joueur joueur);

    /**
     * Obtient la liste de toutes les compétences disponibles.
     *
     * @return La liste de toutes les compétences disponibles
     */
    List<Competence> obtenirToutesCompetences();
}