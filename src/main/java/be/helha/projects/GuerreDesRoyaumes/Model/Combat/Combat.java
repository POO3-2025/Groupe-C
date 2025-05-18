package be.helha.projects.GuerreDesRoyaumes.Model.Combat;

import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import java.time.LocalDateTime;

/**
 * Classe représentant un combat entre deux joueurs dans le jeu Guerre des Royaumes.
 * <p>
 * Cette classe stocke les informations essentielles d'un combat,
 * notamment les joueurs impliqués, le vainqueur, le nombre de tours,
 * ainsi que la date et l'heure du combat.
 * </p>
 */
public class Combat {

    private int id;
    private Joueur joueur1;
    private Joueur joueur2;
    private Joueur vainqueur; // null si égalité
    private int nombreTours;
    private LocalDateTime dateHeure;

    /**
     * Constructeur complet.
     *
     * @param id          Identifiant unique du combat.
     * @param joueur1     Premier joueur participant au combat.
     * @param joueur2     Deuxième joueur participant au combat.
     * @param vainqueur   Vainqueur du combat (null si égalité).
     * @param nombreTours Nombre total de tours effectués dans le combat.
     * @param dateHeure   Date et heure à laquelle le combat a eu lieu.
     */
    public Combat(int id, Joueur joueur1, Joueur joueur2, Joueur vainqueur, int nombreTours, LocalDateTime dateHeure) {
        this.id = id;
        this.joueur1 = joueur1;
        this.joueur2 = joueur2;
        this.vainqueur = vainqueur;
        this.nombreTours = nombreTours;
        this.dateHeure = dateHeure;
    }

    // --- Getters ---

    /**
     * @return L'identifiant unique du combat.
     */
    public int getId() {
        return id;
    }

    /**
     * @return Le premier joueur participant.
     */
    public Joueur getJoueur1() {
        return joueur1;
    }

    /**
     * @return Le deuxième joueur participant.
     */
    public Joueur getJoueur2() {
        return joueur2;
    }

    /**
     * @return Le vainqueur du combat, ou null en cas d'égalité.
     */
    public Joueur getVainqueur() {
        return vainqueur;
    }

    /**
     * @return Le nombre total de tours effectués dans le combat.
     */
    public int getNombreTours() {
        return nombreTours;
    }

    /**
     * @return La date et l'heure du combat.
     */
    public LocalDateTime getDateHeure() {
        return dateHeure;
    }

    // --- Setters ---

    /**
     * Définit l'identifiant unique du combat.
     *
     * @param id Nouvel identifiant.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Définit le premier joueur participant.
     *
     * @param joueur1 Nouveau premier joueur.
     */
    public void setJoueur1(Joueur joueur1) {
        this.joueur1 = joueur1;
    }

    /**
     * Définit le deuxième joueur participant.
     *
     * @param joueur2 Nouveau deuxième joueur.
     */
    public void setJoueur2(Joueur joueur2) {
        this.joueur2 = joueur2;
    }

    /**
     * Définit le vainqueur du combat.
     *
     * @param vainqueur Nouveau vainqueur, ou null en cas d'égalité.
     */
    public void setVainqueur(Joueur vainqueur) {
        this.vainqueur = vainqueur;
    }

    /**
     * Définit le nombre total de tours du combat.
     *
     * @param nombreTours Nouveau nombre de tours.
     */
    public void setNombreTours(int nombreTours) {
        this.nombreTours = nombreTours;
    }

    /**
     * Définit la date et l'heure du combat.
     *
     * @param dateHeure Nouvelle date et heure.
     */
    public void setDateHeure(LocalDateTime dateHeure) {
        this.dateHeure = dateHeure;
    }
}
