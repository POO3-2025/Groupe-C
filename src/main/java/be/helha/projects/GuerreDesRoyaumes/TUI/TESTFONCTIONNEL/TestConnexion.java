package be.helha.projects.GuerreDesRoyaumes.TUI;

import be.helha.projects.GuerreDesRoyaumes.Config.InitialiserAPP;
import be.helha.projects.GuerreDesRoyaumes.Config.ConnexionConfig.ConnexionManager;
import com.mongodb.client.MongoDatabase;
import java.sql.Connection;

public class TestConnexion {
    public static void main(String[] args) {
        try {
            // Obtenir les connexions via InitialiserAPP
            Connection sqlConnection = InitialiserAPP.getSQLConnexion();
            MongoDatabase mongoDB = InitialiserAPP.getMongoConnexion();

            ConnexionManager manager = ConnexionManager.getInstance();

            // Test SQL avec la connexion déjà établie
            testSQL(sqlConnection);

            // Test MongoDB avec la connexion déjà établie
            testMongoDB(mongoDB);

        } catch (Exception e) {
            System.err.println("Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testSQL(Connection conn) {
        System.out.println("===== TEST SQL =====");
        try {
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

    private static void testMongoDB(MongoDatabase db) {
        System.out.println("\n===== TEST MongoDB =====");
        try {
            System.out.println(db != null ? "Connexion MongoDB réussie!" : "Échec connexion MongoDB");
            if (db != null) {
                System.out.println("Nom de la base de données: " + db.getName());
                System.out.println("Collections disponibles:");
                db.listCollectionNames().forEach(name -> System.out.println("- " + name));
            }
        } catch (Exception e) {
            System.err.println("Erreur MongoDB: " + e.getMessage());
        }
    }
}