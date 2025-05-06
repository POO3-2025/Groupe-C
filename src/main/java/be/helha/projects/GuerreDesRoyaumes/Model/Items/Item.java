package be.helha.projects.GuerreDesRoyaumes.Model.Items;

public abstract class Item {

    private int id;
    private String nom;
    private int quantiteMax; // quantité max de l'item par slot
    private String type;// type de l'item (ex: arme, bouclier, etc.)
    private double prix; // prix de l'item en TerraCoin

    //Constructeur
    public Item(int id, String nom, int quantiteMax, String type, double prix) {
        this.id = id;
        this.nom = nom;
        this.quantiteMax = quantiteMax;
        this.type = type;
        this.prix = prix;
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
    public double getPrix() {
        return prix;
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
    public void setPrix(double prix) {
        this.prix = prix;
    }

    // Méthode abstraite 'use()' à implémenter dans les classes dérivées
    public abstract void use();

    @Override
    public String toString() {
        return nom + " (" + type + ")" + " - Prix : " + prix + " TerraCoin";
    }
}