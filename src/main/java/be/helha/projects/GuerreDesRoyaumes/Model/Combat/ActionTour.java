package be.helha.projects.GuerreDesRoyaumes.Model.Combat;

import be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat.Competence;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;

public class ActionTour {
    private Joueur joueur;
    private String action; // "Attaque","Defense"
    private Competence competenceUtilisee;

    public ActionTour(Joueur joueur, String action, Competence competenceUtilisee) {
        this.joueur = joueur;
        this.action = action;
        this.competenceUtilisee = competenceUtilisee;
    }

    public Joueur getJoueur() {
        return joueur;
    }

    public void setJoueur(Joueur joueur) {
        this.joueur = joueur;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Competence getCompetenceUtilisee() {
        return competenceUtilisee;
    }

    public void setCompetenceUtilisee(Competence competenceUtilisee) {
        this.competenceUtilisee = competenceUtilisee;
    }
}
