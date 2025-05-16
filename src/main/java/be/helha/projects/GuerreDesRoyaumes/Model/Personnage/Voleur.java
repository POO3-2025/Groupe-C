package be.helha.projects.GuerreDesRoyaumes.Model.Personnage;

import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;

public class Voleur extends Personnage {
    public Voleur() {
        super("Voleur",90, 0, 15, new Inventaire());
    }
}

// TODO Le voleur doit gagner 2x plus d'argent à la fin d'un combat