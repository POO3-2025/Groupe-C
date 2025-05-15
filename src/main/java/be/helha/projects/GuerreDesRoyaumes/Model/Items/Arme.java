package be.helha.projects.GuerreDesRoyaumes.Model.Items;

import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;

public class Arme extends Item {

    private double degats;

    //Constructeur avec ID
    public Arme(int id, String nom, int quantiteMax, int prix, double degats) {
        super(id, nom, quantiteMax, "Arme", prix);
        this.degats = degats;
    }

    //Constructeur sans ID (pour création initiale)
    public Arme(String nom, int quantiteMax, double degats, int prix) {
        super(0, nom, quantiteMax, "Arme", prix);
        this.degats = degats;
    }

    //Getteur
    public double getDegats() {
        return degats;
    }

    //Setteur
    public void setDegats(double degats) {
        this.degats = degats;
    }

    public void use() {
        System.out.println("Vous utilisez " + getNom());
        // TODO Logique d'utilisation de l'arme
        // TODO Il faut l'appliquer au personnage
        // TODO personnage.setDegats(personnage.getDegats() + degats);
    }

    @Override
    public String toString() {
        return getNom() + " – Dégâts : " + degats;
    }

}
