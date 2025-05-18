package be.helha.projects.GuerreDesRoyaumes.Model;

import be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat.Competence;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Coffre;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Représente un joueur dans le jeu Guerre des Royaumes.
 * <p>
 * Un joueur possède un royaume, un personnage, un coffre, des compétences achetées,
 * ainsi que des statistiques telles que victoires, défaites et argent.
 * Il peut réaliser des actions telles qu'acheter des items ou des compétences,
 * gagner ou perdre de l'argent, et choisir des compétences avant un combat.
 * </p>
 */
public class Joueur {

    private int id;
    private String nom;
    private String prenom;
    private String pseudo;
    private String motDePasse;
    private int argent;
    private Royaume royaume;
    private Personnage personnage;
    private int victoires;
    private int defaites;
    private Coffre coffre;
    private Map<String, Competence> competencesAchetees; // Compétences achetées par le joueur

    /**
     * Constructeur complet.
     *
     * @param id Identifiant unique du joueur.
     * @param nom Nom de famille du joueur.
     * @param prenom Prénom du joueur.
     * @param pseudo Pseudonyme utilisé pour la connexion.
     * @param motDePasse Mot de passe utilisé pour la connexion.
     * @param argent Montant d'argent initial du joueur.
     * @param royaume Royaume associé au joueur.
     * @param personnage Personnage associé au joueur.
     * @param coffre Coffre contenant les items du joueur.
     * @param victoires Nombre de victoires du joueur.
     * @param defaites Nombre de défaites du joueur.
     */
    public Joueur(int id, String nom, String prenom, String pseudo, String motDePasse, int argent,
                  Royaume royaume, Personnage personnage, Coffre coffre, int victoires, int defaites) {
        this.id = id;
        this.nom = nom;
        this.prenom = prenom;
        this.pseudo = pseudo;
        this.motDePasse = motDePasse;
        this.argent = argent;
        this.royaume = royaume;
        this.personnage = personnage;
        this.victoires = victoires;
        this.defaites = defaites;
        this.coffre = coffre;
        this.competencesAchetees = new HashMap<>();
    }

    /**
     * Constructeur par défaut.
     */
    public Joueur() {}

    // --- Getters ---

    /**
     * @return L'identifiant unique du joueur.
     */
    public int getId() {
        return id;
    }

    /**
     * @return Le nom de famille du joueur.
     */
    public String getNom() {
        return nom;
    }

    /**
     * @return Le prénom du joueur.
     */
    public String getPrenom() {
        return prenom;
    }

    /**
     * @return Le pseudonyme du joueur.
     */
    public String getPseudo() {
        return pseudo;
    }

    /**
     * @return Le mot de passe du joueur.
     */
    public String getMotDePasse() {
        return motDePasse;
    }

    /**
     * @return Le royaume associé au joueur.
     */
    public Royaume getRoyaume() {
        return royaume;
    }

    /**
     * @return Le personnage associé au joueur.
     */
    public Personnage getPersonnage() {
        return personnage;
    }

    /**
     * @return Le montant d'argent du joueur.
     */
    public int getArgent() {
        return argent;
    }

    /**
     * @return Le nombre de victoires du joueur.
     */
    public int getVictoires() {
        return victoires;
    }

    /**
     * @return Le nombre de défaites du joueur.
     */
    public int getDefaites() {
        return defaites;
    }

    /**
     * @return Le coffre contenant les items du joueur.
     */
    public Coffre getCoffre() {
        return coffre;
    }

    /**
     * @return La map des compétences achetées par le joueur, avec leur identifiant en clé.
     */
    public Map<String, Competence> getCompetencesAchetees() {
        return competencesAchetees;
    }

    // --- Setters ---

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

    public void setVictoires(int victoires) {
        this.victoires = victoires;
    }

    public void setDefaites(int defaites) {
        this.defaites = defaites;
    }

    public void setCoffre(Coffre coffre) {
        this.coffre = coffre;
    }

    public void setCompetencesAchetees(Map<String, Competence> competencesAchetees) {
        this.competencesAchetees = competencesAchetees;
    }

    // --- Méthodes métier ---

