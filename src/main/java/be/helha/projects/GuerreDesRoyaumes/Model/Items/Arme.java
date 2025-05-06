package be.helha.projects.GuerreDesRoyaumes.Model.Items;

import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;

public class Arme extends Item {

    private int degats;// degats supplementaire de l'arme qui s'ajoute aux degats de base du personnage

    //Constructeur
    public Arme(int id, String nom, int quantiteMax, int degats, double prix) {
        super(id, nom, quantiteMax, "Arme", prix);
        this.degats = degats;
    }

    //Getteur
    public int getDegats() {
        return degats;
    }

    //Setteur
    public void setDegats(int degats) {
        this.degats = degats;
    }

    public void use() {
        System.out.println("Vous utilisez l'arme " + getNom());
        // Logique d'utilisation de l'arme
        // Il faut l'appliquer au personnage
        //personnage.setDegats(personnage.getDegats() + degats);
    }

    @Override
    public String toString() {
        return getNom() + " – Dégâts : " + degats;
    }

}
