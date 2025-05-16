package be.helha.projects.GuerreDesRoyaumes.DAO;

import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import java.util.List;

/**
 * Interface définissant les opérations de persistence pour les inventaires de combat des joueurs
 */
public interface InventaireDAO {

    /**
     * Récupère tous les items dans l'inventaire de combat d'un joueur
     * @param nomJoueur Le nom du joueur
     * @return Liste des items dans l'inventaire
     */
    List<Item> obtenirItemsInventaire(String nomJoueur);

    /**
     * Ajoute un item à l'inventaire de combat d'un joueur
     * @param nomJoueur Le nom du joueur
     * @param item L'item à ajouter à l'inventaire
     * @return true si l'ajout a réussi, false sinon
     */
    boolean ajouterItemInventaire(String nomJoueur, Item item);

    /**
     * Supprime un item de l'inventaire de combat d'un joueur
     * @param nomJoueur Le nom du joueur
     * @param itemId L'ID de l'item à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    boolean supprimerItemInventaire(String nomJoueur, int itemId);

    /**
     * Vide l'inventaire de combat d'un joueur
     * @param nomJoueur Le nom du joueur
     * @return true si l'opération a réussi, false sinon
     */
    boolean viderInventaire(String nomJoueur);
}