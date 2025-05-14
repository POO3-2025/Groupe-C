// CombatDTO.java
package be.helha.projects.GuerreDesRoyaumes.DTO;

import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;

public class CombatDTO {
    private Joueur joueur1;
    private Joueur joueur2;
    private String statutTour;
    private String resultatPartiel;

    // Getters/Setters
    public Joueur getJoueur1() { return joueur1; }
    public void setJoueur1(Joueur joueur1) { this.joueur1 = joueur1; }

    public Joueur getJoueur2() { return joueur2; }
    public void setJoueur2(Joueur joueur2) { this.joueur2 = joueur2; }

    public String getStatutTour() { return statutTour; }
    public void setStatutTour(String statutTour) { this.statutTour = statutTour; }

    public String getResultatPartiel() { return resultatPartiel; }
    public void setResultatPartiel(String resultatPartiel) { this.resultatPartiel = resultatPartiel; }
}