    /**
     * Incrémente le nombre de victoires du joueur de 1.
     */
    public void ajouterVictoire() {
        this.victoires++;
    }

    /**
     * Incrémente le nombre de défaites du joueur de 1.
     */
    public void ajouterDefaite() {
        this.defaites++;
    }

    /**
     * Ajoute un montant positif d'argent au joueur.
     *
     * @param montant Montant à ajouter (doit être > 0).
     */
    public void ajouterArgent(int montant) {
        if (montant > 0) {
            this.argent += montant;
        } else {
            System.out.println("Montant à ajouter doit être positif.");
        }
    }

    /**
     * Retire un montant d'argent au joueur s'il dispose de suffisamment d'argent.
     *
     * @param montant Montant à retirer.
     */
    public void retirerArgent(int montant) {
        if (montant <= this.argent) {
            this.argent -= montant;
        } else {
            System.out.println("Pas assez d'argent.");
        }
    }

    /**
     * Fait gagner de l'argent au joueur, doublé si la compétence "Double Argent" est activée.
     *
     * @param montant Montant d'argent gagné.
     */
    public void gagnerArgent(int montant) {
        Competence doubleArgent = competencesAchetees.get("C3"); // ID de la compétence "Double Argent"
        if (doubleArgent != null) {
            montant *= 2;
            System.out.println("Compétence 'Double Argent' activée. L'argent gagné est doublé.");
        }
        ajouterArgent(montant);
        System.out.println("Le joueur gagne " + montant + " TerraCoin.");
    }

    /**
     * Permet au joueur d'acheter un item s'il a assez d'argent.
     * L'item est ajouté à son coffre.
     *
     * @param item Item à acheter.
     */
    public void acheterItem(Item item) {
        if (this.argent >= item.getPrix()) {
            this.argent -= item.getPrix();
            this.coffre.ajouterItem(item, 1);
            System.out.println("Achat réussi : " + item.getNom());
        } else {
            System.out.println("Pas assez d'argent pour acheter " + item.getNom());
        }
    }

    /**
     * Permet au joueur d'acheter une compétence s'il a assez d'argent.
     * La compétence est appliquée immédiatement au personnage.
     *
     * @param competence Compétence à acheter.
     */
    public void acheterCompetence(Competence competence) {
        if (this.argent >= competence.getPrix()) {
            this.argent -= competence.getPrix();
            this.competencesAchetees.put(competence.getId(), competence);
            competence.appliquerEffet(personnage);
            System.out.println("Compétence achetée et activée avec succès : " + competence.getNom());
        } else {
            System.out.println("Pas assez d'argent pour acheter " + competence.getNom());
        }
    }

    /**
     * Affiche les compétences disponibles et permet au joueur d'en choisir une
     * à utiliser avant un combat.
     */
    public void choisirCompetenceAvantCombat() {
        if (competencesAchetees.isEmpty()) {
            System.out.println("Vous n'avez pas de compétences à utiliser.");
            return;
        }

        System.out.println("Choisissez une compétence parmi celles que vous avez achetées :");
        int index = 1;
        for (Competence competence : competencesAchetees.values()) {
            System.out.println(index++ + ". " + competence.getNom() + " - " + competence.getDescription());
        }

        Scanner scanner = new Scanner(System.in);
        int choix = scanner.nextInt();

        if (choix > 0 && choix <= competencesAchetees.size()) {
            Competence competenceChoisie = (Competence) competencesAchetees.values().toArray()[choix - 1];
            competenceChoisie.appliquerEffet(personnage);
            System.out.println("Compétence " + competenceChoisie.getNom() + " activée !");
        } else {
            System.out.println("Choix invalide. Aucune compétence activée.");
        }
    }

    /**
     * Applique les effets de toutes les compétences achetées au personnage.
     */
    public void appliquerCompetencesAvantCombat() {
        for (Competence competence : competencesAchetees.values()) {
            competence.appliquerEffet(personnage);
        }
    }

    /**
     * Représentation textuelle du joueur.
     *
     * @return Chaîne contenant nom, prénom, pseudo, royaume, personnage et argent.
     */
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
