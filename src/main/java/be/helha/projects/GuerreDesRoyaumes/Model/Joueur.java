package be.helha.projects.GuerreDesRoyaumes.Model;

import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;

public class Joueur {
    private int id;
    private String nom;
    private String prenom;
    private String pseudo;
    private String motDePasse;
    private int argent;
    private Royaume royaume;
    private Personnage personnage;
    private Inventaire inventaire;

    //Construteur
    public Joueur(int id, String nom, String prenom, String pseudo, String motDePasse, int argent , Royaume royaume, Personnage personnage, Inventaire inventaire) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.pseudo = pseudo;
        this.motDePasse = motDePasse;
        this.argent = argent;
        this.royaume = royaume;
        this.personnage = personnage;
        this.inventaire = inventaire;
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
    public Inventaire getInventaire() {
        return inventaire;
    }
    public int getArgent() {
        return argent;
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
    public void setInventaire(Inventaire inventaire) {
        this.inventaire = inventaire;
    }
    public void setArgent(int argent) {
        this.argent = argent;
    }


    public void ajouterArgent(int montant) {
        if (montant > 0) {
            this.argent += montant;
        } else {
            System.out.println("Montant à ajouter doit être positif.");
        }
    }
    public void retirerArgent(int montant) {
        if (montant <= this.argent) {
            this.argent -= montant;
        } else {
            System.out.println("Pas assez d'argent.");
        }
    }

    public void acheterItem(Item item) {
        if (this.argent >= item.getPrix()) {
            // Déduit le prix de l'item du solde du joueur
            this.argent -= (int) item.getPrix();

            // Ajoute 1 item à l'inventaire
            this.inventaire.ajouterItem(item, 1);

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