package be.helha.projects.GuerreDesRoyaumes.DAOImpl;

import be.helha.projects.GuerreDesRoyaumes.Config.ConnexionConfig.ConnexionManager;
import be.helha.projects.GuerreDesRoyaumes.Config.InitialiserAPP;
import be.helha.projects.GuerreDesRoyaumes.DAO.RoyaumeMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.MongoDBConnectionException;
import be.helha.projects.GuerreDesRoyaumes.Model.Royaume;
import be.helha.projects.GuerreDesRoyaumes.Outils.GsonObjectIdAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.springframework.stereotype.Repository;

/**
 * Implémentation de l'interface RoyaumeMongoDAO pour la gestion des royaumes dans MongoDB.
 * Cette classe gère les opérations CRUD pour les documents Royaume.
 */
@Repository
public class RoyaumeMongoDAOImpl implements RoyaumeMongoDAO {

    private static RoyaumeMongoDAOImpl instance;
    private final MongoCollection<Document> collection;
    private final Gson gson = GsonObjectIdAdapter.getGson();


    /**
     * Constructeur privé pour le singleton qui initialise la connexion à la collection MongoDB.
     */
    private RoyaumeMongoDAOImpl() {
        try {
            MongoDatabase mongoDB = InitialiserAPP.getMongoConnexion();
            this.collection = mongoDB.getCollection("royaumes");
        } catch (MongoDBConnectionException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Obtient l'instance unique de RoyaumeMongoDAOImpl (pattern Singleton).
     *
     * @return L'instance unique de RoyaumeMongoDAOImpl
     */
    public static synchronized RoyaumeMongoDAOImpl getInstance() {
        if (instance == null) {
            instance = new RoyaumeMongoDAOImpl();
        }
        return instance;
    }

    /**
     * Ajoute un nouveau royaume dans la collection MongoDB.
     *
     * @param royaume Le royaume à ajouter
     * @param joueurId L'identifiant du joueur propriétaire du royaume
     */
    @Override
    public void ajouterRoyaume(Royaume royaume, int joueurId) {
        Document doc = toDocument(royaume, joueurId);
        collection.insertOne(doc);
    }

    /**
     * Récupère un royaume par l'identifiant du joueur.
     *
     * @param joueurId L'identifiant du joueur
     * @return Le royaume correspondant au joueur ou null si aucun royaume n'est trouvé
     */
    @Override
    public Royaume obtenirRoyaumeParJoueurId(int joueurId) {
        Document doc = collection.find(Filters.eq("id_joueur", joueurId)).first();
        if (doc != null) {
            return fromDocument(doc);
        }
        return null;
    }

    /**
     * Met à jour les informations d'un royaume existant.
     *
     * @param royaume Le royaume avec les nouvelles informations
     * @param joueurId L'identifiant du joueur propriétaire du royaume
     */
    @Override
    public void mettreAJourRoyaume(Royaume royaume, int joueurId) {
        Document doc = toDocument(royaume, joueurId);
        collection.replaceOne(Filters.eq("id_joueur", joueurId), doc);
    }

    /**
     * Supprime un royaume de la collection MongoDB.
     *
     * @param joueurId L'identifiant du joueur propriétaire du royaume
     */
    @Override
    public void supprimerRoyaume(int joueurId) {
        collection.deleteOne(Filters.eq("id_joueur", joueurId));
    }

    /**
     * Convertit un objet Royaume en Document MongoDB.
     *
     * @param royaume Le royaume à convertir
     * @param joueurId L'identifiant du joueur propriétaire du royaume
     * @return Un Document MongoDB représentant le royaume
     */
    private Document toDocument(Royaume royaume, int joueurId) {
        try {
            // Création manuelle du document JSON
            JsonObject jsonRoyaume = new JsonObject();
            
            // Ajouter les propriétés du royaume - sans l'id
            jsonRoyaume.addProperty("nom", royaume.getNom());
            jsonRoyaume.addProperty("niveau", royaume.getNiveau());
            
            // Ajouter l'ID du joueur
            jsonRoyaume.addProperty("id_joueur", joueurId);
            
            // Convertir en document MongoDB
            Document doc = Document.parse(jsonRoyaume.toString());
            return doc;
        } catch (Exception e) {
            throw new RuntimeException("Échec de la sérialisation: " + e.getMessage(), e);
        }
    }

    /**
     * Convertit un Document MongoDB en objet Royaume.
     *
     * @param doc Le Document MongoDB à convertir
     * @return Un objet Royaume créé à partir du Document
     */
    private Royaume fromDocument(Document doc) {
        try {
            // Créer un nouveau royaume et remplir directement les propriétés
            Royaume royaume = new Royaume();
            
            if (doc.containsKey("id")) {
                royaume.setId(doc.getInteger("id"));
            }
            
            if (doc.containsKey("nom")) {
                royaume.setNom(doc.getString("nom"));
            }
            
            if (doc.containsKey("niveau")) {
                royaume.setNiveau(doc.getInteger("niveau"));
            }
            
            return royaume;
        } catch (Exception e) {
            throw new RuntimeException("Échec de la désérialisation: " + e.getMessage(), e);
        }
    }

    /**
     * Augmente le niveau du royaume d'un joueur de 1
     *
     * @param joueurId L'identifiant du joueur propriétaire du royaume
     * @return true si le niveau a été augmenté avec succès, false sinon
     */
    public boolean augmenterNiveauRoyaume(int joueurId) {
        try {
            // Récupérer le royaume actuel
            Royaume royaume = obtenirRoyaumeParJoueurId(joueurId);
            
            if (royaume == null) {
                System.err.println("Aucun royaume trouvé pour le joueur ID: " + joueurId);
                return false;
            }
            
            // Augmenter le niveau de 1
            int nouveauNiveau = royaume.getNiveau() + 1;
            royaume.setNiveau(nouveauNiveau);
            
            // Mettre à jour le royaume dans MongoDB
            mettreAJourRoyaume(royaume, joueurId);
            
            System.out.println("Niveau du royaume pour le joueur ID " + joueurId + " augmenté à " + nouveauNiveau);
            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors de l'augmentation du niveau du royaume: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
} 