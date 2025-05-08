package be.helha.projects.GuerreDesRoyaumes.Service;

import be.helha.projects.GuerreDesRoyaumes.Model.Combat;
import java.util.List;

public interface ServiceCombat {
    boolean choisirItemsPourCombat(int joueurId, List<Integer> itemIds);
    boolean lancerCombat(int joueurId, int adversaireId, List<Integer> itemsChoisis);
    void distribuerRecompenses(int combatId);
}
