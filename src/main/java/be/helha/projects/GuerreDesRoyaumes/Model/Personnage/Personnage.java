package be.helha.projects.GuerreDesRoyaumes.Model.Personnage;

public abstract class Personnage {
    private String nom;
    private int vie;
    private int degats;
    private int resistance;

    //Constructeur
    public Personnage(String nom, int vie, int degats, int resistance) {
        this.nom = nom;
        this.vie = vie;
        this.degats = degats;
        this.resistance = resistance;
    }

    //Getteur
    public String getNom() {
        return nom;
    }
    public int getVie() {
        return vie;
    }
    public int getDegats() {
        return degats;
    }
    public int getResistance() {
        return resistance;
    }

    //Setteur
    public void setNom(String nom) {
        this.nom = nom;
    }
    public void setVie(int vie) {
        this.vie = vie;
    }
    public void setDegats(int degats) {
        this.degats = degats;
    }
    public void setResistance(int resistance) {
        this.resistance = resistance;
    }

    public abstract void attaquer();
    public abstract void attaquerSpecial();
    public abstract void defense();
    public abstract void UtilisationObjet();

    @Override
    public String toString() {
        return nom + " vie = " + vie + " degats = " + degats + " resistance = " + resistance;
    }
}
