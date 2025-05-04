package be.helha.projects.GuerreDesRoyaumes.Model;

import be.helha.projects.GuerreDesRoyaumes.Model.Perssonnage.Personnage;

public class Competence {
    public String nom;
    public String description;
    public int bonusVie;
    public int bonusAttaque;
    public int bonusDefense;
    public int bonusArgent;

    public Competence(String nom, String description, int bonusVie, int bonusAttaque, int bonusDefense, int bonusArgent) {
        this.nom = nom;
        this.description = description;
        this.bonusVie = bonusVie;
        this.bonusAttaque = bonusAttaque;
        this.bonusDefense = bonusDefense;
        this.bonusArgent = bonusArgent;
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

    public int getBonusVie() {
        return bonusVie;
    }

    public void setBonusVie(int bonusVie) {
        this.bonusVie = bonusVie;
    }

    public int getBonusAttaque() {
        return bonusAttaque;
    }

    public void setBonusAttaque(int bonusAttaque) {
        this.bonusAttaque = bonusAttaque;
    }

    public int getBonusDefense() {
        return bonusDefense;
    }

    public void setBonusDefense(int bonusDefense) {
        this.bonusDefense = bonusDefense;
    }

    public int getBonusArgent() {
        return bonusArgent;
    }

    public void setBonusArgent(int bonusArgent) {
        this.bonusArgent = bonusArgent;
    }

    // Méthode pour afficher les bonus de la compétence
    public void afficherBonus() {
        System.out.println("Bonus de la compétence " + nom + ":");
        System.out.println("Vie: +" + bonusVie);
        System.out.println("Attaque: +" + bonusAttaque);
        System.out.println("Défense: +" + bonusDefense);
        System.out.println("Argent: +" + bonusArgent);
    }

    // Applique les bonus au personnage (ici, à titre d'exemple, on suppose un personnage avec les attributs correspondants)
    public void appliquerBonus(Personnage personnage, Joueur Joueur) {
        personnage.setVie(personnage.getVie() + bonusVie);
        personnage.setForce(personnage.getForce() + bonusAttaque);
        personnage.setDefense(personnage.getDefense() + bonusDefense);
        Joueur.setArgent(Joueur.getArgent() + bonusArgent);
    }
}
