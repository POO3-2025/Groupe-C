package be.helha.projects.GuerreDesRoyaumes.Model.Items;

/**
 * Classe représentant un bouclier dans le jeu Guerre des Royaumes.
 * <p>
 * Le bouclier est un type d'item défensif qui apporte une valeur de défense supplémentaire.
 * Il hérite de la classe Item et ajoute une propriété spécifique "defense".
 * </p>
 */
public class Bouclier extends Item {

    private double defense;

    /**
     * Constructeur complet avec identifiant.
     *
     * @param id          Identifiant unique du bouclier.
     * @param nom         Nom du bouclier.
     * @param quantiteMax Quantité maximale que le joueur peut posséder.
     * @param prix        Prix d'achat du bouclier.
     * @param defense     Valeur de la défense apportée par le bouclier.
     */
    public Bouclier(int id, String nom, int quantiteMax, int prix, double defense) {
        super(id, nom, quantiteMax, "Bouclier", prix);
        this.defense = defense;
    }

    /**
     * Constructeur sans identifiant, utilisé pour la création initiale.
     *
     * @param nom         Nom du bouclier.
     * @param quantiteMax Quantité maximale que le joueur peut posséder.
     * @param prix        Prix d'achat du bouclier.
     * @param defense     Valeur de la défense apportée par le bouclier.
     */
    public Bouclier(String nom, int quantiteMax, int prix, double defense) {
        super(0, nom, quantiteMax, "Bouclier", prix);
        this.defense = defense;
    }

    /**
     * Obtient la valeur de défense du bouclier.
     *
     * @return La valeur de défense.
     */
    public double getDefense() {
        return defense;
    }

    /**
     * Définit la valeur de défense du bouclier.
     *
     * @param defense Nouvelle valeur de défense.
     */
    public void setDefense(double defense) {
        this.defense = defense;
    }

    /**
     * Utilisation du bouclier.
     * <p>
     * Affiche un message indiquant l'utilisation du bouclier.
     * La logique d'équipement et d'application sur le personnage
     * doit être implémentée (par exemple, sélection lors du combat).
     * </p>
     */
    @Override
    public void use() {
        System.out.println("Vous utilisez " + getNom());
        // TODO Logique d'équipement du bouclier à appliquer directement au personnage.
        // TODO Le joueur devra rechoisir le bouclier à chaque combat.
    }

    /**
     * Représentation textuelle du bouclier.
     *
     * @return Une chaîne indiquant le nom et la valeur de défense du bouclier.
     */
    @Override
    public String toString() {
        return getNom() + " – Défense : " + defense;
    }
}
