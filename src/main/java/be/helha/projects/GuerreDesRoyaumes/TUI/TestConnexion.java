package be.helha.projects.GuerreDesRoyaumes.TUI;

import be.helha.projects.GuerreDesRoyaumes.Config.ConnexionConfig.ConfigInit;
import be.helha.projects.GuerreDesRoyaumes.Config.ConnexionConfig.ConnexionManager;
import com.mongodb.client.MongoDatabase;
import java.sql.Connection;

public class TestConnexion {
    public static void main(String[] args) {
        try {
            // Initialisation centralisée des configs
            ConfigInit.initAll();

            ConnexionManager manager = ConnexionManager.getInstance();

            // Test SQL
            testSQL(manager);

            // Test MongoDB
            testMongoDB(manager);

        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testSQL(ConnexionManager manager) {
        System.out.println("===== TEST SQL =====");
        try (Connection conn = manager.getSQLConnection()) {
            System.out.println(conn != null ? "Connexion SQL réussie!" : "Échec connexion SQL");
            if (conn != null) {
                System.out.println("URL de connexion: " + conn.getMetaData().getURL());
                System.out.println("Utilisateur: " + conn.getMetaData().getUserName());
                System.out.println("Version de la base de données: " + conn.getMetaData().getDatabaseProductName() + " "
                        + conn.getMetaData().getDatabaseProductVersion());
            }
        } catch (Exception e) {
            System.err.println("Erreur SQL: " + e.getMessage());
        }
    }

    private static void testMongoDB(ConnexionManager manager) {
        System.out.println("\n===== TEST MongoDB =====");
        try {
            MongoDatabase db = manager.getMongoDatabase();
            System.out.println(db != null ? "Connexion MongoDB réussie!" : "Échec connexion MongoDB");
            if (db != null) {
                System.out.println("Nom de la base de données: " + db.getName());
                System.out.println("Collections disponibles:");
                db.listCollectionNames().forEach(name -> System.out.println("- " + name));
            }
        } catch (Exception e) {
            System.err.println("Erreur MongoDB: " + e.getMessage());
        } finally {
            manager.closeConnections();
        }
    }
}