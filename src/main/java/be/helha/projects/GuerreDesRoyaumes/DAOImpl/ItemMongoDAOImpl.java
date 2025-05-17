package be.helha.projects.GuerreDesRoyaumes.DAOImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.ItemMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Arme;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Bouclier;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Potion;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Config.ConnexionConfig.ConnexionManager;

import java.util.ArrayList;
import java.util.List;


public class ItemMongoDAOImpl implements ItemMongoDAO {
    private static ItemMongoDAOImpl instance;
    private final MongoCollection<Document> collection;

    private ItemMongoDAOImpl() {
        MongoDatabase db = ConnexionManager.getInstance().getMongoDatabase();
        this.collection = db.getCollection("items");
    }

    public static synchronized ItemMongoDAOImpl getInstance() {
        if (instance == null) {
            instance = new ItemMongoDAOImpl();
        }
        return instance;
    }

    public void ajouterItem(Item item) {
        Document doc = toDocument(item);
        collection.insertOne(doc);
    }

    public List<Item> obtenirTousLesItems() {
        List<Item> items = new ArrayList<>();
        for (Document doc : collection.find()) {
            items.add(fromDocument(doc));
        }
        return items;
    }

    private static double getAsDouble(Document doc, String key) {
        Object value = doc.get(key);
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return 0.0;
    }

    private static Item fromDocument(org.bson.Document doc) {
        int id = doc.getInteger("id", 0);
        String nom = doc.getString("nom");
        int quantiteMax = doc.getInteger("quantiteMax", 1);
        String type = doc.getString("type");
        int prix = doc.getInteger("prix", 0);

        if (type == null) type = "item";
        type = type.toLowerCase();

        if (type.contains("arme")) {
            double degats = getAsDouble(doc, "degats");
            return new Arme(id, nom, quantiteMax, prix, degats);
        } else if (type.contains("bouclier")) {
            double defense = getAsDouble(doc, "defense");
            return new Bouclier(id, nom, quantiteMax, prix, defense);
        } else if (type.contains("potion")) {
            double degats = getAsDouble(doc, "degats");
            double soin = getAsDouble(doc, "soin");
            return new Potion(id, nom, quantiteMax, prix, degats, soin);
        } else {
            // Item générique si le type n'est pas reconnu
            return new Item(id, nom, quantiteMax, type, prix) {
                @Override
                public void use() {
                    System.out.println("Utilisation de l'item générique : " + getNom());
                }
            };
        }
    }

    private static Document toDocument(Item item) {
        Document doc = new Document();
        doc.append("nom", item.getNom());
        doc.append("quantiteMax", item.getQuantiteMax());
        doc.append("type", item.getType());
        doc.append("prix", item.getPrix());

        // Selon le type, ajouter les champs spécifiques
        if (item instanceof Arme) {
            doc.append("degats", ((Arme) item).getDegats());
        } else if (item instanceof Bouclier) {
            doc.append("defense", ((Bouclier) item).getDefense());
        } else if (item instanceof Potion) {
            Potion potion = (Potion) item;
            doc.append("degats", potion.getDegats());
            doc.append("soin", potion.getSoin());
        }
        return doc;
    }

}