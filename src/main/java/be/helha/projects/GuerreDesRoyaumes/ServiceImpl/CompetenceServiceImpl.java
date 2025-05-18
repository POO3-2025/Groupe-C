package be.helha.projects.GuerreDesRoyaumes.ServiceImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.CompetenceMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.CompetenceMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.JoueurDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.MongoDBConnectionException;
import be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat.*;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Service.CompetenceService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation du service de gestion des compétences de combat.
 */
@Service
public class CompetenceServiceImpl implements CompetenceService {

    private static CompetenceServiceImpl instance;
    private final CompetenceMongoDAO competenceDAO;
    private final JoueurDAO joueurDAO;
    
    /**
     * Constructeur par défaut
     */
    public CompetenceServiceImpl() throws MongoDBConnectionException {
        this.competenceDAO = CompetenceMongoDAOImpl.getInstance();
        this.joueurDAO = new JoueurDAOImpl();
    }

    /**
     * Obtient l'instance unique de CompetenceServiceImpl (Singleton)
     * @return L'instance unique de CompetenceServiceImpl
     */
    public static synchronized CompetenceServiceImpl getInstance() throws MongoDBConnectionException {
        if (instance == null) {
            instance = new CompetenceServiceImpl();
        }
        return instance;
    }

    @Override
    public boolean acheterCompetence(Joueur joueur, Competence competence) throws Exception {
        if (joueur == null || competence == null) {
            throw new Exception("Joueur ou compétence non défini");
        }
        
        // Vérifier si le joueur a déjà 4 compétences
        if (!peutAcheterNouvelleCompetence(joueur)) {
            throw new Exception("Vous avez déjà atteint le nombre maximum de compétences (4)");
        }
        
        // Vérifier si le joueur a déjà cette compétence
        if (competenceDAO.joueurPossedeCompetence(joueur.getId(), competence.getId())) {
            throw new Exception("Vous possédez déjà cette compétence");
        }
        
        // Vérifier si le joueur a assez d'argent
        if (joueur.getArgent() < competence.getPrix()) {
            throw new Exception("Vous n'avez pas assez d'argent pour acheter cette compétence");
        }
        
        // Débiter le joueur
        joueur.setArgent(joueur.getArgent() - competence.getPrix());
        
        // Mettre à jour le joueur dans la base de données
        try {
            joueurDAO.mettreAJourJoueur(joueur);
        } catch (Exception e) {
            // En cas d'erreur, annuler la transaction
            throw new Exception("Erreur lors de la mise à jour du joueur: " + e.getMessage());
        }
        
        // Sauvegarder la compétence
        boolean succes = competenceDAO.sauvegarderCompetence(joueur, competence);
        
        if (!succes) {
            // En cas d'échec, rembourser le joueur
            joueur.setArgent(joueur.getArgent() + competence.getPrix());
            joueurDAO.mettreAJourJoueur(joueur);
            throw new Exception("Erreur lors de la sauvegarde de la compétence");
        }
        
        return true;
    }

    @Override
    public List<Competence> obtenirCompetencesJoueur(Joueur joueur) {
        if (joueur == null) {
            return List.of();
        }
        
        return competenceDAO.obtenirCompetencesParJoueurId(joueur.getId());
    }

    @Override
    public boolean peutAcheterNouvelleCompetence(Joueur joueur) {
        if (joueur == null) {
            return false;
        }
        
        int nombreCompetences = competenceDAO.compterCompetences(joueur.getId());
        return nombreCompetences < 4;
    }

    @Override
    public boolean reinitialiserCompetences(Joueur joueur) {
        if (joueur == null) {
            return false;
        }
        
        return competenceDAO.supprimerToutesCompetences(joueur.getId());
    }

    @Override
    public boolean appliquerCompetences(Joueur joueur) {
        if (joueur == null || joueur.getPersonnage() == null) {
            return false;
        }
        
        List<Competence> competences = obtenirCompetencesJoueur(joueur);
        
        /* Sauvegarder les statistiques originales du personnage avant application des compétences
        int vieOriginale = joueur.getPersonnage().getVie();
        int degatsOriginaux = joueur.getPersonnage().getDegats();
        int resistanceOriginale = joueur.getPersonnage().getResistance();*/
        
        // Appliquer chaque compétence
        for (Competence competence : competences) {
            try {
                competence.appliquerEffet(joueur.getPersonnage());
                System.out.println("Compétence " + competence.getNom() + " appliquée au personnage de " + joueur.getPseudo());
            } catch (Exception e) {
                /*// En cas d'erreur, restaurer les statistiques originales
                joueur.getPersonnage().setVie(vieOriginale);
                joueur.getPersonnage().setDegats(degatsOriginaux);
                joueur.getPersonnage().setResistance(resistanceOriginale);*/
                
                System.err.println("Erreur lors de l'application des compétences: " + e.getMessage());
                return false;
            }
        }
        
        return true;
    }

    @Override
    public List<Competence> obtenirToutesCompetences() {
        List<Competence> competences = new ArrayList<>();
        
        // Ajouter toutes les compétences disponibles
        competences.add(new DoubleDegats());
        competences.add(new DoubleResistance());
        competences.add(new Regeneration());
        competences.add(new DoubleArgent());
        
        return competences;
    }
} 