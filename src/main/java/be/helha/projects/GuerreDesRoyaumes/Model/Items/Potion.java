package be.helha.projects.GuerreDesRoyaumes.Model.Items;


public class Potion extends Item {

    private double degats;
    private double soin;

    // Constructeur avec ID
    public Potion(int id, String nom, int quantiteMax, int prix, double degats, double soin) {
        super(id, nom, quantiteMax, "Potion", prix);
        this.degats = degats;
        this.soin = soin;
    }

    // Constructeur sans ID (pour création initiale)
    public Potion(String nom, int quantiteMax, int prix, double degats, double soin) {
        super(0, nom, quantiteMax, "Potion", prix);
        this.degats = degats;
        this.soin = soin;
    }


    //Getteur
    public double getDegats() {
        return degats;
    }
    public double getSoin() {
        return soin;
    }

    //Setteur
    public void setDegats(double degats) {
        this.degats = degats;
    }
    public void setSoin(double soin) {
        this.soin = soin;
    }

    @Override
    public void use() {
        System.out.println("Vous utilisez " + getNom());
        // TODO Logique d'utilisation de la potion
    }
}
