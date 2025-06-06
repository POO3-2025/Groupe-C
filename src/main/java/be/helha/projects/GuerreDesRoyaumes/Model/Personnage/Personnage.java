package be.helha.projects.GuerreDesRoyaumes.Model.Personnage;

import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Arme;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Bouclier;

/**
 * Classe abstraite représentant un personnage dans le jeu Guerre des Royaumes.
 * <p>
 * Cette classe définit les attributs et comportements communs à tous les personnages,
 * tels que le nom, la vie, les dégâts, la résistance, et l'inventaire.
 * Elle impose également la définition des méthodes d'attaque, de défense,
 * d'utilisation d'objets, ainsi que de gestion des dégâts et soins.
 * </p>
 */
public abstract class Personnage {

    private String nom;
    private double vie;
    private double pointsDeVieMAX;
    private double degats;
    private double resistance;
    private Inventaire inventaire;

    /**
     * Constructeur complet.
     *
     * @param nom        Nom du personnage.
     * @param vie        Points de vie initiaux.
     * @param degats     Valeur des dégâts de base.
     * @param resistance Valeur de la résistance aux dégâts.
     * @param inventaire Inventaire associé au personnage.
     */
    public Personnage(String nom, int vie, int degats, int resistance, Inventaire inventaire) {
        this.nom = nom;
        this.vie = vie;
        pointsDeVieMAX = vie;
        this.degats = degats;
        this.resistance = resistance;
        this.inventaire = inventaire;
    }

    /**
     * Constructeur par défaut.
     */
    public Personnage() {
    }

    // --- Getters ---

    /**
     * @return Le nom du personnage.
     */
    public String getNom() {
        return nom;
    }

    /**
     * @return Les points de vie actuels du personnage.
     */
    public double getVie() {
        return vie;
    }

    /**
     * Alias pour getVie, utilisé pour compatibilité.
     *
     * @return Les points de vie actuels du personnage.
     */
    public double getPointsDeVie() {
        return vie;
    }

    /**
     * @return La valeur des dégâts de base du personnage.
     */
    public double getDegats() {
        return degats;
    }

    /**
     * @return La valeur de la résistance aux dégâts.
     */
    public double getResistance() {
        return resistance;
    }

    /**
     * @return L'inventaire associé au personnage.
     */
    public Inventaire getInventaire() {
        return inventaire;
    }
    public double getPointsDeVieMAX() {
        return pointsDeVieMAX;
    }

    // --- Setters ---

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setVie(double vie) {
        this.vie = vie;
    }



    public void setDegats(double degats) {
        this.degats = degats;
    }

    public void setResistance(double resistance) {
        this.resistance = resistance;
    }

    public void setInventaire(Inventaire inventaire) {
        this.inventaire = inventaire;
    }
    public void setPointsDeVieMAX(double pointsDeVieMAX) {
        this.pointsDeVieMAX = pointsDeVieMAX;
    }

    // --- Méthodes abstraites ---

    /**
     * Le personnage subit des dégâts (à implémenter selon le type de personnage).
     *
     * @param degatsSubis Nombre de dégâts initiaux subis.
     */
    public abstract void subirDegats(double degatsSubis);

    /**
     * Le personnage se soigne d'un certain nombre de points de vie.
     *
     * @param pointsSoin Points de vie à restaurer.
     */
    public abstract void soigner(double pointsSoin);

    /**
     * Le personnage effectue une attaque standard.
     */
    public abstract void attaquer();

    /**
     * Le personnage effectue une attaque spéciale.
     */
    public abstract void attaquerSpecial();

    /**
     * Le personnage effectue une action de défense.
     */
    public abstract void defense();

    /**
     * Le personnage utilise un objet de son inventaire.
     */
    public abstract void UtilisationObjet();

    /**
     * Représentation textuelle du personnage, incluant nom, vie, dégâts et résistance.
     *
     * @return Chaîne décrivant le personnage.
     */
    @Override
    public String toString() {
        return nom + " vie = " + vie + " degats = " + degats + " resistance = " + resistance;
    }

    // Alias pour setVie pour compatibilité avec le reste du code
    public void setPointsDeVie(double pointsDeVie) {
        this.vie = pointsDeVie;
    }

    }

