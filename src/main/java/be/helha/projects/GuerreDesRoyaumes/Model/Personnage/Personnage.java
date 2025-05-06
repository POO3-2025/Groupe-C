package be.helha.projects.GuerreDesRoyaumes.Model.Personnage;

import be.helha.projects.GuerreDesRoyaumes.Model.Items.Arme;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Bouclier;

public abstract class Personnage {
    private String nom; // represente le nom du personnage
    private int vie; // represente le niveau de vie de base du personnage
    private int degats; // represente le niveau d'attaque de base du personnage
    private int resistance; // represente le niveau de defense de base du personnage
    private Arme armeEquipee; // represente l'arme equipee par le personnage
    private Bouclier bouclierEquipee; // represente le bouclier equipee par le personnage

    //Constructeur
    public Personnage(String nom, int vie, int degats, int resistance) {
        this.nom = nom;
        this.vie = vie;
        this.degats = degats;
        this.resistance = resistance;
    }



    //Getteur
    public String getNom() {
        return nom;
    }
    public int getVie() {
        return vie;
    }
    public int getDegats() {
        return degats;
    }
    public int getResistance() {
        return resistance;
    }

    //Setteur
    public void setNom(String nom) {
        this.nom = nom;
    }
    public void setVie(int vie) {
        this.vie = vie;
    }
    public void setDegats(int degats) {
        this.degats = degats;
    }
    public void setResistance(int resistance) {
        this.resistance = resistance;
    }

    public int getAttaqueTotale() {
        return degats + (armeEquipee != null ? armeEquipee.getDegats() : 0);
    }

    public int getDefenseTotale() {
        return resistance + (bouclierEquipee != null ? bouclierEquipee.getDefense() : 0);
    }

    public void equiperArme(Arme arme) {
        this.armeEquipee = arme;
    }

    public void equiperBouclier(Bouclier bouclier) {
        this.bouclierEquipee = bouclier;
    }
    public abstract void attaquer();
    public abstract void attaquerSpecial();
    public abstract void defense();
    public abstract void UtilisationObjet();

    @Override
    public String toString() {
        return nom + " vie = " + vie + " degats = " + degats + " resistance = " + resistance;
    }
}
