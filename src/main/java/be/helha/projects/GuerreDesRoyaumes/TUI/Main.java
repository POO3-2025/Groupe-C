package be.helha.projects.GuerreDesRoyaumes.TUI;

import be.helha.projects.GuerreDesRoyaumes.Config.DatabaseConfigManager;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoDatabase;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        // Récupérer l'instance du manager
        DatabaseConfigManager configManager = DatabaseConfigManager.getInstance();

        // --- Tester la récupération des données MySQL ---
        System.out.println("===== Test récupération MySQL =====");
        JsonObject mysqlConfig = configManager.getConfig()
                .getAsJsonObject("db")
                .getAsJsonObject("mysql")
                .getAsJsonObject("BDCredentials");
        System.out.println("Infos MySQL :");
        System.out.println("Host: " + mysqlConfig.get("HostName").getAsString());
        System.out.println("Port: " + mysqlConfig.get("Port").getAsString());
        System.out.println("Database: " + mysqlConfig.get("DBName").getAsString());
        System.out.println("User: " + mysqlConfig.get("UserName").getAsString());
        System.out.println("Password: " + mysqlConfig.get("Password").getAsString());

        // --- Tester la connexion MySQL ---
        System.out.println("\n===== Test connexion MySQL =====");
        try (Connection conn = configManager.getSQLConnection("mysql")) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Vous êtes bien connecté à MySQL !");
            } else {
                System.out.println("Connexion MySQL échouée !");
            }
        } catch (Exception e) {
            System.out.println("Erreur de connexion MySQL : " + e.getMessage());
        }

        // --- Tester la récupération des données MongoDB ---
        System.out.println("\n===== Test récupération MongoDB =====");
        JsonObject mongoConfig = configManager.getConfig()
                .getAsJsonObject("db")
                .getAsJsonObject("mongodb")
                .getAsJsonObject("BDCredentials");
        System.out.println("Infos MongoDB :");
        System.out.println("Host: " + mongoConfig.get("HostName").getAsString());
        System.out.println("Port: " + mongoConfig.get("Port").getAsString());
        System.out.println("Database: " + mongoConfig.get("DBName").getAsString());
        System.out.println("User: " + mongoConfig.get("UserName").getAsString());
        System.out.println("Password: " + mongoConfig.get("Password").getAsString());

        // --- Tester la connexion MongoDB ---
        System.out.println("\n===== Test connexion MongoDB =====");
        try {
            MongoDatabase mongoDb = configManager.getMongoDatabase("mongodb");
            if (mongoDb != null) {
                System.out.println("Vous êtes bien connecté à MongoDB !");
            } else {
                System.out.println("Connexion MongoDB échouée !");
            }
        } catch (Exception e) {
            System.out.println("Erreur de connexion MongoDB : " + e.getMessage());
        }

        // Fermer le client MongoDB proprement
        configManager.closeMongoClient();
    }
}
