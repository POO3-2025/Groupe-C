package be.helha.projects.GuerreDesRoyaumes.TUI;

import be.helha.projects.GuerreDesRoyaumes.Config.ConnexionConfig.ConfigInit;
import be.helha.projects.GuerreDesRoyaumes.Config.InitialiserAPP;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class TestAjoutItemSpecifique {

    public static void main(String[] args) {
        // Initialisation des configurations
        ConfigInit.initAll();
        
        try {
            System.out.println("=== Test d'ajout d'un item spécifique dans MongoDB ===");
            
            // Récupérer la connexion MongoDB
            MongoDatabase mongoDB = InitialiserAPP.getMongoConnexion();
            MongoCollection<Document> itemCollection = mongoDB.getCollection("items");
            
            // Structure de l'item telle que fournie dans l'exemple
            String itemJson = "{"
                + "\"id\": 1,"
                + "\"nom\": \"épée en bois\","
                + "\"quantiteMax\": 1,"
                + "\"type\": \"Arme\","
                + "\"prix\": 10,"
                + "\"degats\": 10,"
                + "\"itemClass\": \"Arme\""
                + "}";
            
            // Convertir le JSON en Document MongoDB
            Document itemDoc = Document.parse(itemJson);
            
            // Utiliser une méthode sécurisée pour s'assurer que l'item n'existe pas déjà
            Document existingItem = itemCollection.find(new Document("id", 1)).first();
            if (existingItem != null) {
                System.out.println("L'item avec l'ID 1 existe déjà. Suppression et réinsertion...");
                itemCollection.deleteOne(new Document("id", 1));
            }
            
            // Insérer le document dans la collection
            itemCollection.insertOne(itemDoc);
            System.out.println("Item inséré avec succès : " + itemJson);
            
            // Ajouter d'autres items pour enrichir la boutique
            // Ajouter un bouclier
            String bouclierJson = "{"
                + "\"id\": 2,"
                + "\"nom\": \"bouclier en bois\","
                + "\"quantiteMax\": 1,"
                + "\"type\": \"Bouclier\","
                + "\"prix\": 15,"
                + "\"defense\": 10,"
                + "\"itemClass\": \"Bouclier\""
                + "}";
            
            Document bouclierDoc = Document.parse(bouclierJson);
            existingItem = itemCollection.find(new Document("id", 2)).first();
            if (existingItem != null) {
                itemCollection.deleteOne(new Document("id", 2));
            }
            itemCollection.insertOne(bouclierDoc);
            System.out.println("Bouclier inséré avec succès : " + bouclierJson);
            
            // Ajouter une potion
            String potionJson = "{"
                + "\"id\": 3,"
                + "\"nom\": \"potion de soin\","
                + "\"quantiteMax\": 5,"
                + "\"type\": \"Potion\","
                + "\"prix\": 20,"
                + "\"soin\": 20,"
                + "\"degats\": 0,"
                + "\"itemClass\": \"Potion\""
                + "}";
            
            Document potionDoc = Document.parse(potionJson);
            existingItem = itemCollection.find(new Document("id", 3)).first();
            if (existingItem != null) {
                itemCollection.deleteOne(new Document("id", 3));
            }
            itemCollection.insertOne(potionDoc);
            System.out.println("Potion insérée avec succès : " + potionJson);
            
            // Vérifier le contenu de la collection
            long count = itemCollection.countDocuments();
            System.out.println("Nombre total d'items dans la collection : " + count);
            
            System.out.println("=== Test terminé avec succès ===");
            
        } catch (Exception e) {
            System.err.println("ERREUR CRITIQUE: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 