package be.helha.projects.GuerreDesRoyaumes.Model.Items;

public abstract class Item {

    private int id;
    private String nom;
    private int quantiteMax;
    private String type;
    private int prix;
    private String description;

    //Constructeur
    public Item(int id, String nom, int quantiteMax, String type, int prix) {
        this.id = id;
        this.nom = nom;
        this.quantiteMax = quantiteMax;
        this.type = type;
        this.prix = prix;
        this.description = "";
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
    public int getPrix() {
        return prix;
    }
    public String getDescription() {
        return description;
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
    public void setPrix(int prix) {
        this.prix = prix;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    // Méthode abstraite 'use()' à implémenter dans les classes dérivées
    public abstract void use();

    @Override
    public String toString() {
        return nom + " (" + type + ")" + " - Prix : " + prix + " TerraCoin";
    }
}