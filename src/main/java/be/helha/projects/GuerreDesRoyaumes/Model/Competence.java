package be.helha.projects.GuerreDesRoyaumes.Model;

import be.helha.projects.GuerreDesRoyaumes.Model.Perssonnage.Personnage;

public class Competence {
    public String nom;
    public String description;
    public int bonusVie;
    public int bonusDegats;
    public int bonusDefense;
    public int bonusArgent;

    //Constructeur
    public Competence(String nom, String description, int bonusVie, int bonusAttaque, int bonusDefense, int bonusArgent) {
        this.nom = nom;
        this.description = description;
        this.bonusVie = bonusVie;
        this.bonusDegats = bonusAttaque;
        this.bonusDefense = bonusDefense;
        this.bonusArgent = bonusArgent;
    }

    //Getteur
    public String getNom() {
        return nom;
    }
    public String getDescription() {
        return description;
    }
    public int getBonusVie() {
        return bonusVie;
    }
    public int getBonusAttaque() {
        return bonusDegats;
    }
    public int getBonusDefense() {
        return bonusDefense;
    }
    public int getBonusArgent() {
        return bonusArgent;
    }

    //Setteur
    public void setNom(String nom) {
        this.nom = nom;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setBonusVie(int bonusVie) {
        this.bonusVie = bonusVie;
    }
    public void setBonusAttaque(int bonusAttaque) {
        this.bonusDegats = bonusAttaque;
    }
    public void setBonusDefense(int bonusDefense) {
        this.bonusDefense = bonusDefense;
    }
    public void setBonusArgent(int bonusArgent) {
        this.bonusArgent = bonusArgent;
    }

    // Méthode pour afficher les bonus de la compétence
    public void afficherBonus() {
        System.out.println("Bonus de la compétence " + nom + ":");
        System.out.println("Vie: +" + bonusVie);
        System.out.println("Attaque: +" + bonusDegats);
        System.out.println("Défense: +" + bonusDefense);
        System.out.println("Argent: +" + bonusArgent);
    }

    // Applique les bonus au personnage (ici, à titre d'exemple, on suppose un personnage avec les attributs correspondants)
    public void appliquerBonus(Personnage personnage, Joueur Joueur) {
        personnage.setVie(personnage.getVie() + bonusVie);
        personnage.setDegats(personnage.getDegats() + bonusDegats);
        personnage.setResistance(personnage.getResistance() + bonusDefense);
        Joueur.setArgent(Joueur.getArgent() + bonusArgent);
    }
}
