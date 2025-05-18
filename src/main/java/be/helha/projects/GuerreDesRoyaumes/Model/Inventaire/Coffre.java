package be.helha.projects.GuerreDesRoyaumes.Model.Inventaire;

/**
 * Classe représentant le coffre d'un joueur dans le jeu Guerre des Royaumes.
 * <p>
 * Le coffre est un type spécifique de stockage avec une capacité plus grande,
 * ici fixé à 15 slots pour permettre au joueur de stocker davantage d'items.
 * </p>
 */
public class Coffre extends Stockage {

    /**
     * Constructeur par défaut.
     * Initialise un coffre avec une capacité maximale de 15 slots.
     */
    public Coffre() {
        super(15);  // 15 slots pour le coffre
    }
}
