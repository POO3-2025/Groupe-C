package be.helha.projects.GuerreDesRoyaumes.Service;

import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Model.Royaume;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;

public interface ServiceAuthentification {
    void inscrireJoueur(String nom, String prenom, String pseudo, String motDePasse);
    boolean authentifierJoueur(String pseudo, String motDePasse);
    void gererProfil(int id, String pseudo, String motDePasse);
    void choisirPersonnage(int joueurId, int personnageId);
    void initialiserJoueur(String pseudo, Royaume royaume, Personnage personnage);
    
    // Méthodes ajoutées
    Joueur obtenirJoueurParPseudo(String pseudo);
    void mettreAJourJoueur(Joueur joueur);
}