package be.helha.projects.GuerreDesRoyaumes.Model.Personnage;

import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;

public class Guerrier extends Personnage {
    public Guerrier() {
        super("Guerrier",100, 40, 20, new Inventaire());
    }
}