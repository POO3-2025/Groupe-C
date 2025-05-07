package be.helha.projects.GuerreDesRoyaumes.Model.Items;

import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;

public class Argent extends Item {
    int quantite;

    //Constructeur
    public Argent(int id, String nom, int quantiteMax, int quantite ,int prix) {
        super(id, "TerraCoin", quantiteMax, "Argent" , 0);
        this.quantite = quantite;
    }

    //Getteur
    public int getQuantite() {
        return quantite;
    }

    //Setteur
    public void setQuantite(int quantite) {
        this.quantite = quantite;
    }

    public void use(Joueur joueur) {
        // Logique d'utilisation de l'argent
        joueur.ajouterArgent(this.quantite);
        System.out.println("Vous avez utilisé " + this.quantite + " " + getNom());
        // Logique d'utilisation de l'argent
    }

    @Override
    public void use() {

    }

    @Override
    public String toString() {
        return getNom() + " – Quantité : " + quantite;
    }
}