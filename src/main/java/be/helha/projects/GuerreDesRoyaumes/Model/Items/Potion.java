package be.helha.projects.GuerreDesRoyaumes.Model.Items;

/**
 * Classe représentant une potion dans le jeu Guerre des Royaumes.
 * <p>
 * Une potion est un type d'item pouvant infliger des dégâts ou soigner.
 * Elle hérite de la classe Item et ajoute des propriétés spécifiques
 * telles que les dégâts et les points de soin.
 * </p>
 */
public class Potion extends Item {

    private double degats;
    private double soin;

    /**
     * Constructeur complet avec identifiant.
     *
     * @param id          Identifiant unique de la potion.
     * @param nom         Nom de la potion.
     * @param quantiteMax Quantité maximale que le joueur peut posséder.
     * @param prix        Prix d'achat de la potion.
     * @param degats      Valeur des dégâts infligés par la potion.
     * @param soin        Valeur des points de vie restaurés par la potion.
     */
    public Potion(int id, String nom, int quantiteMax, int prix, double degats, double soin) {
        super(id, nom, quantiteMax, "Potion", prix);
        this.degats = degats;
        this.soin = soin;
    }

    /**
     * Constructeur sans identifiant, utilisé pour la création initiale.
     *
     * @param nom         Nom de la potion.
     * @param quantiteMax Quantité maximale que le joueur peut posséder.
     * @param prix        Prix d'achat de la potion.
     * @param degats      Valeur des dégâts infligés par la potion.
     * @param soin        Valeur des points de vie restaurés par la potion.
     */
    public Potion(String nom, int quantiteMax, int prix, double degats, double soin) {
        super(0, nom, quantiteMax, "Potion", prix);
        this.degats = degats;
        this.soin = soin;
    }

    /**
     * Obtient la valeur des dégâts infligés par la potion.
     *
     * @return Les dégâts de la potion.
     */
    public double getDegats() {
        return degats;
    }

    /**
     * Définit la valeur des dégâts infligés par la potion.
     *
     * @param degats Nouvelle valeur des dégâts.
     */
    public void setDegats(double degats) {
        this.degats = degats;
    }

    /**
     * Obtient la valeur des points de soin de la potion.
     *
     * @return Les points de soin.
     */
    public double getSoin() {
        return soin;
    }

    /**
     * Définit la valeur des points de soin de la potion.
     *
     * @param soin Nouvelle valeur des points de soin.
     */
    public void setSoin(double soin) {
        this.soin = soin;
    }

    /**
     * Utilisation de la potion.
     * <p>
     * Affiche un message indiquant l'utilisation de la potion.
     * La logique spécifique d'effet doit être implémentée.
     * </p>
     */
    @Override
    public void use() {
        System.out.println("Vous utilisez " + getNom());
        // TODO Logique d'utilisation de la potion
    }
}
