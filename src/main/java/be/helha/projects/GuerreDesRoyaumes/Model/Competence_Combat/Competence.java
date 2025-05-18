package be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat;

import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;

/**
 * Classe abstraite représentant une compétence dans le jeu Guerre des Royaumes.
 * <p>
 * Chaque compétence possède un identifiant unique, un nom, un prix, et une description.
 * Cette classe implémente l'interface {@link AppliquerEffet} qui impose la méthode
 * {@code appliquerEffet} pour définir l'effet de la compétence sur un personnage.
 * </p>
 * <p>
 * Le prix d'une compétence doit être positif ou nul, une exception est levée sinon.
 * </p>
 */
public abstract class Competence implements AppliquerEffet {

    private String id;
    private String nom;
    private int prix;
    private String description;

    /**
     * Constructeur complet.
     *
     * @param id          Identifiant unique de la compétence.
     * @param nom         Nom de la compétence.
     * @param prix        Prix d'achat de la compétence (doit être >= 0).
     * @param description Description textuelle de la compétence.
     * @throws IllegalArgumentException si le prix est négatif.
     */
    public Competence(String id, String nom, int prix, String description) {
        if (prix < 0) {
            throw new IllegalArgumentException("Le prix ne peut pas être négatif");
        }
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.description = description;
    }

    /**
     * Validation du prix (privée, non utilisée ici mais prête pour extension).
     *
     * @param prix Prix à valider.
     * @throws IllegalArgumentException si le prix est négatif.
     */
    private void validatePrice(int prix) {
        if (prix < 0) {
            throw new IllegalArgumentException("Le prix ne peut pas être négatif");
        }
    }

    // --- Getters ---

    /**
     * @return L'identifiant unique de la compétence.
     */
    public String getId() {
        return id;
    }

    /**
     * @return Le nom de la compétence.
     */
    public String getNom() {
        return nom;
    }

    /**
     * @return Le prix d'achat de la compétence.
     */
    public int getPrix() {
        return prix;
    }

    /**
     * @return La description textuelle de la compétence.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Applique l'effet spécifique de la compétence sur un personnage.
     * <p>
     * Cette méthode est abstraite et doit être définie dans chaque sous-classe.
     * </p>
     *
     * @param personnage Personnage cible de la compétence.
     */
    @Override
    public abstract void appliquerEffet(Personnage personnage);

    /**
     * Représentation textuelle de la compétence.
     *
     * @return Une chaîne contenant le nom, la description et le prix.
     */
    @Override
    public String toString() {
        return nom + " (" + description + ") - Prix : " + prix;
    }
}
