package be.helha.projects.GuerreDesRoyaumes.DAO;

import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;
import java.util.List;

public interface PersonnageDAO {
    void ajouterPersonnage(Personnage personnage);
    Personnage obtenirPersonnageParId(int id);
    List<Personnage> obtenirTousLesPersonnages();
    void mettreAJourPersonnage(Personnage personnage);
    void supprimerPersonnage(int id);
}