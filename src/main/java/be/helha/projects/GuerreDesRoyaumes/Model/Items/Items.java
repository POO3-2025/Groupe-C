package be.helha.projects.GuerreDesRoyaumes.Model.Items;

public abstract class Items {
    private int id;
    private String nom;
    private int quantiteMax; // quantité max de l'item par slot

    //Constructeur
    public Items(int id, String nom, int quantiteMax) {
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
        quantiteMax = quantiteMax;
    }

    @Override
    public String toString() {
        return nom;
    }
}