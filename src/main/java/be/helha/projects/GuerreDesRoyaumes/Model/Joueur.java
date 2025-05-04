package be.helha.projects.GuerreDesRoyaumes.Model;

import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;
import be.helha.projects.GuerreDesRoyaumes.Model.Perssonnage.Personnage;

public class Joueur {
    private int id;
    private String nom;
    private String prenom;
    private String pseudo;
    private String email;
    private String motDePasse;
    private int argent;
    private Royaume royaume;
    private Personnage personnage;
    private Inventaire inventaire;

    //Construteur
    public Joueur(int id, String nom, String prenom, String pseudo, String email, String motDePasse, int argent , Royaume royaume, Personnage personnage, Inventaire inventaire) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.pseudo = pseudo;
        this.email = email;
        this.motDePasse = motDePasse;
        this.argent = argent;
        this.royaume = royaume;
        this.personnage = personnage;
        this.inventaire = inventaire;
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
    public String getEmail() {
        return email;
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
    public void setEmail(String email) {
        this.email = email;
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
        this.argent += montant;
    }
    public void retirerArgent(int montant) {
        if (this.argent >= montant) {
            this.argent -= montant;
        } else {
            System.out.println("Pas assez d'argent.");
        }
    }


    @Override
    public String toString() {
        return  "\nNom        = " + nom +
                "\nPrenom     = " + prenom +
                "\nPseudo     = " + pseudo +
                "\nEmail      = " + email +
                "\nRoyaume    = " + royaume +
                "\nPersonnage = " + personnage +
                "\nArgent     = " + argent;
    }
}