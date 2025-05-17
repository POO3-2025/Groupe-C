package be.helha.projects.GuerreDesRoyaumes.DAOImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.ItemMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.Config.InitialiserAPP;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.MongoDBConnectionException;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Arme;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Bouclier;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Potion;
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
    private ItemMongoDAOImpl() {
        try {
             mongoDB = InitialiserAPP.getMongoConnexion();
            this.itemCollection = mongoDB.getCollection("items");
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

    public void ajouterItem(Item item) {
        Document doc = toDocument(item);
        collection.insertOne(doc);
    }

    public List<Item> obtenirTousLesItems() {
        List<Item> items = new ArrayList<>();
        System.out.println("Tentative de récupération de tous les items depuis MongoDB...");

        try (MongoCursor<Document> cursor = itemCollection.find().iterator()) {
            while (cursor.hasNext()) {
                Document doc = cursor.next();
                System.out.println("Document trouvé: " + doc.toJson());
                Item item = convertirDocumentEnItem(doc);
                if (item != null) {
                    System.out.println("Item converti avec succès: ID=" + item.getId() + ", Nom=" + item.getNom() + ", Type=" + item.getClass().getSimpleName());
                    items.add(item);
                } else {
                    System.err.println("Échec de la conversion du document en item: " + doc.toJson());
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
        return fromDocument(doc);
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
        Document doc = new Document()
            .append("id", item.getId())
            .append("nom", item.getNom())
            .append("quantiteMax", item.getQuantiteMax())
            .append("type", item.getType())
            .append("prix", item.getPrix());

        // Ajouter des champs spécifiques selon le type d'item
        if (item instanceof Arme) {
            Arme arme = (Arme) item;
            doc.append("degats", arme.getDegats());
            doc.append("itemClass", "Arme");
        } else if (item instanceof Bouclier) {
            Bouclier bouclier = (Bouclier) item;
            doc.append("defense", bouclier.getDefense());
            doc.append("itemClass", "Bouclier");
        } else if (item instanceof Potion) {
            Potion potion = (Potion) item;
            doc.append("soin", potion.getSoin());
            doc.append("degats", potion.getDegats());
            doc.append("itemClass", "Potion");
        }

        return doc;
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

    private static Item fromDocument(org.bson.Document doc) {
        try {
            int id = doc.getInteger("id", 0);
            String nom = doc.getString("nom");
            int quantiteMax = doc.getInteger("quantiteMax", 1);
            int prix = doc.getInteger("prix", 0);
            
            // Utiliser itemClass en priorité, puis type comme fallback
            String itemClass = doc.getString("itemClass");
            String type = doc.getString("type");
            
            // Si itemClass est présent, l'utiliser pour déterminer le type
            if (itemClass != null && !itemClass.isEmpty()) {
                if (itemClass.equals("Arme")) {
                    double degats = getAsDouble(doc, "degats");
                    return new Arme(id, nom, quantiteMax, prix, degats);
                } else if (itemClass.equals("Bouclier")) {
                    double defense = getAsDouble(doc, "defense");
                    return new Bouclier(id, nom, quantiteMax, prix, defense);
                } else if (itemClass.equals("Potion")) {
                    double degats = getAsDouble(doc, "degats");
                    double soin = getAsDouble(doc, "soin");
                    return new Potion(id, nom, quantiteMax, prix, degats, soin);
                }
            }
            
            // Fallback sur le champ type
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
        } catch (Exception e) {
            System.err.println("Erreur lors de la conversion du document en item: " + e.getMessage());
            e.printStackTrace();
            return null;
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