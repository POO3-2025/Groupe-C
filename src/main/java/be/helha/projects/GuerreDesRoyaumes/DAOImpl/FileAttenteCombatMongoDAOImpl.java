package be.helha.projects.GuerreDesRoyaumes.DAOImpl;

import be.helha.projects.GuerreDesRoyaumes.Config.InitialiserAPP;
import be.helha.projects.GuerreDesRoyaumes.DAO.FileAttenteCombatDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.MongoDBConnectionException;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation MongoDB de l'interface FileAttenteCombatDAO.
 * Gère la file d'attente des joueurs prêts à combattre.
 */
public class FileAttenteCombatMongoDAOImpl implements FileAttenteCombatDAO {

    private static FileAttenteCombatMongoDAOImpl instance;
    private final MongoCollection<Document> fileAttenteCollection;
    private static final String FILE_ATTENTE_COLLECTION = "fileAttenteCombat";
    private final JoueurDAO joueurDAO;

    /**
     * Constructeur privé pour le singleton.
     */
    private FileAttenteCombatMongoDAOImpl(JoueurDAO joueurDAO) {
        this.joueurDAO = joueurDAO;
        MongoDatabase mongoDB;
        try {
            mongoDB = InitialiserAPP.getMongoConnexion();
        } catch (MongoDBConnectionException ex) {
            throw new RuntimeException(ex);
        }
        this.fileAttenteCollection = mongoDB.getCollection(FILE_ATTENTE_COLLECTION);
    }

    /**
     * Obtient l'instance unique de FileAttenteCombatMongoDAOImpl (pattern Singleton).
     *
     * @param joueurDAO L'instance de JoueurDAO pour récupérer les informations des joueurs
     * @return L'instance unique de FileAttenteCombatMongoDAOImpl
     */
    public static synchronized FileAttenteCombatMongoDAOImpl getInstance(JoueurDAO joueurDAO) {
        if (instance == null) {
            instance = new FileAttenteCombatMongoDAOImpl(joueurDAO);
        }
        return instance;
    }

    @Override
    public boolean ajouterJoueurEnAttente(Joueur joueur) {
        try {
            // Vérifier si le joueur est déjà en attente
            if (estJoueurEnAttente(joueur.getId())) {
                // Mettre à jour le timestamp seulement
                Bson filter = Filters.eq("joueurId", joueur.getId());
                Bson update = Updates.set("dateMAJ", LocalDateTime.now().toString());
                UpdateResult result = fileAttenteCollection.updateOne(filter, update);
                return result.getModifiedCount() > 0;
            }

            // Créer un nouveau document pour le joueur en attente
            Document doc = new Document()
                    .append("joueurId", joueur.getId())
                    .append("pseudo", joueur.getPseudo())
                    .append("niveau", joueur.getPersonnage() != null ? 1 : 0) // Niveau par défaut
                    .append("statut", "EN_ATTENTE")
                    .append("adversaireId", 0) // 0 signifie pas d'adversaire encore
                    .append("dateCreation", LocalDateTime.now().toString())
                    .append("dateMAJ", LocalDateTime.now().toString());

            fileAttenteCollection.insertOne(doc);
            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout du joueur à la file d'attente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean estJoueurEnAttente(int joueurId) {
        try {
            Bson filter = Filters.eq("joueurId", joueurId);
            return fileAttenteCollection.countDocuments(filter) > 0;
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification du joueur en attente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean retirerJoueurEnAttente(int joueurId) {
        try {
            Bson filter = Filters.eq("joueurId", joueurId);
            return fileAttenteCollection.deleteOne(filter).getDeletedCount() > 0;
        } catch (Exception e) {
            System.err.println("Erreur lors du retrait du joueur de la file d'attente: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Joueur> obtenirJoueursEnAttente() {
        List<Joueur> joueursEnAttente = new ArrayList<>();
        try {
            Bson filter = Filters.eq("statut", "EN_ATTENTE");
            MongoCursor<Document> cursor = fileAttenteCollection.find(filter).iterator();

            while (cursor.hasNext()) {
                Document doc = cursor.next();
                int joueurId = doc.getInteger("joueurId");
                Joueur joueur = joueurDAO.obtenirJoueurParId(joueurId);
                if (joueur != null) {
                    joueursEnAttente.add(joueur);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des joueurs en attente: " + e.getMessage());
            e.printStackTrace();
        }
        return joueursEnAttente;
    }

    @Override
    public Joueur trouverAdversaire(Joueur joueur) {
        try {
            // Chercher un joueur en attente qui n'est pas le joueur courant
            Bson filter = Filters.and(
                    Filters.eq("statut", "EN_ATTENTE"),
                    Filters.ne("joueurId", joueur.getId())
            );

            Document doc = fileAttenteCollection.find(filter).first();
            if (doc != null) {
                int adversaireId = doc.getInteger("joueurId");
                
                // Mettre à jour les deux joueurs comme étant en matchmaking
                mettreAJourStatut(joueur.getId(), "MATCHMAKING");
                mettreAJourStatut(adversaireId, "MATCHMAKING");
                
                // Enregistrer l'adversaire trouvé pour les deux joueurs
                enregistrerMatch(joueur.getId(), adversaireId);
                
                return joueurDAO.obtenirJoueurParId(adversaireId);
            }
            return null;
        } catch (Exception e) {
            System.err.println("Erreur lors de la recherche d'un adversaire: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Enregistre un match entre deux joueurs.
     * 
     * @param joueur1Id L'ID du premier joueur
     * @param joueur2Id L'ID du second joueur
     * @return true si l'enregistrement a réussi, false sinon
     */
    private boolean enregistrerMatch(int joueur1Id, int joueur2Id) {
        try {
            // Enregistrer l'adversaire pour le joueur 1
            Bson filter1 = Filters.eq("joueurId", joueur1Id);
            Bson update1 = Updates.set("adversaireId", joueur2Id);
            fileAttenteCollection.updateOne(filter1, update1);
            
            // Enregistrer l'adversaire pour le joueur 2
            Bson filter2 = Filters.eq("joueurId", joueur2Id);
            Bson update2 = Updates.set("adversaireId", joueur1Id);
            fileAttenteCollection.updateOne(filter2, update2);
            
            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors de l'enregistrement du match: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean mettreAJourStatut(int joueurId, String statut) {
        try {
            Bson filter = Filters.eq("joueurId", joueurId);
            Bson update = Updates.combine(
                    Updates.set("statut", statut),
                    Updates.set("dateMAJ", LocalDateTime.now().toString())
            );
            UpdateResult result = fileAttenteCollection.updateOne(filter, update);
            return result.getModifiedCount() > 0;
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour du statut: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int verifierMatchTrouve(int joueurId) {
        try {
            Bson filter = Filters.eq("joueurId", joueurId);
            Document doc = fileAttenteCollection.find(filter).first();
            
            if (doc != null) {
                String statut = doc.getString("statut");
                if ("MATCHMAKING".equals(statut)) {
                    return doc.getInteger("adversaireId", 0);
                }
            }
            return 0;
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification de match: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
} 