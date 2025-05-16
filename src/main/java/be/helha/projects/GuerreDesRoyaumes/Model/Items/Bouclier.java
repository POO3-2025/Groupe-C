package be.helha.projects.GuerreDesRoyaumes.Model.Items;

public class Bouclier extends Item {

    private double defense;

    // Constructeur avec ID
    public Bouclier(int id, String nom, int quantiteMax, int prix, double defense) {
        super(id, nom, quantiteMax, "Bouclier", prix);
        this.defense = defense;
    }

    // Constructeur sans ID (pour création initiale)
    public Bouclier(String nom, int quantiteMax, int prix, double defense) {
        super(0, nom, quantiteMax, "Bouclier", prix);
        this.defense = defense;
    }

    // Getteur
    public double getDefense() {
        return defense;
    }

    // Setteur
    public void setDefense(double defense) {
        this.defense = defense;
    }

    @Override
    public void use() {
        System.out.println("Vous utilisez " + getNom());
        // TODO Logique d'équipement du bouclier il faut l'appliquer au personnage directement apres
        // TODO qu'il est choisi les items pour le combat a chaque combat le joueur devra rechoisir l'item qu'il souhaite utiliser
    }

    @Override
    public String toString() {
        return getNom() + " – Défense : " + defense;
    }
}