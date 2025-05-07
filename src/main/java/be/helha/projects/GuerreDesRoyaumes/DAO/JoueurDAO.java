package be.helha.projects.GuerreDesRoyaumes.DAO;

import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public interface JoueurDAO {

    int getNextJoueurID() throws SQLException;
    boolean authentifierJoueur(String pseudo, String motDePasse);

    // Create
    void ajouterJoueur(Joueur joueur);

    // Read
    Joueur obtenirJoueurParId(int id);
    Joueur obtenirJoueurParPseudo(String pseudo);
    List<Joueur> obtenirTousLesJoueurs();

    // Update
    void mettreAJourJoueur(Joueur joueur);

    // Delete
    void supprimerJoueur(int id);
}