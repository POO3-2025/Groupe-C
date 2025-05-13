package be.helha.projects.GuerreDesRoyaumes.DAO;

import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import java.util.List;

/**
 * Interface définissant les opérations de persistance pour les entités Item.
 * Fournit des méthodes CRUD (Create, Read, Update, Delete) pour manipuler les données des items.
 */
public interface ItemDAO {
    /**
     * Ajoute un nouvel item dans la base de données.
     *
     * @param item L'item à ajouter
     */
    void ajouterItem(Item item);

    /**
     * Récupère un item par son identifiant.
     *
     * @param id L'identifiant de l'item à récupérer
     * @return L'item correspondant à l'identifiant ou null si aucun item n'est trouvé
     */
    Item obtenirItemParId(int id);

    /**
     * Récupère tous les items enregistrés dans la base de données.
     *
     * @return Une liste de tous les items
     */
    List<Item> obtenirTousLesItems();

    /**
     * Récupère tous les items d'un type spécifique.
     *
     * @param type Le type d'item à récupérer (arme, bouclier, potion, etc.)
     * @return Une liste des items du type spécifié
     */
    List<Item> obtenirItemsParType(String type);

    /**
     * Met à jour les informations d'un item existant.
     *
     * @param item L'item avec les nouvelles informations
     */
    void mettreAJourItem(Item item);

    /**
     * Supprime un item de la base de données.
     *
     * @param id L'identifiant de l'item à supprimer
     */
    void supprimerItem(int id);
}
