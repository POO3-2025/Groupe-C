package be.helha.projects.GuerreDesRoyaumes.Model.Combat;

import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;

import java.time.LocalDateTime;

public class Combat {
    private int id;
    private Joueur joueur1;
    private Joueur joueur2;
    private Joueur vainqueur; // null si égalité
    private int nombreTours;
    private LocalDateTime dateHeure;

    // Constructeur
    public Combat(int id, Joueur joueur1, Joueur joueur2, Joueur vainqueur, int nombreTours, LocalDateTime dateHeure) {
        this.id = id;
        this.joueur1 = joueur1;
        this.joueur2 = joueur2;
        this.vainqueur = vainqueur;
        this.nombreTours = nombreTours;
        this.dateHeure = dateHeure;
    }

    // Getters et Setters
    public int getId() { return id; }
    public Joueur getJoueur1() { return joueur1; }
    public Joueur getJoueur2() { return joueur2; }
    public Joueur getVainqueur() { return vainqueur; }
    public int getNombreTours() { return nombreTours; }
    public LocalDateTime getDateHeure() { return dateHeure; }

    public void setId(int id) { this.id = id; }
    public void setJoueur1(Joueur joueur1) { this.joueur1 = joueur1; }
    public void setJoueur2(Joueur joueur2) { this.joueur2 = joueur2; }
    public void setVainqueur(Joueur vainqueur) { this.vainqueur = vainqueur; }
    public void setNombreTours(int nombreTours) { this.nombreTours = nombreTours; }
    public void setDateHeure(LocalDateTime dateHeure) { this.dateHeure = dateHeure; }
}
