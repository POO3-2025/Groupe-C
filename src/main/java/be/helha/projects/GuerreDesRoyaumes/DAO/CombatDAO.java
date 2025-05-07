package be.helha.projects.GuerreDesRoyaumes.DAO;

import be.helha.projects.GuerreDesRoyaumes.Model.Combat;
import java.util.List;

public interface CombatDAO {
    void ajouterCombat(Combat combat);
    Combat obtenirCombatParId(int id);
    List<Combat> obtenirTousLesCombats();
    List<Combat> obtenirCombatsParJoueurId(int joueurId);
    void mettreAJourCombat(Combat combat);
    void supprimerCombat(int id);
}