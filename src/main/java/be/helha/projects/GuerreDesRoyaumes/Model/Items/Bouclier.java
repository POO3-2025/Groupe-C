package be.helha.projects.GuerreDesRoyaumes.Model.Items;

public class Bouclier extends Item {
    private int defense; // Represente la valeur a ajouter a la resistance du personnage

    public Bouclier(int id, String nom, int quantiteMax, int defense) {
        super(id, nom, quantiteMax, "Bouclier");
        this.defense = defense;
    }

    public int getDefense() {
        return defense;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    @Override
    public void use() {
        System.out.println("Vous utilisez le bouclier " + getNom());
        // Logique d'équipement du bouclier il faut l'appliquer au personnage directement apres
        // qu'il est choisi les items pour le combat a chaque combat le joueur devra rechoisir l'item qu'il souhaite utiliser
    }

    @Override
    public String toString() {
        return getNom() + " – Défense : " + defense;
    }
}