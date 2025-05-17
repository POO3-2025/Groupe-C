package be.helha.projects.GuerreDesRoyaumes.Model;

import org.bson.types.ObjectId;

/**
 * Classe représentant un royaume dans le jeu Guerre des Royaumes.
 * Un royaume est caractérisé par un identifiant, un nom et un niveau.
 * Chaque joueur possède un royaume qu'il peut développer au cours du jeu.
 */
public class Royaume {

    private int id;
    private String nom;
    private int niveau;

    /**
     * Constructeur par défaut pour créer un royaume avec des valeurs par défaut.
     */
    public Royaume() {}

    /**
     * Constructeur pour créer un royaume avec toutes ses propriétés.
     *
     * @param id Identifiant unique du royaume
     * @param nom Nom du royaume
     * @param niveau Niveau actuel du royaume
     */
    public Royaume(int id, String nom, int niveau) {
        this.id = id;
        this.nom = nom;
        this.niveau = niveau;
    }

    // Getters
    /**
     * @return L'identifiant du royaume
     */
    public int getId() {
        return id;
    }

    /**
     * @return Le nom du royaume
     */
    public String getNom() {
        return nom;
    }

    /**
     * @return Le niveau actuel du royaume
     */
    public int getNiveau() {
        return niveau;
    }

    // Setters
    /**
     * @param id Le nouvel identifiant du royaume
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @param nom Le nouveau nom du royaume
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * @param niveau Le nouveau niveau du royaume
     */
    public void setNiveau(int niveau) {
        this.niveau = niveau;
    }

    /**
     * Retourne une représentation textuelle du royaume.
     *
     * @return Une chaîne contenant le nom et le niveau du royaume
     */
    @Override
    public String toString() {
        return nom + " niveau " + niveau;
    }
}
