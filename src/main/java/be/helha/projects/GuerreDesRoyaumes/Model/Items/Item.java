package be.helha.projects.GuerreDesRoyaumes.Model.Items;

public abstract class Item {

    private int id;
    private String nom;
    private int quantiteMax; // quantité max de l'item par slot
    private String type;// type de l'item (ex: arme, bouclier, etc.)

    //Constructeur
    public Item(int id, String nom, int quantiteMax, String type) {
        this.id = id;
        this.nom = nom;
        this.quantiteMax = quantiteMax;
        this.type = type;
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
    public String getType() {
        return type;
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
    public void setType(String type) {
        this.type = type;
    }

    // Méthode abstraite 'use()' à implémenter dans les classes dérivées
    public abstract void use();

    @Override
    public String toString() {
        return nom + " (" + type + ")";
    }
}