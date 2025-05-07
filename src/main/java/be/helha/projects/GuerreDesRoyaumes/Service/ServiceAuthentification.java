package be.helha.projects.GuerreDesRoyaumes.Service;

import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;

public interface ServiceAuthentification {
    boolean inscrireJoueur(String nom, String prenom, String pseudo, String email, String motDePasse);
    Joueur authentifierJoueur(String pseudo, String motDePasse);
    Joueur obtenirJoueurParId(int id);
    Joueur obtenirJoueurParPseudo(String pseudo);
    void gererProfil(int id, String pseudo, String email, String motDePasse);
    void choisirPersonnage(int joueurId, int personnageId);
}