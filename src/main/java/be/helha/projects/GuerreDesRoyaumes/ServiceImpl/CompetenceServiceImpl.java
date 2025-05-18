package be.helha.projects.GuerreDesRoyaumes.ServiceImpl;

import be.helha.projects.GuerreDesRoyaumes.Service.CompetenceService;
import be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat.*;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.DAO.CompetenceMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.CompetenceMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.JoueurDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.MongoDBConnectionException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation du service {@link CompetenceService} gérant les compétences de combat.
 * <p>
 * Cette classe supporte l'achat, la récupération, la réinitialisation, et l'application
 * des compétences pour un joueur. Elle communique avec la base MongoDB via DAO.
 * </p>
 */
@Service
public class CompetenceServiceImpl implements CompetenceService {

    private static CompetenceServiceImpl instance;
    private final CompetenceMongoDAO competenceDAO;
    private final JoueurDAO joueurDAO;

    /**
     * Constructeur par défaut.
     * Initialise les DAO MongoDB et SQL.
     *
     * @throws MongoDBConnectionException en cas d'erreur de connexion MongoDB.
     */
    public CompetenceServiceImpl() throws MongoDBConnectionException {
        this.competenceDAO = CompetenceMongoDAOImpl.getInstance();
        this.joueurDAO = new JoueurDAOImpl();
    }

    /**
     * Obtient l'instance unique du singleton.
     *
     * @return Instance unique de CompetenceServiceImpl.
     * @throws MongoDBConnectionException en cas d'erreur MongoDB.
     */
    public static synchronized CompetenceServiceImpl getInstance() throws MongoDBConnectionException {
        if (instance == null) {
            instance = new CompetenceServiceImpl();
        }
        return instance;
    }

    /**
     * Achète une compétence pour un joueur.
     * <p>
     * Vérifie que le joueur peut acheter la compétence, qu'il ne la possède pas déjà,
     * et qu'il a assez d'argent. Met à jour la base de données en conséquence.
     * </p>
     *
     * @param joueur     Le joueur achetant la compétence.
     * @param competence La compétence à acheter.
     * @return true si l'achat a réussi, sinon lève une exception.
     * @throws Exception En cas d'erreur (compétence existante, fonds insuffisants, etc.).
     */
    @Override
    public boolean acheterCompetence(Joueur joueur, Competence competence) throws Exception {
        if (joueur == null || competence == null) {
            throw new Exception("Joueur ou compétence non défini");
        }
        if (!peutAcheterNouvelleCompetence(joueur)) {
            throw new Exception("Nombre maximum de compétences atteint (4)");
        }
        if (competenceDAO.joueurPossedeCompetence(joueur.getId(), competence.getId())) {
            throw new Exception("Compétence déjà possédée");
        }
        if (joueur.getArgent() < competence.getPrix()) {
            throw new Exception("Argent insuffisant pour acheter la compétence");
        }
        joueur.setArgent(joueur.getArgent() - competence.getPrix());
        try {
            joueurDAO.mettreAJourJoueur(joueur);
        } catch (Exception e) {
            throw new Exception("Erreur mise à jour joueur: " + e.getMessage());
        }
        boolean succes = competenceDAO.sauvegarderCompetence(joueur, competence);
        if (!succes) {
            joueur.setArgent(joueur.getArgent() + competence.getPrix());
            joueurDAO.mettreAJourJoueur(joueur);
            throw new Exception("Erreur sauvegarde compétence");
        }
        return true;
    }

    /**
     * Obtient la liste des compétences achetées par un joueur.
     *
     * @param joueur Le joueur ciblé.
     * @return Liste des compétences achetées ou liste vide si joueur nul.
     */
    @Override
    public List<Competence> obtenirCompetencesJoueur(Joueur joueur) {
        if (joueur == null) {
            return List.of();
        }
        return competenceDAO.obtenirCompetencesParJoueurId(joueur.getId());
    }

    /**
     * Vérifie si un joueur peut acheter une nouvelle compétence
     * (limite fixée à 4 compétences).
     *
     * @param joueur Le joueur ciblé.
     * @return true si le joueur peut acheter, false sinon.
     */
    @Override
    public boolean peutAcheterNouvelleCompetence(Joueur joueur) {
        if (joueur == null) {
            return false;
        }
        int nombreCompetences = competenceDAO.compterCompetences(joueur.getId());
        return nombreCompetences < 4;
    }

    /**
     * Réinitialise (supprime) toutes les compétences d'un joueur.
     *
     * @param joueur Le joueur ciblé.
     * @return true si la suppression a réussi, false sinon.
     */
    @Override
    public boolean reinitialiserCompetences(Joueur joueur) {
        if (joueur == null) {
            return false;
        }
        return competenceDAO.supprimerToutesCompetences(joueur.getId());
    }

    /**
     * Applique les effets de toutes les compétences achetées au personnage du joueur.
     *
     * @param joueur Le joueur ciblé.
     * @return true si l'application a réussi, false sinon.
     */
    @Override
    public boolean appliquerCompetences(Joueur joueur) {
        if (joueur == null || joueur.getPersonnage() == null) {
            return false;
        }
        List<Competence> competences = obtenirCompetencesJoueur(joueur);
        for (Competence competence : competences) {
            try {
                competence.appliquerEffet(joueur.getPersonnage());
                System.out.println("Compétence " + competence.getNom() + " appliquée au personnage de " + joueur.getPseudo());
            } catch (Exception e) {
                System.err.println("Erreur lors de l'application des compétences: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    /**
     * Obtient la liste de toutes les compétences disponibles dans le jeu.
     *
     * @return Liste complète des compétences.
     */
    @Override
    public List<Competence> obtenirToutesCompetences() {
        List<Competence> competences = new ArrayList<>();
        competences.add(new DoubleDegats());
        competences.add(new DoubleResistance());
        competences.add(new Regeneration());
        competences.add(new DoubleArgent());
        return competences;
    }
}
