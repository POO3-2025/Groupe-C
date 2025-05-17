package be.helha.projects.GuerreDesRoyaumes.DAO;

import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;

import java.util.List;

public interface ItemMongoDAO {
    List<Item> obtenirItemsParType(String type);

    Item obtenirItemParId(int id);

    void ajouterItem(Item item);

    void mettreAJourItem(Item item);

    void supprimerItem(int id);

    void supprimerTousLesItems();

    List<Item> obtenirTousLesItems();
}
