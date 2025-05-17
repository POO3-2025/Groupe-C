package be.helha.projects.GuerreDesRoyaumes.DAOImpl;

import be.helha.projects.GuerreDesRoyaumes.Config.ConnexionConfig.ConnexionManager;
import be.helha.projects.GuerreDesRoyaumes.Config.InitialiserAPP;
import be.helha.projects.GuerreDesRoyaumes.DAO.PersonnageMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.MongoDBConnectionException;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Golem;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Guerrier;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Titan;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Voleur;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;
import org.springframework.stereotype.Repository;

/**
 * Implémentation de l'interface PersonnageMongoDAO pour la gestion des personnages dans MongoDB.
 * Cette classe gère les opérations CRUD pour les documents Personnage.
 */
@Repository
public class PersonnageMongoDAOImpl implements PersonnageMongoDAO {

    private static PersonnageMongoDAOImpl instance;
    private final MongoCollection<Document> collection;

    /**
     * Constructeur privé pour le singleton qui initialise la connexion à la collection MongoDB.
     */
    private PersonnageMongoDAOImpl() {
        try {
            MongoDatabase mongoDB = InitialiserAPP.getMongoConnexion();
            this.collection = mongoDB.getCollection("personnages");
        } catch (MongoDBConnectionException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Obtient l'instance unique de PersonnageMongoDAOImpl (pattern Singleton).
     *
     * @return L'instance unique de PersonnageMongoDAOImpl
     */
    public static synchronized PersonnageMongoDAOImpl getInstance() {
        if (instance == null) {
            instance = new PersonnageMongoDAOImpl();
        }
        return instance;
    }

    /**
     * Ajoute un nouveau personnage dans la collection MongoDB.
     *
     * @param personnage Le personnage à ajouter
     * @param joueurId L'identifiant du joueur propriétaire du personnage
     */
    @Override
    public void ajouterPersonnage(Personnage personnage, int joueurId) {
        Document doc = toDocument(personnage, joueurId);
        collection.insertOne(doc);
    }

    /**
     * Récupère un personnage par l'identifiant du joueur.
     *
     * @param joueurId L'identifiant du joueur
     * @return Le personnage correspondant au joueur ou null si aucun personnage n'est trouvé
     */
    @Override
    public Personnage obtenirPersonnageParJoueurId(int joueurId) {
        Document doc = collection.find(Filters.eq("id_joueur", joueurId)).first();
        if (doc != null) {
            return fromDocument(doc);
        }
        return null;
    }

    /**
     * Met à jour les informations d'un personnage existant.
     *
     * @param personnage Le personnage avec les nouvelles informations
     * @param joueurId L'identifiant du joueur propriétaire du personnage
     */
    @Override
    public void mettreAJourPersonnage(Personnage personnage, int joueurId) {
        Document doc = toDocument(personnage, joueurId);
        collection.replaceOne(Filters.eq("id_joueur", joueurId), doc);
    }

    /**
     * Supprime un personnage de la collection MongoDB.
     *
     * @param joueurId L'identifiant du joueur propriétaire du personnage
     */
    @Override
    public void supprimerPersonnage(int joueurId) {
        collection.deleteOne(Filters.eq("id_joueur", joueurId));
    }

    /**
     * Convertit un objet Personnage en Document MongoDB.
     *
     * @param personnage Le personnage à convertir
     * @param joueurId L'identifiant du joueur propriétaire du personnage
     * @return Un Document MongoDB représentant le personnage
     */
    private Document toDocument(Personnage personnage, int joueurId) {
        Document doc = new Document();
        doc.append("id_joueur", joueurId);
        doc.append("nom", personnage.getNom());
        doc.append("vie", personnage.getVie());
        doc.append("degats", personnage.getDegats());
        doc.append("resistance", personnage.getResistance());
        doc.append("type", personnage.getClass().getSimpleName());
        
        // On pourrait ajouter d'autres informations spécifiques au type de personnage ici
        
        return doc;
    }

    /**
     * Convertit un Document MongoDB en objet Personnage.
     *
     * @param doc Le Document MongoDB à convertir
     * @return Un objet Personnage créé à partir du Document
     */
    private Personnage fromDocument(Document doc) {
        String type = doc.getString("type");
        
        // Créer une instance de personnage en fonction du type
        Personnage personnage = null;
        switch (type) {
            case "Guerrier":
                personnage = new Guerrier();
                break;
            case "Voleur":
                personnage = new Voleur();
                break;
            case "Golem":
                personnage = new Golem();
                break;
            case "Titan":
                personnage = new Titan();
                break;
            default:
                // Par défaut, on crée un guerrier si le type n'est pas reconnu
                personnage = new Guerrier();
                break;
        }
        
        return personnage;
    }
} 