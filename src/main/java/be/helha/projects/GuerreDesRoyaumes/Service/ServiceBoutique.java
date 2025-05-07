package be.helha.projects.GuerreDesRoyaumes.Service;

import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import java.util.List;

public interface ServiceBoutique {
    List<Item> obtenirTousLesItems();

    Item obtenirItemParId(int id);

    List<Item> obtenirItemsParType(String type);

    boolean acheterItem(int joueurId, int itemId, int quantite);
}