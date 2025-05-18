package be.helha.projects.GuerreDesRoyaumes.Model.Items;

/**
 * Classe abstraite représentant un item dans le jeu Guerre des Royaumes.
 * <p>
 * Un item possède un identifiant, un nom, une quantité maximale,
 * un type, et un prix. Cette classe sert de base aux différents types d'items.
 * </p>
 */
public abstract class Item {

    private int id;
    private String nom;
    private int quantiteMax;
    private String type;
    private int prix;

    /**
     * Constructeur complet.
     *
     * @param id          Identifiant unique de l'item.
     * @param nom         Nom de l'item.
     * @param quantiteMax Quantité maximale que le joueur peut posséder.
     * @param type        Type de l'item (ex : "Potion", "Arme", etc.).
     * @param prix        Prix d'achat de l'item en TerraCoin.
     */
    public Item(int id, String nom, int quantiteMax, String type, int prix) {
        this.id = id;
        this.nom = nom;
        this.quantiteMax = quantiteMax;
        this.type = type;
        this.prix = prix;
    }

    // --- Getters ---

    /**
     * @return L'identifiant unique de l'item.
     */
    public int getId() {
        return id;
    }

    /**
     * @return Le nom de l'item.
     */
    public String getNom() {
        return nom;
    }

    /**
     * @return La quantité maximale que le joueur peut posséder de cet item.
     */
    public int getQuantiteMax() {
        return quantiteMax;
    }

    /**
     * @return Le type de l'item.
     */
    public String getType() {
        return type;
    }

    /**
     * @return Le prix d'achat de l'item en TerraCoin.
     */
    public int getPrix() {
        return prix;
    }

    // --- Setters ---

    /**
     * Définit l'identifiant unique de l'item.
     *
     * @param id Nouvel identifiant.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Définit le nom de l'item.
     *
     * @param nom Nouveau nom.
     */
    public void setNom(String nom) {
        this.nom = nom;
    }

    /**
     * Définit la quantité maximale que le joueur peut posséder.
     *
     * @param quantiteMax Nouvelle quantité maximale.
     */
    public void setQuantiteMax(int quantiteMax) {
        this.quantiteMax = quantiteMax;
    }

    /**
     * Définit le type de l'item.
     *
     * @param type Nouveau type.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Définit le prix d'achat de l'item.
     *
     * @param prix Nouveau prix.
     */
    public void setPrix(int prix) {
        this.prix = prix;
    }

    /**
     * Méthode abstraite représentant l'utilisation de l'item.
     * Doit être implémentée dans les classes dérivées.
     */
    public abstract void use();

    /**
     * Représentation textuelle de l'item.
     *
     * @return Une chaîne indiquant le nom, le type et le prix de l'item.
     */
    @Override
    public String toString() {
        return nom + " (" + type + ")" + " - Prix : " + prix + " TerraCoin";
    }
}
