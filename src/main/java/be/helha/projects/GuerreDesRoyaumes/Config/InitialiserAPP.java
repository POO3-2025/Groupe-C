package be.helha.projects.GuerreDesRoyaumes.Config;

import be.helha.projects.GuerreDesRoyaumes.Config.ConnexionConfig.ConfigInit;
import be.helha.projects.GuerreDesRoyaumes.Config.ConnexionConfig.ConnexionManager;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.MongoDBConnectionException;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.SQLConnectionException;
import com.mongodb.client.MongoDatabase;
import java.sql.Connection;
import java.sql.SQLException;

public class InitialiserAPP {
    private static boolean isInitialized = false;
    
    // Méthode d'initialisation commune pour éviter la duplication
    private static synchronized void siNonInit() {
        if (!isInitialized) {
            ConfigInit.initAll();
            isInitialized = true;
        }
    }

    public static Connection getSQLConnexion() throws SQLConnectionException {
        try {
            siNonInit();
            Connection SQLDB = ConnexionManager.getInstance().getSQLConnection();

            verifySQLConnection(SQLDB); // Appel de la vérification

            return SQLDB;

        } catch (Exception e) {
            throw new SQLConnectionException("Échec de la connexion SQL: " + e.getMessage(), e);
        }
    }

    public static MongoDatabase getMongoConnexion() throws MongoDBConnectionException {
        try {
            siNonInit();
            MongoDatabase MongoDB = ConnexionManager.getInstance().getMongoDatabase();

            verifyMongoConnection(MongoDB); // Appel de la vérification

            return MongoDB;

        } catch (Exception e) {
            throw new MongoDBConnectionException("Échec de la connexion MongoDB: " + e.getMessage(), e);
        }
    }

    private static void verifySQLConnection(Connection sql) {
        if(sql == null)
            throw new IllegalStateException("Connexion SQL non initialisée");

        try {
            if(sql.isClosed() || !sql.isValid(2)) {
                throw new IllegalStateException("Connexion SQL invalide ou fermée");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erreur de validation SQL", e);
        }
    }

    private static void verifyMongoConnection(MongoDatabase mongo) {
        if(mongo == null)
            throw new IllegalStateException("Connexion MongoDB non initialisée");

        try {
            // Vérification par ping
            mongo.runCommand(new org.bson.Document("ping", 1));
        } catch (Exception e) {
            throw new IllegalStateException("Échec du ping MongoDB", e);
        }
    }
    
    // Pour les tests ou les cas où on veut réinitialiser
    public static void reset() {
        isInitialized = false;
        ConfigInit.reset();
    }
}