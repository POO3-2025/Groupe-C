package be.helha.projects.GuerreDesRoyaumes.TUI;

import be.helha.projects.GuerreDesRoyaumes.Config.*;
import com.mongodb.client.MongoDatabase;

import java.sql.Connection;

public class TestConnexion {
    public static void main(String[] args) {
        // Managers
        SQLConfigManager sqlManager = SQLConfigManager.getInstance();
        MongoDBConfigManager mongoManager = MongoDBConfigManager.getInstance();

        // Test SQL
        testSQL(sqlManager);

        // Test MongoDB
        testMongoDB(mongoManager);
    }

    private static void testSQL(SQLConfigManager manager) {
        System.out.println("===== TEST SQL =====");

        MySqlConfigurations config = manager.getConfigurations("sqlserver");
        System.out.println("Configuration SQL:");
        System.out.println(config);

        try (Connection conn = manager.getConnection("sqlserver")) {
            System.out.println(conn != null ? "Connexion SQL réussie!" : "Échec connexion SQL");
        } catch (Exception e) {
            System.err.println("Erreur SQL: " + e.getMessage());
        }
    }

    private static void testMongoDB(MongoDBConfigManager manager) {
        System.out.println("\n===== TEST MongoDB =====");

        MongoDBConfigurations config = manager.getConfigurations("mongodb");
        System.out.println("Configuration MongoDB:");
        System.out.println(config);

        try {
            MongoDatabase db = manager.getDatabase("mongodb");
            System.out.println(db != null ? "Connexion MongoDB réussie!" : "Échec connexion MongoDB");
        } catch (Exception e) {
            System.err.println("Erreur MongoDB: " + e.getMessage());
        } finally {
            manager.closeClient();
        }
    }
}