package be.helha.projects.GuerreDesRoyaumes.Model.Personnage;

import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Arme;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Bouclier;

import javax.imageio.plugins.jpeg.JPEGImageReadParam;

public abstract class Personnage {
    private String nom;
    private double vie;
    private double pointsDeVieMAX;
    private double degats;
    private double resistance;
    private Inventaire inventaire;

    //Constructeur
    public Personnage(String nom, int vie, int degats, int resistance, Inventaire inventaire) {
        this.nom = nom;
        this.vie = vie;
        pointsDeVieMAX = vie;
        this.degats = degats;
        this.resistance = resistance;
        this.inventaire = inventaire;
    }

    //Constructeur par défaut
    public Personnage() {
    }

    //Getteur
    public String getNom() {
        return nom;
    }
    public double getVie() {
        return vie;
    }
    public double getDegats() {
        return degats;
    }
    public double getResistance() {
        return resistance;
    }
    public Inventaire getInventaire() {
        return inventaire;
    }
    public double getPointsDeVieMAX() {
        return pointsDeVieMAX;
    }

    // Alias pour getVie pour compatibilité avec le reste du code
    public double getPointsDeVie() {
        return vie;
    }

    //Setteur
    public void setNom(String nom) {
        this.nom = nom;
    }
    public void setVie(double vie) {
        this.vie = vie;
    }
    public void setDegats(double degats) {
        this.degats = degats;
    }
    public void setResistance(double resistance) {
        this.resistance = resistance;
    }
    public void setInventaire(Inventaire inventaire) {
        this.inventaire = inventaire;
    }
    public void setPointsDeVieMAX(double pointsDeVieMAX) {
        this.pointsDeVieMAX = pointsDeVieMAX;
    }

    // Alias pour setVie pour compatibilité avec le reste du code
    public void setPointsDeVie(double pointsDeVie) {
        this.vie = pointsDeVie;
    }

    // Méthodes abstraites pour subir des dégâts et se soigner
    public abstract void subirDegats(double degatsSubis);
    public abstract void soigner(double pointsSoin);

    /*public double getAttaqueTotale() {
        return degats + (armeEquipee != null ? armeEquipee.getDegats() : 0);
    }
    public double getDefenseTotale() {
        return resistance + (bouclierEquipee != null ? bouclierEquipee.getDefense() : 0);
    }
    public void equiperArme(Arme arme) {
        this.armeEquipee = arme;
    }
    public void equiperBouclier(Bouclier bouclier) {
        this.bouclierEquipee = bouclier;
    }*/

    public abstract void attaquer();
    public abstract void attaquerSpecial();
    public abstract void defense();
    public abstract void UtilisationObjet();



    @Override
    public String toString() {
        return nom + " vie = " + vie + " degats = " + degats + " resistance = " + resistance;
    }
}
