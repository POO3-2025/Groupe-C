package be.helha.projects.GuerreDesRoyaumes.Model;

import org.bson.types.ObjectId;

/**
 * Classe représentant un royaume dans le jeu Guerre des Royaumes.
 * <p>
 * Un royaume est caractérisé par un identifiant, un nom et un niveau.
 * Chaque joueur possède un royaume qu'il peut développer au cours du jeu.
 * </p>
 */
public class Royaume {

    private int id;
    private String nom;
    private int niveau;

    /**
     * Constructeur par défaut.
     * <p>
     * Initialise un royaume avec des valeurs par défaut (non définies).
     * </p>
     */
    public Royaume() {}

    /**
     * Constructeur complet.
     *
     * @param id     Identifiant unique du royaume.
     * @param nom    Nom du royaume.
     * @param niveau Niveau actuel du royaume.
     */
    public Royaume(int id, String nom, int niveau) {
        this.id = id;
        this.nom = nom;
        this.niveau = niveau;
    }

    /**
     * Obtient l'identifiant unique du royaume.
     *
     * @return L'identifiant du royaume.
     */
    public int getId() {
        return id;
    }

    /**
     * Définit l'identifiant unique du royaume.
     *
     * @param id Nouvel identifiant du royaume.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Obtient le nom du royaume.
     *
     * @return Le nom du royaume.
     */
    public String getNom() {
        return nom;
    }

    /**
     * Définit le nom du royaume.
     *
     * @param nom Nouveau nom du royaume.
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Obtient le niveau actuel du royaume.
     *
     * @return Le niveau du royaume.
     */
    public int getNiveau() {
        return niveau;
    }

    /**
     * Définit le niveau actuel du royaume.
     *
     * @param niveau Nouveau niveau du royaume.
     */
    public void setNiveau(int niveau) {
        this.niveau = niveau;
    }
    
    /**
     * Incrémente le niveau du royaume de 1.
     */
    public void incrementNiveau() {
        this.niveau++;
    }

    /**
     * Retourne une représentation textuelle du royaume.
     *
     * @return Une chaîne contenant le nom et le niveau du royaume.
     */
    @Override
    public String toString() {
        return nom + " niveau " + niveau;
    }
    
    /**
     * Compare ce royaume avec un autre objet pour vérifier l'égalité.
     *
     * @param o L'objet à comparer avec ce royaume
     * @return true si l'objet est égal à ce royaume, false sinon
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        
        Royaume autre = (Royaume) o;
        return id == autre.id && 
               niveau == autre.niveau && 
               (nom != null ? nom.equals(autre.nom) : autre.nom == null);
    }
    
    /**
     * Calcule le code de hachage pour ce royaume.
     *
     * @return Le code de hachage
     */
    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + (nom != null ? nom.hashCode() : 0);
        result = 31 * result + niveau;
        return result;
    }
}
