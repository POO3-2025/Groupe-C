package be.helha.projects.GuerreDesRoyaumes.Model.Combat;

import be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat.Competence;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;

/**
 * Classe représentant l'action effectuée par un joueur lors d'un tour de combat.
 * <p>
 * Une action comprend le joueur concerné, le type d'action ("Attaque", "Defense", etc.),
 * ainsi qu'une compétence éventuellement utilisée pendant cette action.
 * </p>
 */
public class ActionTour {

    private Joueur joueur;
    private String action; // Exemples : "Attaque", "Defense"
    private Competence competenceUtilisee;

    /**
     * Constructeur complet.
     *
     * @param joueur            Le joueur effectuant l'action.
     * @param action            Type d'action réalisée par le joueur.
     * @param competenceUtilisee Compétence utilisée lors de l'action (peut être null).
     */
    public ActionTour(Joueur joueur, String action, Competence competenceUtilisee) {
        this.joueur = joueur;
        this.action = action;
        this.competenceUtilisee = competenceUtilisee;
    }

    /**
     * @return Le joueur effectuant l'action.
     */
    public Joueur getJoueur() {
        return joueur;
    }

    /**
     * Définit le joueur effectuant l'action.
     *
     * @param joueur Nouveau joueur.
     */
    public void setJoueur(Joueur joueur) {
        this.joueur = joueur;
    }

    /**
     * @return Le type d'action réalisée.
     */
    public String getAction() {
        return action;
    }

    /**
     * Définit le type d'action réalisée.
     *
     * @param action Nouveau type d'action.
     */
    public void setAction(String action) {
        this.action = action;
    }

    /**
     * @return La compétence utilisée lors de l'action (peut être null).
     */
    public Competence getCompetenceUtilisee() {
        return competenceUtilisee;
    }

    /**
     * Définit la compétence utilisée lors de l'action.
     *
     * @param competenceUtilisee Nouvelle compétence utilisée.
     */
    public void setCompetenceUtilisee(Competence competenceUtilisee) {
        this.competenceUtilisee = competenceUtilisee;
    }
}
