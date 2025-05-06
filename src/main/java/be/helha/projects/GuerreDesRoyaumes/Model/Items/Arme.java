package be.helha.projects.GuerreDesRoyaumes.Model.Items;

public class Arme extends Item {

    private int degats;// degats supplementaire de l'arme qui s'ajoute aux degats de base du personnage

    //Constructeur
    public Arme(int id, String nom, int quantiteMax, int degats) {
        super(id, nom, quantiteMax, "Arme");
        this.degats = degats;
    }

    //Getteur
    public int getDegats() {
        return degats;
    }

    //Setteur
    public void setDegats(int degats) {
        this.degats = this.degats;
    }

    public void use() {
        System.out.println("Vous utilisez l'arme " + getNom());
        // Logique d'utilisation de l'arme
    }

    @Override
    public String toString() {
        return getNom() + " – Dégâts : " + degats;
    }

}
