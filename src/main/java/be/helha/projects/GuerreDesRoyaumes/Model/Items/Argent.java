package be.helha.projects.GuerreDesRoyaumes.Model.Items;

public class Argent extends Items {
    int quantite;

    //Constructeur
    public Argent(int id, String nom, int quantiteMax, int quantite) {
        super(id, nom, quantiteMax);
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

    @Override
    public String toString() {
        return quantite + "€";
    }
}