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

    public int getNiveauRoyaume() {
        return royaume.getNiveau();  // Assurez-vous que le royaume a une méthode getNiveau()
    }

    public void setNiveauRoyaume(int niveau) {
        royaume.setNiveau(niveau); // Assurez-vous que le royaume a une méthode setNiveau()
    }
    // Méthode pour gérer la victoire ou la défaite
    public void gestionCombat(Joueur adversaire, boolean aGagne) {
        // Si le joueur a gagné
        if (aGagne) {
            // Le gagnant reçoit 250 d'argent
            this.ajouterArgent(250);

            // Le gagnant gagne un niveau de royaume
            this.setNiveauRoyaume(this.getNiveauRoyaume() + 1);

            System.out.println(this.nom + " a gagné et a gagné 250 d'argent et un niveau de royaume.");
        } else {
            // Le perdant perd un niveau de royaume, mais ne descend pas en dessous de 1
            int niveauPerdant = adversaire.getNiveauRoyaume();
            if (niveauPerdant > 1) {
                adversaire.setNiveauRoyaume(niveauPerdant - 1);
                System.out.println(adversaire.nom + " a perdu un niveau de royaume.");
            }
        }
    }
}