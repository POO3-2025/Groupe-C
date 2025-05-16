package be.helha.projects.GuerreDesRoyaumes.DAO;

import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;

import java.util.List;

public interface ItemMongoDAO {
    public List<Item> obtenirTousLesItems();
}
