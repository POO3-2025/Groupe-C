package be.helha.projects.GuerreDesRoyaumes.Service;

import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;

/**
 * Interface définissant les services de gestion des inventaires de combat des joueurs.
 * <p>
 * Elle gère principalement les transferts d'items entre le coffre du joueur
 * et son inventaire de combat.
 * </p>
 */
public interface InventaireService {

    /**
     * Transfère un item depuis le coffre vers l'inventaire de combat du joueur.
     *
     * @param joueur Le joueur concerné.
     * @param itemId L'identifiant de l'item à transférer.
     * @return L'item transféré, ou null si le transfert a échoué.
     * @throws Exception En cas d'erreur pendant le transfert.
     */
    Item transfererDuCoffreVersInventaire(Joueur joueur, int itemId) throws Exception;

    /**
     * Transfère un item depuis l'inventaire de combat vers le coffre du joueur.
     *
     * @param joueur Le joueur concerné.
     * @param itemId L'identifiant de l'item à transférer.
     * @return true si le transfert a réussi, false sinon.
     * @throws Exception En cas d'erreur pendant le transfert.
     */
    boolean transfererDeInventaireVersCoffre(Joueur joueur, int itemId) throws Exception;
}
