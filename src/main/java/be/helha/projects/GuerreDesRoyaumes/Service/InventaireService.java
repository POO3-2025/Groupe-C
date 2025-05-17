package be.helha.projects.GuerreDesRoyaumes.Service;

import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;

/**
 * Interface définissant les services de gestion des inventaires de combat des joueurs.
 * Gère principalement les transferts entre le coffre et l'inventaire de combat.
 */
public interface InventaireService {

    /**
     * Transfère un item du coffre vers l'inventaire de combat
     *
     * @param joueur Le joueur concerné
     * @param itemId L'ID de l'item à transférer
     * @return true si le transfert a réussi, false sinon
     */
    boolean transfererDuCoffreVersInventaire(Joueur joueur, int itemId);

    /**
     * Transfère un item de l'inventaire de combat vers le coffre
     *
     * @param joueur Le joueur concerné
     * @param itemId L'ID de l'item à transférer
     * @return true si le transfert a réussi, false sinon
     */
    boolean transfererDeInventaireVersCoffre(Joueur joueur, int itemId);
}