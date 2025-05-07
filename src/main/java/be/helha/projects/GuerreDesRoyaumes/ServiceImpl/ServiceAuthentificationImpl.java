package be.helha.projects.GuerreDesRoyaumes.ServiceImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.PersonnageDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Coffre;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;
import be.helha.projects.GuerreDesRoyaumes.Model.Royaume;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceAuthentification;

public class ServiceAuthentificationImpl implements ServiceAuthentification {

    private JoueurDAO joueurDAO;
    private PersonnageDAO personnageDAO;

    public ServiceAuthentificationImpl(JoueurDAO joueurDAO, PersonnageDAO personnageDAO) {
        this.joueurDAO = joueurDAO;
        this.personnageDAO = personnageDAO;
    }

    @Override
    public void inscrireJoueur(String nom, String prenom, String pseudo, String motDePasse) {
        // Vérifier si le pseudo existe déjà
        if (joueurDAO.obtenirJoueurParPseudo(pseudo) != null) {
            throw new IllegalArgumentException("Ce pseudo est déjà utilisé");
        }

        // Créer un nouveau royaume et inventaire par défaut
        Royaume royaume = new Royaume(0, "Royaume de " + pseudo, 1);
        Coffre coffre = new Coffre();

        // Créer le joueur (sans personnage pour l'instant)
        Joueur joueur = new Joueur(0, nom, prenom, pseudo, motDePasse, 100, royaume, null, coffre);

        // Persister le joueur
        joueurDAO.ajouterJoueur(joueur);
    }

    @Override
    public boolean authentifierJoueur(String pseudo, String motDePasse) {
        return joueurDAO.authentifierJoueur(pseudo, motDePasse);
    }

    @Override
    public Joueur obtenirJoueurParId(int id) {
        return joueurDAO.obtenirJoueurParId(id);
    }

    @Override
    public Joueur obtenirJoueurParPseudo(String pseudo) {
        return joueurDAO.obtenirJoueurParPseudo(pseudo);
    }

    @Override
    public void gererProfil(int id, String pseudo, String motDePasse) {
        Joueur joueur = joueurDAO.obtenirJoueurParId(id);
        if (joueur == null) {
            throw new IllegalArgumentException("Joueur non trouvé");
        }

        // Vérifier si le nouveau pseudo est disponible (si changé)
        if (!joueur.getPseudo().equals(pseudo)) {
            Joueur existant = joueurDAO.obtenirJoueurParPseudo(pseudo);
            if (existant != null) {
                throw new IllegalArgumentException("Ce pseudo est déjà utilisé");
            }
        }

        // Mettre à jour les informations
        joueur.setPseudo(pseudo);
        joueur.setMotDePasse(motDePasse);

        // Persister les modifications
        joueurDAO.mettreAJourJoueur(joueur);
    }

    @Override
    public void choisirPersonnage(int joueurId, int personnageId) {
        Joueur joueur = joueurDAO.obtenirJoueurParId(joueurId);
        if (joueur == null) {
            throw new IllegalArgumentException("Joueur non trouvé");
        }

        Personnage personnage = personnageDAO.obtenirPersonnageParId(personnageId);
        if (personnage == null) {
            throw new IllegalArgumentException("Personnage non trouvé");
        }

        // Associer le personnage au joueur
        joueur.setPersonnage(personnage);

        // Persister les modifications
        joueurDAO.mettreAJourJoueur(joueur);
    }
}