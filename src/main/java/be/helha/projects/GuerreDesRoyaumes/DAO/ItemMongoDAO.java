package be.helha.projects.GuerreDesRoyaumes.DAO;

import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import org.bson.Document;

import java.util.List;

public interface ItemMongoDAO {

    public void ajouterItem(Item item);
    public List<Item> obtenirTousLesItems();
}
