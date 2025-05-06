package be.helha.projects.GuerreDesRoyaumes.DAO;

import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import java.util.List;

public interface JoueurDAO {
    void ajouterJoueur(Joueur joueur);
    Joueur obtenirJoueurParId(int id);
    Joueur obtenirJoueurParPseudo(String pseudo);
    List<Joueur> obtenirTousLesJoueurs();
    void mettreAJourJoueur(Joueur joueur);
    void supprimerJoueur(int id);
    boolean authentifierJoueur(String pseudo, String motDePasse);
}