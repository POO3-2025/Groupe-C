package be.helha.projects.GuerreDesRoyaumes.Model.Items;

import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;

/**
 * Classe représentant une arme dans le jeu Guerre des Royaumes.
 * <p>
 * Une arme est un type d'item offensif qui apporte une valeur de dégâts supplémentaire.
 * Elle hérite de la classe Item et ajoute une propriété spécifique "degats".
 * </p>
 */
public class Arme extends Item {

    private double degats;

    /**
     * Constructeur complet avec identifiant.
     *
     * @param id          Identifiant unique de l'arme.
     * @param nom         Nom de l'arme.
     * @param quantiteMax Quantité maximale que le joueur peut posséder.
     * @param prix        Prix d'achat de l'arme.
     * @param degats      Valeur des dégâts apportés par l'arme.
     */
    public Arme(int id, String nom, int quantiteMax, int prix, double degats) {
        super(id, nom, quantiteMax, "Arme", prix);
        this.degats = degats;
    }

    /**
     * Constructeur sans identifiant, utilisé pour la création initiale.
     *
     * @param nom         Nom de l'arme.
     * @param quantiteMax Quantité maximale que le joueur peut posséder.
     * @param degats      Valeur des dégâts apportés par l'arme.
     * @param prix        Prix d'achat de l'arme.
     */
    public Arme(String nom, int quantiteMax, double degats, int prix) {
        super(0, nom, quantiteMax, "Arme", prix);
        this.degats = degats;
    }

    /**
     * Obtient la valeur des dégâts de l'arme.
     *
     * @return Les dégâts de l'arme.
     */
    public double getDegats() {
        return degats;
    }

    /**
     * Définit la valeur des dégâts de l'arme.
     *
     * @param degats Nouvelle valeur des dégâts.
     */
    public void setDegats(double degats) {
        this.degats = degats;
    }

    /**
     * Utilisation de l'arme.
     * <p>
     * Affiche un message indiquant l'utilisation de l'arme.
     * La logique d'application au personnage (augmentation des dégâts)
     * doit être implémentée.
     * </p>
     */
    public void use() {
        System.out.println("Vous utilisez " + getNom());
        // TODO Logique d'utilisation de l'arme
        // TODO Appliquer l'augmentation des dégâts au personnage :
        // personnage.setDegats(personnage.getDegats() + degats);
    }

    /**
     * Représentation textuelle de l'arme.
     *
     * @return Une chaîne indiquant le nom et la valeur des dégâts de l'arme.
     */
    @Override
    public String toString() {
        return getNom() + " – Dégâts : " + degats;
    }
}
