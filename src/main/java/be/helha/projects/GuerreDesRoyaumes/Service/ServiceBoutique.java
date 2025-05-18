package be.helha.projects.GuerreDesRoyaumes.Service;

import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import java.util.List;

/**
 * Interface définissant les services liés à la boutique du jeu Guerre des Royaumes.
 * <p>
 * Permet notamment l'achat d'items par les joueurs.
 * </p>
 */
public interface ServiceBoutique {

    /**
     * Permet à un joueur d'acheter une quantité donnée d'un item.
     *
     * @param joueurId L'identifiant du joueur effectuant l'achat.
     * @param itemId   L'identifiant de l'item à acheter.
     * @param quantite La quantité d'items à acheter.
     * @return true si l'achat a réussi, false sinon (ex: fonds insuffisants).
     */
    boolean acheterItem(int joueurId, int itemId, int quantite);
}
