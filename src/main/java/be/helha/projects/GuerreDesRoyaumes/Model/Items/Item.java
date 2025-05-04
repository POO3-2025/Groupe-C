package be.helha.projects.GuerreDesRoyaumes.Model.Items;

public abstract class Item {
    private String id;
    private String nom;
    private String description;
    private int QuantiteMax;// quantité max de l'item par slot

    public Item(String id, String nom, String description, int quantiteMax) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        QuantiteMax = quantiteMax;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantiteMax() {
        return QuantiteMax;
    }

    public void setQuantiteMax(int quantiteMax) {
        QuantiteMax = quantiteMax;
    }
}
