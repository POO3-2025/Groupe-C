package be.helha.projects.GuerreDesRoyaumes.TUI;

import be.helha.projects.GuerreDesRoyaumes.Config.DatabaseConfigManager;
import com.mongodb.client.MongoDatabase;

import java.sql.Connection;
import java.sql.SQLException;

public class TestDatabaseConnection {
    public static void main(String[] args) {
        DatabaseConfigManager dbConfigManager = DatabaseConfigManager.getInstance();

        // Test SQL Server connection
        try (Connection sqlConnection = dbConfigManager.getSQLConnection("sqlserver")) {
            System.out.println("Connexion SQL Server réussie !");
        } catch (SQLException e) {
            System.out.println("Erreur de connexion à SQL Server : " + e.getMessage());
        }

        // Test MongoDB connection
        try {
            MongoDatabase mongoDatabase = dbConfigManager.getMongoDatabase("mongodb");
            System.out.println("Connexion MongoDB réussie !");
        } catch (Exception e) {
            System.out.println("Erreur de connexion à MongoDB : " + e.getMessage());
        }
    }
}
