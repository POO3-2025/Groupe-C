package be.helha.projects.GuerreDesRoyaumes.ServiceImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.PersonnageDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.JoueurDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.AuthentificationException;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.JoueurNotFoundException;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.PersonnageNotFoundException;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Coffre;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;
import be.helha.projects.GuerreDesRoyaumes.Model.Royaume;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceAuthentification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.sql.SQLException;

@Service
public class ServiceAuthentificationImpl implements ServiceAuthentification {

    private JoueurDAO joueurDAO;
    private PersonnageDAO personnageDAO;

    @Autowired
    public ServiceAuthentificationImpl(JoueurDAO joueurDAO, PersonnageDAO personnageDAO) {
        this.joueurDAO = joueurDAO;
        this.personnageDAO = personnageDAO;
    }

    @Override
    public void inscrireJoueur(String nom, String prenom, String pseudo, String motDePasse) {
        // Vérifier si le pseudo existe déjà
        if (joueurDAO.obtenirJoueurParPseudo(pseudo) != null) {
            throw new AuthentificationException("Ce pseudo est déjà utilisé");
        }

        // Validation du format du pseudo
        if (!pseudo.matches("[a-zA-Z0-9_]+")) {
            throw new AuthentificationException("Le pseudo ne peut contenir que des lettres, chiffres et underscores.");
        }

        // Créer un nouveau royaume et le coffre par défaut
        Royaume royaume = new Royaume(0, "Royaume de " + pseudo, 1);
        Coffre coffre = new Coffre();

        // Créer le joueur avec le mot de passe haché
        String motDePasseHache = BCrypt.hashpw(motDePasse, BCrypt.gensalt());
        Joueur joueur = new Joueur(0, nom, prenom, pseudo, motDePasseHache, 100, royaume, null, coffre);

        // Persister le joueur
        joueurDAO.ajouterJoueur(joueur);
    }

    @Override
    public boolean authentifierJoueur(String pseudo, String motDePasse) {
        if (pseudo == null || pseudo.trim().isEmpty()) {
            throw new AuthentificationException("Le pseudo ne peut pas être vide");
        }

        if (motDePasse == null || motDePasse.trim().isEmpty()) {
            throw new AuthentificationException("Le mot de passe ne peut pas être vide");
        }

        // Caster joueurDAO en JoueurDAOImpl pour accéder à la méthode verifierIdentifiants
        if (joueurDAO instanceof JoueurDAOImpl) {
            try {
                return ((JoueurDAOImpl) joueurDAO).verifierIdentifiants(pseudo, motDePasse);
            } catch (Exception e) {
                throw new AuthentificationException("Erreur lors de l'authentification", e);
            }
        }
        throw new AuthentificationException("Service d'authentification mal configuré");
    }

    @Override
    public void gererProfil(int id, String pseudo, String motDePasse) {
        try {
            Joueur joueur = joueurDAO.obtenirJoueurParId(id);

            // Vérifier si le nouveau pseudo est disponible (si changé)
            if (!joueur.getPseudo().equals(pseudo)) {
                Joueur existant = joueurDAO.obtenirJoueurParPseudo(pseudo);
                if (existant != null) {
                    throw new AuthentificationException("Ce pseudo est déjà utilisé");
                }
            }

            // Mettre à jour les informations
            joueur.setPseudo(pseudo);

            // Si le mot de passe est changé, le hacher
            if (!motDePasse.equals(joueur.getMotDePasse())) {
                joueur.setMotDePasse(BCrypt.hashpw(motDePasse, BCrypt.gensalt()));
            }

            // Persister les modifications
            joueurDAO.mettreAJourJoueur(joueur);
        } catch (JoueurNotFoundException e) {
            throw e; // Relance l'exception si c'est déjà une JoueurNotFoundException
        } catch (Exception e) {
            throw new AuthentificationException("Erreur lors de la mise à jour du profil", e);
        }
    }

    @Override
    public void choisirPersonnage(int joueurId, int personnageId) {
        try {
            Joueur joueur = joueurDAO.obtenirJoueurParId(joueurId);

            Personnage personnage = personnageDAO.obtenirPersonnageParId(personnageId);
            if (personnage == null) {
                throw new PersonnageNotFoundException(personnageId);
            }

            // Associer le personnage au joueur
            joueur.setPersonnage(personnage);

            // Persister les modifications
            joueurDAO.mettreAJourJoueur(joueur);
        } catch (JoueurNotFoundException | PersonnageNotFoundException e) {
            throw e; // Relance l'exception
        } catch (Exception e) {
            throw new AuthentificationException("Erreur lors du choix de personnage", e);
        }
    }
}