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

    public Joueur(int id, String nom, String prenom, String pseudo, String email, String motDePasse, String argent, int point_de_vie, Royaume royaume, Personnage personnage, Competence competence_Depart, Inventaire inventaire) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.pseudo = pseudo;
        this.email = email;
        this.motDePasse = motDePasse;
        this.argent = argent;
        this.point_de_vie = point_de_vie;
        this.royaume = royaume;
        this.personnage = personnage;
        this.competence_Depart = competence_Depart;
        this.inventaire = inventaire;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public Royaume getRoyaume() {
        return royaume;
    }

    public void setRoyaume(Royaume royaume) {
        this.royaume = royaume;
    }

    public Personnage getPersonnage() {
        return personnage;
    }

    public void setPersonnage(Personnage personnage) {
        this.personnage = personnage;
    }

    public Inventaire getInventaire() {
        return inventaire;
    }

    public void setInventaire(Inventaire inventaire) {
        this.inventaire = inventaire;
    }

    public int getArgent() {
        return argent;
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

}