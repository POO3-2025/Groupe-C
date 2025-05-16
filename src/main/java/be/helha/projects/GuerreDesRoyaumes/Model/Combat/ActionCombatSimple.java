package be.helha.projects.GuerreDesRoyaumes.Model.Combat;

import be.helha.projects.GuerreDesRoyaumes.Model.Items.Arme;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Bouclier;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;

public class ActionCombatSimple {

    public enum TypeAction {
        ATTAQUER,
        EQUIPER_BOUCLIER
    }

    private TypeAction type;
    private Personnage acteur;
    private Personnage cible;
    private Arme arme;
    private Bouclier bouclier;

    public ActionCombatSimple(TypeAction type, Personnage acteur, Personnage cible, Arme arme, Bouclier bouclier) {
        this.type = type;
        this.acteur = acteur;
        this.cible = cible;
        this.arme = arme;
        this.bouclier = bouclier;
    }

    public TypeAction getType() {
        return type;
    }

    public Personnage getActeur() {
        return acteur;
    }

    public Personnage getCible() {
        return cible;
    }

    public Arme getArme() {
        return arme;
    }

    public Bouclier getBouclier() {
        return bouclier;
    }
}
