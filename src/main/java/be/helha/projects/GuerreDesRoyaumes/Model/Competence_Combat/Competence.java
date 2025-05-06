package be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat;

import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;

public abstract class Competence implements AppliquerEffet {
    private String id;
    private String nom;
    private int prix;
    private String description;

    public Competence(String id, String nom, int prix, String description) {
        if (prix < 0) {
            throw new IllegalArgumentException("Le prix ne peut pas être négatif");
        }
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.description = description;
    }

    private void validatePrice(int prix) {
        if (prix < 0) {
            throw new IllegalArgumentException("Le prix ne peut pas être négatif");
        }
    }


    // Getters uniquement (immutable)
    public String getId() { return id; }
    public String getNom() { return nom; }
    public int getPrix() { return prix; }
    public String getDescription() { return description; }

    // Méthode abstraite pour appliquer l'effet (chaque compétence doit la définir)
    @Override
    public abstract void appliquerEffet(Personnage personnage);  // Applique l'effet spécifique sur le personnage

    public String toString() {
        return nom + " (" + description + ") - Prix : " + prix;
    }
}