package be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat;

import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;

public abstract class Competence implements AppliquerEffet {
    private String id;
    private String nom;
    private int prix;
    private String description;
    private String type;

    public Competence(String id, String nom, int prix, String description, String type) {
        validatePrice(prix);
        this.id = id;
        this.nom = nom;
        this.prix = prix;
        this.description = description;
        this.type = type;
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
    public String getType() { return type; }

    //TODO appliquer l'effet une fois qu'il est acheter et que la partie commence l'effet est actif pendant toute la partie
    // public abstract void appliquerEffet( );


    // Méthode abstraite pour appliquer l'effet (chaque compétence doit la définir)
    @Override
    public abstract void appliquerEffet(Personnage personnage);  // Applique l'effet spécifique sur le personnage

}