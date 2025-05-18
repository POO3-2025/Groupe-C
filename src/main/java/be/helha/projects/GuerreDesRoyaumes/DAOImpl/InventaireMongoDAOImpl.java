package be.helha.projects.GuerreDesRoyaumes.DAOImpl;

import be.helha.projects.GuerreDesRoyaumes.Config.ConnexionConfig.ConnexionManager;
import be.helha.projects.GuerreDesRoyaumes.DAO.InventaireDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Arme;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Bouclier;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Potion;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.DeleteResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class InventaireMongoDAOImpl implements InventaireDAO {

    private static InventaireMongoDAOImpl instance;
    private MongoDatabase database;
    private MongoCollection<Document> inventaireCollection;
    private static final String COLLECTION_NAME = "inventaires";

    /**
     * Constructeur public pour l'injection de dépendances avec Spring
     */
    public InventaireMongoDAOImpl() {
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
                System.out.println("Collection inventaires créée avec succès");
            }

            inventaireCollection = database.getCollection(COLLECTION_NAME);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation de InventaireMongoDAOImpl: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static synchronized InventaireMongoDAOImpl getInstance() {
        if (instance == null) {
            instance = new InventaireMongoDAOImpl();
        }
        return instance;
    }

    @Override
    public List<Item> obtenirItemsInventaire(String nomJoueur) {
        List<Item> items = new ArrayList<>();

        try {
            Document inventaire = inventaireCollection.find(Filters.eq("nomJoueur", nomJoueur)).first();

            if (inventaire != null) {
                List<Document> itemsDoc = (List<Document>) inventaire.get("items");

                for (Document itemDoc : itemsDoc) {
                    Item item = convertirDocumentEnItem(itemDoc);
                    if (item != null) {
                        items.add(item);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des items de l'inventaire: " + e.getMessage());
            e.printStackTrace();
        }

        return items;
    }

    @Override
    public boolean ajouterItemInventaire(String nomJoueur, Item item) {
        try {
            // Convertir l'item en document
            Document itemDoc = convertirItemEnDocument(item);

            // Vérifier si le joueur a déjà un inventaire
            Document inventaire = inventaireCollection.find(Filters.eq("nomJoueur", nomJoueur)).first();

            if (inventaire == null) {
                // Créer un nouveau inventaire pour le joueur
                Document nouveauInventaire = new Document()
                        .append("nomJoueur", nomJoueur)
                        .append("items", new ArrayList<Document>() {{ add(itemDoc); }});

                inventaireCollection.insertOne(nouveauInventaire);
            } else {
                // Ajouter l'item à l'inventaire existant
                Bson filter = Filters.eq("nomJoueur", nomJoueur);
                Bson update = Updates.push("items", itemDoc);
                inventaireCollection.updateOne(filter, update);
            }

            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors de l'ajout d'un item à l'inventaire: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean supprimerItemInventaire(String nomJoueur, int itemId) {
        try {
            // Récupérer l'inventaire du joueur
            Document inventaire = inventaireCollection.find(Filters.eq("nomJoueur", nomJoueur)).first();

            if (inventaire != null) {
                // Récupérer la liste des items
                List<Document> items = (List<Document>) inventaire.get("items");

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
                    // Mettre à jour l'inventaire
                    Bson filter = Filters.eq("nomJoueur", nomJoueur);
                    Bson update = Updates.set("items", nouveauxItems);
                    inventaireCollection.updateOne(filter, update);
                    return true;
                }
            }

            return false;
        } catch (Exception e) {
            System.err.println("Erreur lors de la suppression d'un item de l'inventaire: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean viderInventaire(String nomJoueur) {
        try {
            // Vérifier si le joueur a déjà un inventaire
            Document inventaire = inventaireCollection.find(Filters.eq("nomJoueur", nomJoueur)).first();

            if (inventaire != null) {
                // Vider la liste des items
                Bson filter = Filters.eq("nomJoueur", nomJoueur);
                Bson update = Updates.set("items", new ArrayList<Document>());
                inventaireCollection.updateOne(filter, update);
                return true;
            }

            return false;
        } catch (Exception e) {
            System.err.println("Erreur lors du vidage de l'inventaire: " + e.getMessage());
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