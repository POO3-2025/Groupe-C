package be.helha.projects.GuerreDesRoyaumes.DAO;

import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import java.util.List;

public interface ItemDAO {
    void ajouterItem(Item item);
    Item obtenirItemParId(int id);
    List<Item> obtenirTousLesItems();
    List<Item> obtenirItemsParType(String type);
    void mettreAJourItem(Item item);
    void supprimerItem(int id);
}
