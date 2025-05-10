package be.helha.projects.GuerreDesRoyaumes.Model;

import be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat.Competence;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Coffre;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Voleur;
import ch.qos.logback.classic.util.LogbackMDCAdapter;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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
    private Map<String, Competence> competencesAchetees; // Stocke les compétences achetées


    //Construteur
    public Joueur(int id, String nom, String prenom, String pseudo, String motDePasse, int argent, Royaume royaume, Personnage personnage, Coffre coffre) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.pseudo = pseudo;
        this.motDePasse = motDePasse;
        this.argent = argent;
        this.royaume = royaume;
        this.personnage = personnage;
        this.coffre = coffre;
        this.competencesAchetees = new HashMap<>(); // Initialisation de la liste des compétences achetées
    }

    public Joueur(int id, String nom, String prenom, String pseudo, String motDePasse, int argent) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.pseudo = pseudo;
        this.motDePasse = motDePasse;
        this.argent = argent;
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

    public Map<String, Competence> getCompetencesAchetees() {
        return competencesAchetees;
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

    public void setCompetencesAchetees(Map<String, Competence> competencesAchetees) {
        this.competencesAchetees = competencesAchetees;
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

    // Méthode pour gagner de l'argent avec vérification des compétences actives (comme DoubleArgent)
    public void gagnerArgent(int montant) {
        // Vérifier si la compétence "DoubleArgent" est active
        Competence doubleArgent = competencesAchetees.get("C3"); // C3 correspond à l'ID de DoubleArgent
        if (doubleArgent != null) {
            montant *= 2;  // Si la compétence DoubleArgent est achetée, doubler l'argent gagné
            System.out.println("Compétence 'Double Argent' activée. L'argent gagné est doublé.");
        }
        ajouterArgent(montant); // Ajoute l'argent gagné
        System.out.println("Le joueur gagne " + montant + " TerraCoin.");
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

    // Méthode pour acheter une compétence
    public void acheterCompetence(Competence competence) {
        if (this.argent >= competence.getPrix()) {
            this.argent -= competence.getPrix();  // Déduit l'argent du joueur
            this.competencesAchetees.put(competence.getId(), competence);  // Ajoute la compétence aux compétences achetées
            // Appliquer directement la compétence au personnage
            competence.appliquerEffet(personnage);
            System.out.println("Compétence achetée et activée avec succès : " + competence.getNom());
        } else {
            System.out.println("Pas assez d'argent pour acheter " + competence.getNom());
        }
    }

    // Méthode pour permettre au joueur de choisir une compétence avant chaque tour
    public void choisirCompetenceAvantCombat() {
        // Afficher toutes les compétences disponibles
        if (competencesAchetees.isEmpty()) {
            System.out.println("Vous n'avez pas de compétences à utiliser.");
            return;  // Aucune compétence disponible
        }

        System.out.println("Choisissez une compétence parmi celles que vous avez achetées :");

        // Liste des compétences achetées
        int index = 1;
        for (Competence competence : competencesAchetees.values()) {
            System.out.println(index++ + ". " + competence.getNom() + " - " + competence.getDescription());
        }

        // Demander à l'utilisateur de choisir une compétence
        Scanner scanner = new Scanner(System.in);
        int choix = scanner.nextInt();

        // Vérifier si le choix est valide
        if (choix > 0 && choix <= competencesAchetees.size()) {
            Competence competenceChoisie = (Competence) competencesAchetees.values().toArray()[choix - 1];
            competenceChoisie.appliquerEffet(personnage);  // Appliquer l'effet de la compétence choisie
            System.out.println("Compétence " + competenceChoisie.getNom() + " activée !");
        } else {
            System.out.println("Choix invalide. Aucune compétence activée.");
        }
    }

    public void appliquerCompetencesAvantCombat() {
        for (Competence competence : competencesAchetees.values()) {
            competence.appliquerEffet(personnage);  // Applique chaque compétence achetée sur le personnage
        }
    }

    @Override
    public String toString() {
        return "\nNom        = " + nom +
                "\nPrenom     = " + prenom +
                "\nPseudo     = " + pseudo +
                "\nRoyaume    = " + royaume +
                "\nPersonnage = " + personnage +
                "\nArgent     = " + argent;
    }
}