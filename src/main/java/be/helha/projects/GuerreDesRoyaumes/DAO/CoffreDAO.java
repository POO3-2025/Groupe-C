package be.helha.projects.GuerreDesRoyaumes.DAO;

import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import java.util.List;

/**
 * Interface définissant les opérations de persistence pour les coffres des joueurs
 */
public interface CoffreDAO {

    /**
     * Récupère tous les items dans le coffre d'un joueur
     * @param nomJoueur Le nom du joueur
     * @return Liste des items dans le coffre
     */
    List<Item> obtenirItemsDuCoffre(String nomJoueur);

    /**
     * Ajoute un item au coffre d'un joueur
     * @param nomJoueur Le nom du joueur
     * @param item L'item à ajouter au coffre
     * @return true si l'ajout a réussi, false sinon
     */
    boolean ajouterItemAuCoffre(String nomJoueur, Item item);

    /**
     * Supprime un item du coffre d'un joueur
     * @param nomJoueur Le nom du joueur
     * @param itemId L'ID de l'item à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    boolean supprimerItemDuCoffre(String nomJoueur, int itemId);
}