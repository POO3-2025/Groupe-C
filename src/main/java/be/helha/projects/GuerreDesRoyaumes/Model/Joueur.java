package be.helha.projects.GuerreDesRoyaumes.Model;

import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Coffre;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Voleur;

public class Joueur {
    private int id;
    private String nom;
    private String prenom;
    private String pseudo;
    private String motDePasse;
    private int argent;
    private Royaume royaume;
    private Personnage personnage;
    private Coffre coffre;


    //Construteur
    public Joueur(int id, String nom, String prenom, String pseudo, String motDePasse, int argent , Royaume royaume, Personnage personnage, Coffre coffre) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.pseudo = pseudo;
        this.motDePasse = motDePasse;
        this.argent = argent;
        this.royaume = royaume;
        this.personnage = personnage;
        this.coffre = coffre;
    }

    public Joueur() {

    }


    //Getteur
    public int getId() {
        return id;
    }
    public String getNom() {
        return nom;
    }
    public String getPrenom() {
        return prenom;
    }
    public String getPseudo() {
        return pseudo;
    }
    public String getMotDePasse() {
        return motDePasse;
    }
    public Royaume getRoyaume() {
        return royaume;
    }
    public Personnage getPersonnage() {
        return personnage;
    }
    public int getArgent() {
        return argent;
    }
    public Coffre getCoffre() {
        return coffre;
    }

    //Setteur
    public void setId(int id) {
        this.id = id;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }
    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }
    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }
    public void setRoyaume(Royaume royaume) {
        this.royaume = royaume;
    }
    public void setPersonnage(Personnage personnage) {
        this.personnage = personnage;
    }
    public void setArgent(int argent) {
        this.argent = argent;
    }
    public void setCoffre(Coffre coffre) {
        this.coffre = coffre;
    }


    // Méthode pour ajouter de l'argent
    public void ajouterArgent(int montant) {
        if (montant > 0) {
            this.argent += montant;
        } else {
            System.out.println("Montant à ajouter doit être positif.");
        }
    }


    // Méthode pour retirer de l'argent
    public void retirerArgent(int montant) {
        if (montant <= this.argent) {
            this.argent -= montant;
        } else {
            System.out.println("Pas assez d'argent.");
        }
    }

    // Méthode pour gagner de l'argent
    public void gagnerArgent(int montant) {
        if (this.personnage instanceof Voleur) {
            // Si le personnage est un voleur, l'argent gagné est doublé
            montant *= 2;
            System.out.println("Le voleur gagne " + montant + " TerraCoin (argent doublé).");
        } else {
            System.out.println("Le joueur gagne " + montant + " TerraCoin.");
        }
        ajouterArgent(montant); // Ajoute l'argent gagné
    }

    public void acheterItem(Item item) {
        if (this.argent >= item.getPrix()) {
            // Déduit le prix de l'item du solde du joueur
            this.argent -= item.getPrix();

            // Ajoute 1 item au coffre du joueur
            this.coffre.ajouterItem(item, 1);

            // Affiche le message de confirmation d'achat
            System.out.println("Achat réussi : " + item.getNom());
        } else {
            // Si le joueur n'a pas assez d'argent
            System.out.println("Pas assez d'argent pour acheter " + item.getNom());
        }
    }

    @Override
    public String toString() {
        return  "\nNom        = " + nom +
                "\nPrenom     = " + prenom +
                "\nPseudo     = " + pseudo +
                "\nRoyaume    = " + royaume +
                "\nPersonnage = " + personnage +
                "\nArgent     = " + argent;
    }
}