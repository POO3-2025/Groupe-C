package be.helha.projects.GuerreDesRoyaumes.Model.Items;

public class Argent extends Item {
    int quantite;

    //Constructeur
    public Argent(int id, String nom, int quantiteMax, int quantite) {
        super(id, "TerraCoin", quantiteMax, "Argent");
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

    public void use() {
        System.out.println("Vous utilisez l'argent " + getNom());
        // Logique d'utilisation de l'argent
    }

    @Override
    public String toString() {
        return getNom() + " – Quantité : " + quantite;
    }
}