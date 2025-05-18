package be.helha.projects.GuerreDesRoyaumes.Service;

import be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat.Competence;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;

import java.util.List;

/**
 * Interface définissant les services de gestion des compétences de combat.
 * <p>
 * Elle inclut les opérations d'achat, de récupération, d'application
 * et de réinitialisation des compétences des joueurs.
 * </p>
 */
public interface CompetenceService {

    /**
     * Achète une compétence pour un joueur.
     * <p>
     * Vérifie que le joueur dispose de suffisamment d'argent,
     * débite le montant et sauvegarde la compétence.
     * </p>
     *
     * @param joueur     Le joueur qui achète la compétence.
     * @param competence La compétence à acheter.
     * @return true si l'achat a réussi, false sinon.
     * @throws Exception En cas d'erreur lors de l'achat.
     */
    boolean acheterCompetence(Joueur joueur, Competence competence) throws Exception;

    /**
     * Obtient la liste des compétences achetées par un joueur.
     *
     * @param joueur Le joueur dont on souhaite récupérer les compétences.
     * @return La liste des compétences achetées.
     */
    List<Competence> obtenirCompetencesJoueur(Joueur joueur);

    /**
     * Vérifie si un joueur peut acheter une nouvelle compétence.
     * <p>
     * Par exemple, vérifie que le joueur n'a pas déjà atteint la limite
     * maximale (ex: 4 compétences).
     * </p>
     *
     * @param joueur Le joueur à vérifier.
     * @return true si le joueur peut acheter une compétence supplémentaire, false sinon.
     */
    boolean peutAcheterNouvelleCompetence(Joueur joueur);

    /**
     * Réinitialise toutes les compétences d'un joueur (les supprime).
     * <p>
     * Utile après un combat ou lors de l'annulation d'une préparation au combat.
     * </p>
     *
     * @param joueur Le joueur dont on veut réinitialiser les compétences.
     * @return true si la réinitialisation a réussi, false sinon.
     */
    boolean reinitialiserCompetences(Joueur joueur);

    /**
     * Applique les effets de toutes les compétences achetées par un joueur à son personnage.
     *
     * @param joueur Le joueur dont on veut appliquer les compétences.
     * @return true si l'application a réussi, false sinon.
     */
    boolean appliquerCompetences(Joueur joueur);

    /**
     * Obtient la liste de toutes les compétences disponibles dans le jeu.
     *
     * @return La liste complète des compétences disponibles.
     */
    List<Competence> obtenirToutesCompetences();
}
