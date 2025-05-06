package be.helha.projects.GuerreDesRoyaumes.Model.Items;

public abstract class Item {

    private int id;
    private String nom;
    private int quantiteMax; // quantité max de l'item par slot

    //Constructeur
    public Item(int id, String nom, int quantiteMax) {
        this.id = id;
        this.nom = nom;
        this.quantiteMax = quantiteMax;
    }

    //Getteur
    public int getId() {
        return id;
    }
    public String getNom() {
        return nom;
    }
    public int getQuantiteMax() {
        return quantiteMax;
    }

    //Setteur
    public void setId(int id) {
        this.id = id;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public void setQuantiteMax(int quantiteMax) {
        this.quantiteMax = quantiteMax;
    }

    // Méthode abstraite 'use()' à implémenter dans les classes dérivées
    public abstract void use();

    @Override
    public String toString() {
        return nom;
    }
}