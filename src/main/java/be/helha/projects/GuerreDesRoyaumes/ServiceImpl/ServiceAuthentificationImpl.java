package be.helha.projects.GuerreDesRoyaumes.ServiceImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.PersonnageDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.JoueurDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Coffre;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;
import be.helha.projects.GuerreDesRoyaumes.Model.Royaume;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceAuthentification;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.sql.SQLException;

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

        // Validation du format du pseudo
        if (!pseudo.matches("[a-zA-Z0-9_]+")) {
            throw new IllegalArgumentException("Le pseudo ne peut contenir que des lettres, chiffres et underscores.");
        }

        // Créer un nouveau royaume et le coffre par défaut
        Royaume royaume = new Royaume(0, "Royaume de " + pseudo, 1);
        Coffre coffre = new Coffre();

        // Créer le joueur avec le mot de passe haché
        String motDePasseHache = BCrypt.hashpw(motDePasse, BCrypt.gensalt());
        Joueur joueur = new Joueur(0, nom, prenom, pseudo, motDePasseHache, 100, royaume, null, coffre,0,0);

        // Persister le joueur
        joueurDAO.ajouterJoueur(joueur);
    }

    @Override
    public boolean authentifierJoueur(String pseudo, String motDePasse) {
        // Caster joueurDAO en JoueurDAOImpl pour accéder à la méthode verifierIdentifiants
        if (joueurDAO instanceof JoueurDAOImpl) {
            return ((JoueurDAOImpl) joueurDAO).verifierIdentifiants(pseudo, motDePasse);
        }
        return false; // Si l'implémentation n'est pas correcte
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

        // Si le mot de passe est changé, le hacher
        if (!motDePasse.equals(joueur.getMotDePasse())) {
            joueur.setMotDePasse(BCrypt.hashpw(motDePasse, BCrypt.gensalt()));
        }

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