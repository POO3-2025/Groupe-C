package be.helha.projects.GuerreDesRoyaumes.TUI;

import be.helha.projects.GuerreDesRoyaumes.Config.ConnexionConfig.ConfigInit;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.ItemMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Arme;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Bouclier;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Potion;
import be.helha.projects.GuerreDesRoyaumes.Config.InitialiserAPP;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.util.List;

public class TestRecupItemsMongo {

    public static void main(String[] args) {
        try {
            // Initialisation des configurations
            ConfigInit.initAll();
            
            System.out.println("=== Test de récupération des items depuis MongoDB ===");
            
            // Récupérer l'instance du DAO
            ItemMongoDAOImpl itemMongoDAO = ItemMongoDAOImpl.getInstance();
            
            // Récupérer tous les items
            List<Item> items = itemMongoDAO.obtenirTousLesItems();
            
            System.out.println("\n=== Résumé des items récupérés ===");
            if (items.isEmpty()) {
                System.out.println("AUCUN ITEM TROUVÉ DANS LA BASE DE DONNÉES!");
            } else {
                System.out.println("Nombre total d'items récupérés: " + items.size());
                
                int countArmes = 0;
                int countBoucliers = 0;
                int countPotions = 0;
                int countAutres = 0;
                
                for (Item item : items) {
                    if (item instanceof Arme) {
                        countArmes++;
                    } else if (item instanceof Bouclier) {
                        countBoucliers++;
                    } else if (item instanceof Potion) {
                        countPotions++;
                    } else {
                        countAutres++;
                    }
                }
                
                System.out.println("Armes: " + countArmes);
                System.out.println("Boucliers: " + countBoucliers);
                System.out.println("Potions: " + countPotions);
                System.out.println("Autres: " + countAutres);
                
                System.out.println("\nDétail des 5 premiers items:");
                for (int i = 0; i < Math.min(5, items.size()); i++) {
                    Item item = items.get(i);
                    System.out.println(i + ": " + item.getClass().getSimpleName() + " - ID: " + item.getId() + ", Nom: " + item.getNom() + ", Prix: " + item.getPrix());
                }
            }
            
            // Examiner directement les documents dans MongoDB
            System.out.println("\n=== Examen direct des documents MongoDB ===");
            examinerDocumentsMongoDirectement();
            
            System.out.println("\n=== Test terminé ===");
            
        } catch (Exception e) {
            System.err.println("ERREUR CRITIQUE: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void examinerDocumentsMongoDirectement() {
        try {
            MongoDatabase mongoDB = InitialiserAPP.getMongoConnexion();
            MongoCollection<Document> itemCollection = mongoDB.getCollection("items");
            
            System.out.println("Nombre de documents dans la collection 'items': " + itemCollection.countDocuments());
            
            System.out.println("Échantillon de 5 documents:");
            try (MongoCursor<Document> cursor = itemCollection.find().limit(5).iterator()) {
                int count = 0;
                while (cursor.hasNext()) {
                    Document doc = cursor.next();
                    System.out.println("Document " + (++count) + ": " + doc.toJson());
                    // Afficher les champs clés
                    Object id = doc.get("id");
                    Object nom = doc.get("nom");
                    Object type = doc.get("type");
                    Object prix = doc.get("prix");
                    
                    System.out.println("Champs clés: id=" + id + ", nom=" + nom + ", type=" + type + ", prix=" + prix);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'examen direct de MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 