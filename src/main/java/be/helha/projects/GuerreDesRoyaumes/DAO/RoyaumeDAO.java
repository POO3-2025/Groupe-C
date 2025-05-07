package be.helha.projects.GuerreDesRoyaumes.DAO;

import be.helha.projects.GuerreDesRoyaumes.Model.Royaume;
import java.util.List;

public interface RoyaumeDAO {
    void ajouterRoyaume(Royaume royaume);
    Royaume obtenirRoyaumeParId(int id);
    List<Royaume> obtenirTousLesRoyaumes();
    List<Royaume> obtenirRoyaumesParJoueurId(int joueurId);
    void mettreAJourRoyaume(Royaume royaume);
    void supprimerRoyaume(int id);
}
