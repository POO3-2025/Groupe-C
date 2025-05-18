package be.helha.projects.GuerreDesRoyaumes.Model.Inventaire;

import be.helha.projects.GuerreDesRoyaumes.Model.Items.Arme;

/**
 * Classe représentant l'inventaire d'un personnage dans le jeu Guerre des Royaumes.
 * <p>
 * L'inventaire est un type spécifique de stockage avec une capacité maximale
 * de 5 slots, utilisée notamment pour le combat.
 * </p>
 */
public class Inventaire extends Stockage {

    /**
     * Nombre maximal de slots dans l'inventaire (5 pour le combat).
     */
    private static final int MAX_SLOTS_COMBAT = 5;

    /**
     * Constructeur par défaut.
     * Initialise un inventaire avec une capacité maximale de 5 slots.
     */
    public Inventaire() {
        super(MAX_SLOTS_COMBAT);
    }
}
