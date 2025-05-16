package be.helha.projects.GuerreDesRoyaumes.DAOImpl;

import be.helha.projects.GuerreDesRoyaumes.Config.ConnexionManager;
import be.helha.projects.GuerreDesRoyaumes.DAO.CoffreDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Arme;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Bouclier;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Potion;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CoffreMongoDAOImpl implements CoffreDAO {

    private static CoffreMongoDAOImpl instance;
    private MongoDatabase database;
    private MongoCollection<Document> coffreCollection;
    private static final String COLLECTION_NAME = "coffres";

    private CoffreMongoDAOImpl() {
        try {
            // Utiliser ConnexionManager pour obtenir la base de données MongoDB
            database = ConnexionManager.getInstance().getMongoDatabase();

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
                System.out.println("Collection coffres créée avec succès");
            }

            coffreCollection = database.getCollection(COLLECTION_NAME);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation de CoffreMongoDAOImpl: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static synchronized CoffreMongoDAOImpl getInstance() {
        if (instance == null) {
            instance = new CoffreMongoDAOImpl();
        }
        return instance;
    }

    @Override
    public List<Item> obtenirItemsDuCoffre(String nomJoueur) {
        List<Item> items = new ArrayList<>();

        try {
            Document coffre = coffreCollection.find(Filters.eq("nomJoueur", nomJoueur)).first();

            if (coffre != null) {
                List<Document> itemsDoc = (List<Document>) coffre.get("items");

                for (Document itemDoc : itemsDoc) {
                    Item item = convertirDocumentEnItem(itemDoc);
                    if (item != null) {
                        items.add(item);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des items du coffre: " + e.getMessage());
            e.printStackTrace();
        }

        return items;
    }

    @Override
    public boolean ajouterItemAuCoffre(String nomJoueur, Item item) {
        try {
            // Convertir l'item en document
            Document itemDoc = convertirItemEnDocument(item);

            // Vérifier si le joueur a déjà un coffre
            Document coffre = coffreCollection.find(Filters.eq("nomJoueur", nomJoueur)).first();

            if (coffre == null) {
                // Créer un nouveau coffre pour le joueur
                Document nouveauCoffre = new Document()
                        .append("nomJoueur", nomJoueur)
                        .append("items", new ArrayList<Document>() {{ add(itemDoc); }});

                coffreCollection.insertOne(nouveauCoffre);
            } else {
                // Ajouter l'item au coffre existant
                Bson filter = Filters.eq("nomJoueur", nomJoueur);
                Bson update = Updates.push("items", itemDoc);
                coffreCollection.updateOne(filter, update);
            }

            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout d'un item au coffre: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean supprimerItemDuCoffre(String nomJoueur, int itemId) {
        try {
            // Récupérer le coffre du joueur
            Document coffre = coffreCollection.find(Filters.eq("nomJoueur", nomJoueur)).first();

            if (coffre != null) {
                // Récupérer la liste des items
                List<Document> items = (List<Document>) coffre.get("items");

                // Créer une nouvelle liste sans l'item à supprimer
                List<Document> nouveauxItems = new ArrayList<>();
                boolean itemTrouve = false;

                for (Document item : items) {
                    if (item.getInteger("id") != itemId) {
                        nouveauxItems.add(item);
                    } else {
                        itemTrouve = true;
                    }
                }

                if (itemTrouve) {
                    // Mettre à jour le coffre
                    Bson filter = Filters.eq("nomJoueur", nomJoueur);
                    Bson update = Updates.set("items", nouveauxItems);
                    coffreCollection.updateOne(filter, update);
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression d'un item du coffre: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // Méthodes utilitaires pour la conversion entre Document et Item
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
}