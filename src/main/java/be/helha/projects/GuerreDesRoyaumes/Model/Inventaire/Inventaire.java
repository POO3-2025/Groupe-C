package be.helha.projects.GuerreDesRoyaumes.Model.Inventaire;

import be.helha.projects.GuerreDesRoyaumes.Model.Items.Arme;

public class Inventaire extends Stockage {
    // Pour le combat, on veut 5 slots maximum (+1 slot pour une compétence)
    private static final int MAX_SLOTS_COMBAT = 5;

    // Constructeur
    public Inventaire() {
        super(MAX_SLOTS_COMBAT);
    }

}
