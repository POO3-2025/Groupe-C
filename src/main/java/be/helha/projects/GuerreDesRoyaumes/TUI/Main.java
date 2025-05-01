package be.helha.projects.GuerreDesRoyaumes.TUI;

import be.helha.projects.GuerreDesRoyaumes.Config.DatabaseConfigManager;
import com.google.gson.JsonObject;
import com.mongodb.client.MongoDatabase;

import java.sql.Connection;

public class Main {
    public static void main(String[] args) {
        // Récupérer l'instance du manager
        DatabaseConfigManager configManager = DatabaseConfigManager.getInstance();

        // --- Tester la récupération des données sqlserver ---
        System.out.println("===== Test récupération sqlserver =====");
        JsonObject mysqlConfig = configManager.getConfig()
                .getAsJsonObject("db")
                .getAsJsonObject("sqlserver")
                .getAsJsonObject("BDCredentials");
        System.out.println("Infos sqlserver :");
        System.out.println("Host: " + mysqlConfig.get("HostName").getAsString());
        System.out.println("Port: " + mysqlConfig.get("Port").getAsString());
        System.out.println("Database: " + mysqlConfig.get("DBName").getAsString());
        System.out.println("User: " + mysqlConfig.get("UserName").getAsString());
        System.out.println("Password: " + mysqlConfig.get("Password").getAsString());

        // --- Tester la connexion sqlserver ---
        System.out.println("\n===== Test connexion sqlserver =====");
        try (Connection conn = configManager.getSQLConnection("sqlserver")) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Vous êtes bien connecté à sqlserver !");
            } else {
                System.out.println("Connexion sqlserver échouée !");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur de connexion sqlserver : ");
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
