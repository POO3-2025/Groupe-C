package be.helha.projects.GuerreDesRoyaumes.TUI;

import be.helha.projects.GuerreDesRoyaumes.Config.*;
import com.mongodb.client.MongoDatabase;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

public class TestConnexion {
    public static void main(String[] args) {
        try {
            // Chargement des propriétés
            Properties properties = loadApplicationProperties();

            // Initialisation de SQLConfigManager
            String sqlUrl = properties.getProperty("spring.datasource.url");
            String sqlUsername = properties.getProperty("spring.datasource.username");
            String sqlPassword = properties.getProperty("spring.datasource.password");
            String sqlDriverClassName = properties.getProperty("spring.datasource.driver-class-name");

            // Initialisation de MongoDBConfigManager
            String mongoHost = properties.getProperty("spring.data.mongodb.host");
            String mongoPort = properties.getProperty("spring.data.mongodb.port");
            String mongoDatabaseName = properties.getProperty("spring.data.mongodb.database");
            String mongoUsername = properties.getProperty("spring.data.mongodb.username");
            String mongoPassword = properties.getProperty("spring.data.mongodb.password");

            // Vérification des propriétés
            if (sqlUrl == null || sqlUsername == null || sqlPassword == null || sqlDriverClassName == null) {
                System.err.println("Propriétés SQL manquantes dans application.properties");
                return;
            }

            if (mongoHost == null || mongoPort == null || mongoDatabaseName == null) {
                System.err.println("Propriétés MongoDB manquantes dans application.properties");
                return;
            }

            // Initialisation des gestionnaires
            SQLConfigManager.initialize(sqlUrl, sqlUsername, sqlPassword, sqlDriverClassName);
            MongoDBConfigManager.initialize(mongoHost, mongoPort, mongoDatabaseName, mongoUsername, mongoPassword);

            // Utilisation du ConnexionManager pour tous les tests
            ConnexionManager connexionManager = ConnexionManager.getInstance();

            // Test SQL
            testSQL(connexionManager);

            // Test MongoDB
            testMongoDB(connexionManager);

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

    /**
     * Charge les propriétés depuis le fichier application.properties
     */
    private static Properties loadApplicationProperties() {
        Properties properties = new Properties();
        try {
            // Essayer de charger depuis le classpath
            try (InputStream input = TestConnexion.class.getClassLoader().getResourceAsStream("application.properties")) {
                if (input != null) {
                    properties.load(input);
                    return properties;
                }
            }

            // Si échec, essayer de charger depuis le chemin du système de fichiers
            try (FileInputStream fileInput = new FileInputStream("src/main/resources/application.properties")) {
                properties.load(fileInput);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de application.properties: " + e.getMessage());
            e.printStackTrace();
        }
        return properties;
    }
}