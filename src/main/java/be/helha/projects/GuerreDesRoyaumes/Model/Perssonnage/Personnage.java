package be.helha.projects.GuerreDesRoyaumes.Model.Perssonnage;

import be.helha.projects.GuerreDesRoyaumes.Model.Competence;

public abstract class Personnage {

    private String nom;
    private String description;
    private int vie;
    private int force;
    private int defense;
    private int vitesse;


    private Competence competenceBonus;


    public Personnage(String nom, String description, int vie, int force, int defense, int vitesse, int argent, Competence competenceBonus) {
        this.nom = nom;
        this.description = description;
        this.vie = vie;
        this.force = force;
        this.defense = defense;
        this.vitesse = vitesse;
        this.competenceBonus = competenceBonus;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getVie() {
        return vie;
    }

    public void setVie(int vie) {
        this.vie = vie;
    }

    public int getForce() {
        return force;
    }

    public void setForce(int force) {
        this.force = force;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public int getVitesse() {
        return vitesse;
    }

    public void setVitesse(int vitesse) {
        this.vitesse = vitesse;
    }

    public Competence getcompetenceBonus() {
        return competenceBonus;
    }

    public void setcompetenceBonus(Competence competenceBonus) {
        this.competenceBonus = competenceBonus;
    }

    public abstract void attaquer();

}
