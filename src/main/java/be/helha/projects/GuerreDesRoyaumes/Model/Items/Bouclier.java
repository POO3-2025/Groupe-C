package be.helha.projects.GuerreDesRoyaumes.Model.Items;

import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;

public class Bouclier extends Item {

    private double defense;

    // Constructeur
    public Bouclier(int id, String nom, int quantiteMax, int prix, double defense) {
        super(id, nom, quantiteMax, "Bouclier", prix);
        this.defense = defense;
    }

    // Getters
    public double getDefense() {
        return defense;
    }

    // Setters
    public void setDefense(double defense) {
        this.defense = defense;
    }



    public void encaisserDegats(double degats, Personnage personnage) {
        // La défense du bouclier réduit les dégâts reçus
        double degatsRestants = degats - this.defense;

        if (degatsRestants < 0) {
            degatsRestants = 0;
        }

        // Le personnage subit les dégâts restants
        personnage.setVie(personnage.getVie() - degatsRestants);
    }

    // Méthode "use" pour équiper le bouclier à un personnage
    @Override
    public void use(Personnage personnage) {
        if (personnage.getBouclierEquipe() == null) {  // Vérifie si un bouclier n'est pas déjà équipé
            personnage.equiperBouclier(this);
            System.out.println(personnage.getNom() + " a équipé le bouclier " + getNom() + " avec " + getDefense() + " de defense.\n");
        } else {
            System.out.println(personnage.getNom() + " possède déjà un bouclier équipé.\n");
        }
    }

//    // Méthode toString pour afficher les informations du bouclier
//    @Override
//    public String toString() {
//        return getNom() + " – Défense : " + defense;
//    }
}
