package be.helha.projects.GuerreDesRoyaumes.Model.Personnage;

import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Arme;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Bouclier;

public abstract class Personnage {
    private String nom;
    private double vie;
    private double degats;
    private double defense;
    private Inventaire inventaire;

    private Bouclier bouclierEquipe;

    // Constructeur
    public Personnage(String nom, int vie, int degats, int defense, Inventaire inventaire) {
        this.nom = nom;
        this.vie = vie;
        this.degats = degats;
        this.defense = defense;
        this.inventaire = inventaire;
    }

    // Getters
    public String getNom() {
        return nom;
    }

    public double getVie() {
        return vie;
    }

    public double getDegats() {
        return degats;
    }

    public double getDefense() {
        return defense;
    }

    public Inventaire getInventaire() {
        return inventaire;
    }

    public Bouclier getBouclierEquipe() {
        return bouclierEquipe;
    }

    // Setters
    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setVie(double vie) {
        this.vie = vie < 0 ? 0 : vie;  // S'assure que la vie ne descende pas en dessous de 0
    }

    public void setDegats(double degats) {
        this.degats = degats;
    }

    public void setDefense(double defense) {
        this.defense = defense;
    }

    public void setInventaire(Inventaire inventaire) {
        this.inventaire = inventaire;
    }


    // Méthodes de combat
    public void attaquer(Personnage cible, Arme arme) {
        if (arme != null) {
            System.out.println(this.nom + " attaque " + cible.getNom() + " avec " + arme.getNom() + "\n");
        } else {
            System.out.println(this.nom + " attaque " + cible.getNom() + " à mains nues\n");
        }

        // Calcul des dégâts totaux de l'attaquant
        double degatsTotal = this.degats;
        if (arme != null) {
            degatsTotal += arme.getDegats();
        }

        // La défense du personnage inclut déjà le bouclier s'il est équipé
        double defenseTotale = cible.getDefense();

        // Dégâts infligés après défense
        double degatsInfliges = degatsTotal - defenseTotale;
        if (degatsInfliges < 0) degatsInfliges = 0;

        // Appliquer les dégâts
        cible.recevoirDegats(degatsInfliges);

        System.out.println(cible.getNom() + " subit " + degatsInfliges + " points de dégâts.\n");
        System.out.println(cible.getNom() + " a maintenant " + cible.getVie() + " PV.\n");
    }

    public void equiperBouclier(Bouclier bouclier) {
        if (bouclierEquipe != null) {
            this.defense -= bouclierEquipe.getDefense();
        }

        this.bouclierEquipe = bouclier;

        if (bouclier != null) {
            this.defense += bouclier.getDefense();
            System.out.println(getNom() + " a équipé le bouclier " + bouclier.getNom() + " avec " + bouclier.getDefense() + " de défense.\n");
        } else {
            System.out.println(getNom() + " n'a plus de bouclier équipé.\n");
        }

        if (this.defense < 0) this.defense = 0;
    }

    public void recevoirDegats(double degats) {
        setVie(this.vie - degats);
    }



    @Override
    public String toString() {
        return nom + " vie = " + vie + " degats = " + degats + " resistance = " + defense;
    }
}
