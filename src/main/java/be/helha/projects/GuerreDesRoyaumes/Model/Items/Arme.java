package be.helha.projects.GuerreDesRoyaumes.Model.Items;

public class Arme extends Items {

    private int degats;

    //Constructeur
    public Arme(int id, String nom, int quantiteMax, int degats) {
        super(id, nom, quantiteMax);
        this.degats = degats;
    }

    //Getteur
    public int getdegats() {
        return degats;
    }

    //Setteur
    public void setdegats(int damage) {
        this.degats = degats;
    }

    @Override
    public String toString() {
        return getNom() + " – Dégâts : " + degats;
    }
}
