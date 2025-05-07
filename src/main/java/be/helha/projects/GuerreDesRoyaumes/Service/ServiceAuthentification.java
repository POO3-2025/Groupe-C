package be.helha.projects.GuerreDesRoyaumes.Service;

import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;

public interface ServiceAuthentification {
    void inscrireJoueur(String nom, String prenom, String pseudo, String motDePasse);
    boolean authentifierJoueur(String pseudo, String motDePasse);
    Joueur obtenirJoueurParId(int id);
    Joueur obtenirJoueurParPseudo(String pseudo);
    void gererProfil(int id, String pseudo, String motDePasse);
    void choisirPersonnage(int joueurId, int personnageId);
}