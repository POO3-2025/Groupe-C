package be.helha.projects.GuerreDesRoyaumes.Service;

import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import java.util.List;

public interface ServiceBoutique {
    boolean acheterItem(int joueurId, int itemId, int quantite);
}