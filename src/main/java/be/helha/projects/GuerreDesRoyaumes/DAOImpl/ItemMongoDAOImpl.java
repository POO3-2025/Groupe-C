package be.helha.projects.GuerreDesRoyaumes.DAOImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.ItemMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.Config.InitialiserAPP;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.MongoDBConnectionException;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Arme;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Bouclier;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Potion;
import be.helha.projects.GuerreDesRoyaumes.Outils.GsonObjectIdAdapter;
import com.google.gson.Gson;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ItemMongoDAOImpl implements ItemMongoDAO {
    private static ItemMongoDAOImpl instance;
    private final MongoCollection<Document> itemCollection;
    MongoDatabase mongoDB = null;
    private final Gson gson;

    private ItemMongoDAOImpl() {
        try {
             mongoDB = InitialiserAPP.getMongoConnexion();
            this.itemCollection = mongoDB.getCollection("items");
            this.gson = GsonObjectIdAdapter.getGson();
        } catch (MongoDBConnectionException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static synchronized ItemMongoDAOImpl getInstance() {
        if (instance == null) {
            instance = new ItemMongoDAOImpl();
        }
        return instance;
    }


    public List<Item> obtenirTousLesItems() {
        List<Item> items = new ArrayList<>();
        System.out.println("Tentative de récupération de tous les items depuis MongoDB...");

        try (MongoCursor<Document> cursor = itemCollection.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                System.out.println("Document trouvé: " + doc.toJson());
                
                // Traitement spécial pour le champ _id qui peut causer des problèmes
                if (doc.containsKey("_id")) {
                    // Créer une copie du document sans le champ _id
                    Document docSansId = new Document(doc);
                    docSansId.remove("_id");
                    
                    // Utiliser le document sans _id pour la conversion
                    Item item = convertirDocumentEnItem(docSansId);
                    if (item != null) {
                        System.out.println("Item converti avec succès: ID=" + item.getId() + ", Nom=" + item.getNom() + ", Type=" + item.getClass().getSimpleName());
                        items.add(item);
                    } else {
                        System.err.println("Échec de la conversion du document en item: " + docSansId.toJson());
                    }
                } else {
                    Item item = convertirDocumentEnItem(doc);
                    if (item != null) {
                        System.out.println("Item converti avec succès: ID=" + item.getId() + ", Nom=" + item.getNom() + ", Type=" + item.getClass().getSimpleName());
                        items.add(item);
                    } else {
                        System.err.println("Échec de la conversion du document en item: " + doc.toJson());
                    }
                }
            }
            System.out.println("Nombre total d'items récupérés: " + items.size());
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des items: " + e.getMessage());
            e.printStackTrace();
        }

        return items;
    }

    private Item convertirDocumentEnItem(Document doc) {
        try {
            // Afficher le document pour debug
            System.out.println("Conversion du document en item: " + doc.toJson());
            
            // Vérifier que le document contient les champs requis
            if (!doc.containsKey("id") || !doc.containsKey("nom") || !doc.containsKey("type")) {
                System.err.println("Document incomplet, champs requis manquants (id, nom, type)");
                return null;
            }
            
            // Extraire les champs communs
            int id = doc.getInteger("id");
            String nom = doc.getString("nom");
            String type = doc.getString("type");
            int prix = 0;
            if (doc.containsKey("prix")) {
                prix = doc.getInteger("prix");
            }
            int quantiteMax = 1;
            if (doc.containsKey("quantiteMax")) {
                quantiteMax = doc.getInteger("quantiteMax");
            }
            
            // Créer l'objet approprié en fonction du type
            if ("Arme".equals(type)) {
                double degats = 0;
                if (doc.containsKey("degats")) {
                    degats = getAsDouble(doc, "degats");
                }
                System.out.println("Création d'une Arme: " + nom + " avec dégâts: " + degats);
                return new Arme(id, nom, quantiteMax, prix, degats);
            } else if ("Bouclier".equals(type)) {
                double defense = 0;
                if (doc.containsKey("defense")) {
                    defense = getAsDouble(doc, "defense");
                }
                System.out.println("Création d'un Bouclier: " + nom + " avec défense: " + defense);
                return new Bouclier(id, nom, quantiteMax, prix, defense);
            } else if ("Potion".equals(type)) {
                double soin = 0;
                if (doc.containsKey("soin")) {
                    soin = getAsDouble(doc, "soin");
                }
                double degats = 0;
                if (doc.containsKey("degats")) {
                    degats = getAsDouble(doc, "degats");
                }
                System.out.println("Création d'une Potion: " + nom + " avec soin: " + soin + " et dégâts: " + degats);
                return new Potion(id, nom, quantiteMax, prix, degats, soin);
            } else {
                System.err.println("Type d'item non reconnu: " + type);
                return null;
            }
        } catch (Exception e) {
            //Gestion robuste des erreurs
            System.err.println("Erreur lors de la conversion du document en item: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static double getAsDouble(Document doc, String key) {
        Object value = doc.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return 0.0;
    }

    @Override
    public List<Item> obtenirItemsParType(String type) {
        List<Item> items = new ArrayList<>();

        try (MongoCursor<Document> cursor = itemCollection.find(Filters.eq("type", type)).iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Item item = convertirDocumentEnItem(doc);
                if (item != null) {
                    items.add(item);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des items par type: " + e.getMessage());
        }

        return items;
    }

    @Override
    public Item obtenirItemParId(int id) {
        try {
            Document doc = itemCollection.find(Filters.eq("id", id)).first();
            return (doc != null) ? convertirDocumentEnItem(doc) : null;
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération de l'item par ID: " + e.getMessage());
            return null;
        }
    }

    @Override
    public void ajouterItem(Item item) {
        try {
            Document itemDoc = convertirItemEnDocument(item);
            itemCollection.insertOne(itemDoc);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout de l'item: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private Document convertirItemEnDocument(Item item) {
        try {
            Gson gson = GsonObjectIdAdapter.getGson();
            String json = gson.toJson(item);
            return Document.parse(json);
        } catch (Exception e) {
            System.err.println("Erreur critique lors de la conversion de l'item en document: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Échec de la sérialisation Gson", e);
        }
    }

    @Override
    public void mettreAJourItem(Item item) {
        try {
            Bson filter = Filters.eq("id", item.getId());
            Document updateDoc = convertirItemEnDocument(item);
            itemCollection.replaceOne(filter, updateDoc);
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour de l'item: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void supprimerItem(int id) {
        try {
            itemCollection.deleteOne(Filters.eq("id", id));
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression de l'item: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Méthode pour générer un nouvel ID unique
    public int genererNouvelId() {
        Document maxDoc = itemCollection.find()
                .sort(new Document("id", -1))
                .limit(1)
                .first();

        return (maxDoc != null) ? maxDoc.getInteger("id") + 1 : 1;
    }

    public void supprimerTousLesItems() {
        try {
            itemCollection.deleteMany(new Document()); // supprime tous les documents
            System.out.println("Tous les items ont été supprimés de la collection.");
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression de tous les items : " + e.getMessage());
            e.printStackTrace();
        }
    }
}