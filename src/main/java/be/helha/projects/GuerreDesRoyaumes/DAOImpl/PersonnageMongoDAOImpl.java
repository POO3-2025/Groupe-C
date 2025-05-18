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
import be.helha.projects.GuerreDesRoyaumes.Outils.GsonObjectIdAdapter;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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

    private final MongoCollection<Document> collection;
    private final Gson gson;

    /**
     * Constructeur pour l'injection de dépendances Spring
     */
    public PersonnageMongoDAOImpl() {
        try {
            MongoDatabase mongoDB = InitialiserAPP.getMongoConnexion();
            this.collection = mongoDB.getCollection("personnages");
            this.gson = GsonObjectIdAdapter.getGson();

        } catch (MongoDBConnectionException ex) {
            throw new RuntimeException(ex);
        }
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
        try {
            // Conversion du personnage en JsonObject via notre adaptateur
            JsonObject jsonPersonnage = new JsonObject();
            
            // Ajouter les propriétés principales du personnage
            jsonPersonnage.addProperty("nom", personnage.getNom());
            jsonPersonnage.addProperty("vie", personnage.getVie());
            jsonPersonnage.addProperty("degats", personnage.getDegats());
            jsonPersonnage.addProperty("resistance", personnage.getResistance());
            jsonPersonnage.addProperty("type", personnage.getClass().getSimpleName());
            
            // Ajouter l'ID du joueur
            jsonPersonnage.addProperty("id_joueur", joueurId);
            
            // Ajouter l'inventaire si présent
            if (personnage.getInventaire() != null) {
                JsonElement inventaireJson = gson.toJsonTree(personnage.getInventaire());
                jsonPersonnage.add("inventaire", inventaireJson);
            }
            
            // Convertir en document MongoDB
            Document doc = Document.parse(jsonPersonnage.toString());
            return doc;
        } catch (Exception e) {
            throw new RuntimeException("Échec de la sérialisation: " + e.getMessage(), e);
        }
    }

    /**
     * Convertit un Document MongoDB en objet Personnage.
     *
     * @param doc Le Document MongoDB à convertir
     * @return Un objet Personnage créé à partir du Document
     */
    private Personnage fromDocument(Document doc) {
        try {
            String type = doc.getString("type");
            if (type == null) {
                throw new RuntimeException("Le champ 'type' est absent du document MongoDB");
            }
            
            Personnage personnage;
            switch (type) {
                case "Golem":
                    personnage = new Golem();
                    break;
                case "Guerrier":
                    personnage = new Guerrier();
                    break;
                case "Titan":
                    personnage = new Titan();
                    break;
                case "Voleur":
                    personnage = new Voleur();
                    break;
                default:
                    throw new RuntimeException("Type de personnage non reconnu: " + type);
            }
            
            // Définir les propriétés à partir du document
            personnage.setNom(doc.getString("nom"));
            personnage.setVie(doc.getDouble("vie"));
            personnage.setDegats(doc.getDouble("degats"));
            personnage.setResistance(doc.getDouble("resistance"));
            
            // Gérer l'inventaire si présent
            Document invDoc = (Document) doc.get("inventaire");
            if (invDoc != null) {
                Inventaire inventaire = gson.fromJson(invDoc.toJson(), Inventaire.class);
                personnage.setInventaire(inventaire);
            } else {
                personnage.setInventaire(new Inventaire());
            }
            
            return personnage;
        } catch (Exception e) {
            throw new RuntimeException("Échec de la désérialisation: " + e.getMessage(), e);
        }
    }
} 