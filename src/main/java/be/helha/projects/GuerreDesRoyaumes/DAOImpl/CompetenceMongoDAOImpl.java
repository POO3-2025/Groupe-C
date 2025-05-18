package be.helha.projects.GuerreDesRoyaumes.DAOImpl;

import be.helha.projects.GuerreDesRoyaumes.Config.InitialiserAPP;
import be.helha.projects.GuerreDesRoyaumes.DAO.CompetenceMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.MongoDBConnectionException;
import be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat.*;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation de l'interface CompetenceMongoDAO pour la gestion des compétences de combat dans MongoDB.
 * Cette classe gère les opérations CRUD pour les documents de compétences.
 */
@Repository
public class CompetenceMongoDAOImpl implements CompetenceMongoDAO {

    private static CompetenceMongoDAOImpl instance;
    private final MongoCollection<Document> collection;
    private static final String COLLECTION_NAME = "competencesAchetees";

    /**
     * Constructeur privé pour le singleton qui initialise la connexion à la collection MongoDB.
     */
    private CompetenceMongoDAOImpl() {
        MongoDatabase mongoDB;
        try {
            mongoDB = InitialiserAPP.getMongoConnexion();
        } catch (MongoDBConnectionException ex) {
            throw new RuntimeException(ex);
        }
        this.collection = mongoDB.getCollection(COLLECTION_NAME);
    }

    /**
     * Obtient l'instance unique de CompetenceMongoDAOImpl (pattern Singleton).
     *
     * @return L'instance unique de CompetenceMongoDAOImpl
     */
    public static synchronized CompetenceMongoDAOImpl getInstance() throws MongoDBConnectionException {
        if (instance == null) {
            instance = new CompetenceMongoDAOImpl();
        }
        return instance;
    }

    /**
     * Crée une instance de compétence à partir de son identifiant.
     *
     * @param id L'identifiant de la compétence
     * @return L'instance de compétence correspondante ou null si non trouvée
     */
    private Competence creerCompetenceParId(String id) {
        switch (id) {
            case "CompetenceDegats":
                return new DoubleDegats();
            case "CompetenceResistance":
                return new DoubleResistance();
            case "CompetenceRegeneration":
                return new Regeneration();
            case "CompetenceArgent":
                return new DoubleArgent();
            default:
                return null;
        }
    }

    @Override
    public boolean sauvegarderCompetence(Joueur joueur, Competence competence) {
        try {
            System.out.println("Sauvegarde de la compétence " + competence.getNom() + " pour le joueur ID: " + joueur.getId());
            
            // Vérifier si la compétence existe déjà pour ce joueur
            if (joueurPossedeCompetence(joueur.getId(), competence.getId())) {
                System.out.println("Le joueur possède déjà cette compétence");
                return false;
            }
            
            // Vérifier si le joueur a déjà 4 compétences
            if (compterCompetences(joueur.getId()) >= 4) {
                System.out.println("Le joueur a déjà atteint le nombre maximum de compétences (4)");
                return false;
            }

            // Créer un nouveau document pour la compétence
            Document competenceDoc = new Document();
            competenceDoc.append("id_joueur", joueur.getId());
            competenceDoc.append("competence_id", competence.getId());
            competenceDoc.append("nom", competence.getNom());
            competenceDoc.append("prix", competence.getPrix());
            competenceDoc.append("description", competence.getDescription());

            // Insérer le document
            collection.insertOne(competenceDoc);
            System.out.println("Compétence sauvegardée avec succès dans MongoDB");
            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde de la compétence dans MongoDB: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Competence> obtenirCompetencesParJoueurId(int joueurId) {
        List<Competence> competences = new ArrayList<>();
        try {
            System.out.println("Recherche des compétences pour le joueur ID: " + joueurId);
            MongoCursor<Document> cursor = collection.find(Filters.eq("id_joueur", joueurId)).iterator();
            
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                String competenceId = doc.getString("competence_id");
                Competence competence = creerCompetenceParId(competenceId);
                
                if (competence != null) {
                    competences.add(competence);
                    System.out.println("Compétence trouvée: " + competence.getNom());
                }
            }
            
            System.out.println("Nombre total de compétences trouvées: " + competences.size());
            return competences;
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des compétences depuis MongoDB: " + e.getMessage());
            e.printStackTrace();
            return competences;
        }
    }

    @Override
    public List<Competence> obtenirCompetencesParPseudo(String pseudo) {
        try {
            // Obtenir l'ID du joueur à partir du pseudo
            JoueurDAOImpl joueurDAO = new JoueurDAOImpl();
            Joueur joueur = joueurDAO.obtenirJoueurParPseudo(pseudo);
            
            if (joueur == null) {
                System.err.println("Joueur non trouvé avec le pseudo: " + pseudo);
                return List.of();
            }
            
            return obtenirCompetencesParJoueurId(joueur.getId());
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des compétences par pseudo: " + e.getMessage());
            e.printStackTrace();
            return List.of();
        }
    }

    @Override
    public boolean joueurPossedeCompetence(int joueurId, String competenceId) {
        try {
            Document doc = collection.find(
                Filters.and(
                    Filters.eq("id_joueur", joueurId),
                    Filters.eq("competence_id", competenceId)
                )
            ).first();
            
            return doc != null;
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification de possession de compétence: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean supprimerCompetence(int joueurId, String competenceId) {
        try {
            DeleteResult result = collection.deleteOne(
                Filters.and(
                    Filters.eq("id_joueur", joueurId),
                    Filters.eq("competence_id", competenceId)
                )
            );
            
            System.out.println("Suppression de la compétence " + competenceId + " pour le joueur " + joueurId + ": " + result.getDeletedCount() + " document(s) supprimé(s)");
            return result.getDeletedCount() > 0;
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression de compétence: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean supprimerToutesCompetences(int joueurId) {
        try {
            DeleteResult result = collection.deleteMany(Filters.eq("id_joueur", joueurId));
            System.out.println("Suppression de toutes les compétences pour le joueur " + joueurId + ": " + result.getDeletedCount() + " document(s) supprimé(s)");
            return result.getDeletedCount() > 0;
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression de toutes les compétences: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int compterCompetences(int joueurId) {
        try {
            return (int) collection.countDocuments(Filters.eq("id_joueur", joueurId));
        } catch (Exception e) {
            System.err.println("Erreur lors du comptage des compétences: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
} 