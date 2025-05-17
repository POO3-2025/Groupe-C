package be.helha.projects.GuerreDesRoyaumes.DAOImpl;

import be.helha.projects.GuerreDesRoyaumes.Config.ConnexionConfig.ConnexionManager;
import be.helha.projects.GuerreDesRoyaumes.DAO.RoyaumeMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Royaume;
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

    /**
     * Constructeur privé pour le singleton qui initialise la connexion à la collection MongoDB.
     */
    private RoyaumeMongoDAOImpl() {
        MongoDatabase db = ConnexionManager.getInstance().getMongoDatabase();
        this.collection = db.getCollection("royaumes");
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
        Document doc = new Document();
        doc.append("id_joueur", joueurId);
        doc.append("nom", royaume.getNom());
        doc.append("niveau", royaume.getNiveau());
        // On pourrait ajouter d'autres informations sur le royaume ici (ressources, bâtiments, etc.)
        return doc;
    }

    /**
     * Convertit un Document MongoDB en objet Royaume.
     *
     * @param doc Le Document MongoDB à convertir
     * @return Un objet Royaume créé à partir du Document
     */
    private Royaume fromDocument(Document doc) {
        String nom = doc.getString("nom");
        int niveau = doc.getInteger("niveau", 1);
        return new Royaume(0, nom, niveau); // L'ID sera mis à jour si nécessaire
    }
} 