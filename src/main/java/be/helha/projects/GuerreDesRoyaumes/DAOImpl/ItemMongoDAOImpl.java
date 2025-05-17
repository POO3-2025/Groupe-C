package be.helha.projects.GuerreDesRoyaumes.DAOImpl;

import be.helha.projects.GuerreDesRoyaumes.Config.ConnexionConfig.ConnexionManager;
import be.helha.projects.GuerreDesRoyaumes.DAO.ItemDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Arme;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Bouclier;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Potion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ItemMongoDAOImpl implements ItemDAO {

    private static ItemMongoDAOImpl instance;
    private MongoDatabase database;
    private MongoCollection<Document> itemCollection;
    private static final String COLLECTION_NAME = "items";

    private ItemMongoDAOImpl() {
        try {
            // Utiliser ConnexionManager pour obtenir la base de données MongoDB
            database = ConnexionManager.getInstance().getMongoDatabase();
            itemCollection = database.getCollection(COLLECTION_NAME);

            // Vérifier si la collection existe déjà, sinon la créer
            boolean collectionExists = false;
            for (String name : database.listCollectionNames()) {
                if (name.equals(COLLECTION_NAME)) {
                    collectionExists = true;
                    break;
                }
            }

            if (!collectionExists) {
                database.createCollection(COLLECTION_NAME);
                System.out.println("Collection items créée avec succès");
                // Initialiser avec des items par défaut
                initialiserItemsParDefaut();
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation de ItemMongoDAOImpl: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static synchronized ItemMongoDAOImpl getInstance() {
        if (instance == null) {
            instance = new ItemMongoDAOImpl();
        }
        return instance;
    }

    @Override
    public List<Item> obtenirTousLesItems() {
        List<Item> items = new ArrayList<>();

        try (MongoCursor<Document> cursor = itemCollection.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                Item item = convertirDocumentEnItem(doc);
                if (item != null) {
                    items.add(item);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des items: " + e.getMessage());
        }

        return items;
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

    /**
     * Initialise la collection avec des items par défaut
     * Cette méthode est appelée automatiquement si la collection n'existe pas
     * Elle peut aussi être appelée manuellement pour réinitialiser les items
     */
    public void initialiserItemsParDefaut() {
        System.out.println("Initialisation des items par défaut dans la collection MongoDB...");

        // Créer des armes
        ajouterItem(new Arme(1, "Épée courte", 100, 5, 5.0));
        ajouterItem(new Arme(2, "Épée longue", 100, 10, 10.0));
        ajouterItem(new Arme(3, "Hache de guerre", 100, 15, 15.0));
        ajouterItem(new Arme(4, "Arc court", 100, 8, 8.0));
        ajouterItem(new Arme(5, "Arc long", 100, 12, 12.0));

        // Créer des boucliers
        ajouterItem(new Bouclier(6, "Bouclier en bois", 100, 3, 3.0));
        ajouterItem(new Bouclier(7, "Bouclier en fer", 100, 7, 7.0));
        ajouterItem(new Bouclier(8, "Bouclier en acier", 100, 12, 12.0));
        ajouterItem(new Bouclier(9, "Bouclier royal", 100, 20, 20.0));

        // Créer des potions
        ajouterItem(new Potion(10, "Potion de soin mineure", 100, 30, 0.0, 20.0));
        ajouterItem(new Potion(11, "Potion de soin moyenne", 100, 75, 0.0, 50.0));
        ajouterItem(new Potion(12, "Potion de soin majeure", 100, 150, 0.0, 100.0));
        ajouterItem(new Potion(13, "Potion de poison", 100, 100, 30.0, 0.0));
        ajouterItem(new Potion(14, "Potion explosive", 100, 200, 60.0, 0.0));

        System.out.println("Items par défaut ajoutés à la collection MongoDB avec succès!");
    }

    private Document convertirItemEnDocument(Item item) {
        Document doc = new Document()
                .append("id", item.getId())
                .append("nom", item.getNom())
                .append("prix", item.getPrix())
                .append("type", item.getType());

        // Ajouter les propriétés spécifiques selon le type d'item
        if (item instanceof Arme) {
            doc.append("itemClass", "Arme");
            doc.append("degats", ((Arme) item).getDegats());
        } else if (item instanceof Bouclier) {
            doc.append("itemClass", "Bouclier");
            doc.append("defense", ((Bouclier) item).getDefense());
        } else if (item instanceof Potion) {
            doc.append("itemClass", "Potion");
            doc.append("soin", ((Potion) item).getSoin());
            doc.append("degats", ((Potion) item).getDegats());
        }

        return doc;
    }

    private Item convertirDocumentEnItem(Document doc) {
        Item item = null;

        try {
            int id = doc.getInteger("id");
            String nom = doc.getString("nom");
            int prix = doc.getInteger("prix");
            String type = doc.getString("type");
            String itemClass = doc.getString("itemClass");
            int quantiteMax = 100; // Valeur par défaut pour la quantité max

            switch (itemClass) {
                case "Arme":
                    double degatsArme = 0.0;
                    if (doc.containsKey("degats")) {
                        degatsArme = doc.getDouble("degats");
                    }
                    item = new Arme(id, nom, quantiteMax, prix, degatsArme);
                    break;

                case "Bouclier":
                    double defense = 0.0;
                    if (doc.containsKey("defense")) {
                        defense = doc.getDouble("defense");
                    }
                    item = new Bouclier(id, nom, quantiteMax, prix, defense);
                    break;

                case "Potion":
                    double soin = 0.0;
                    if (doc.containsKey("soin")) {
                        soin = doc.getDouble("soin");
                    }
                    double degatsPotion = 0.0;
                    if (doc.containsKey("degats")) {
                        degatsPotion = doc.getDouble("degats");
                    }
                    item = new Potion(id, nom, quantiteMax, prix, degatsPotion, soin);
                    break;
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la conversion du document en item: " + e.getMessage());
        }

        return item;
    }

    // Méthode pour générer un nouvel ID unique
    public int genererNouvelId() {
        Document maxDoc = itemCollection.find()
                .sort(new Document("id", -1))
                .limit(1)
                .first();

        return (maxDoc != null) ? maxDoc.getInteger("id") + 1 : 1;
    }

    /**
     * Vide la collection d'items et la réinitialise avec les items par défaut
     * @return true si l'opération a réussi, false sinon
     */
    public boolean reinitialiserCollection() {
        try {
            itemCollection.drop();
            System.out.println("Collection items supprimée");

            // Recréer la collection
            database.createCollection(COLLECTION_NAME);
            itemCollection = database.getCollection(COLLECTION_NAME);
            System.out.println("Collection items recréée");

            // Initialiser avec des items par défaut
            initialiserItemsParDefaut();
            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors de la réinitialisation de la collection: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}