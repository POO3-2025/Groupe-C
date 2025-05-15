package be.helha.projects.GuerreDesRoyaumes.Config;

import com.mongodb.client.MongoDatabase;
import java.sql.Connection;
import java.sql.SQLException;

public class ConnexionManager {
    private static ConnexionManager instance;

    private ConnexionManager() {
        // Constructeur privé pour le singleton
    }

    public static synchronized ConnexionManager getInstance() {
        if (instance == null) {
            instance = new ConnexionManager();
        }
        return instance;
    }

    /**
     * Obtient une connexion SQL à partir de SQLConfigManager
     */
    public Connection getSQLConnection() throws SQLException {
        return SQLConfigManager.getInstance().getConnection();
    }

    /**
     * Obtient une base de données MongoDB à partir de MongoDBConfigManager
     */
    public MongoDatabase getMongoDatabase() {
        return MongoDBConfigManager.getInstance().getDatabase();
    }

    /**
     * Méthode maintenue pour compatibilité avec le code existant
     * @param dbKey clé identifiant la base de données (sqlserver ou mongodb)
     * @return une connexion SQL pour SQL Server uniquement
     * @throws SQLException si la clé est invalide ou si le type de base de données n'est pas compatible
     * @apiNote Cette méthode ne retourne que des connexions SQL. Pour MongoDB, utiliser getMongoDatabase()
     */
    public Connection getConnection(String dbKey) throws SQLException {
        if (dbKey == null || dbKey.isEmpty()) {
            throw new SQLException("Database key is null or empty");
        }

        if (dbKey.equals("sqlserver")) {
            return getSQLConnection();
        } else if (dbKey.equals("mongodb")) {
            throw new SQLException("MongoDB ne peut pas retourner une connexion SQL. Utilisez getMongoDatabase() à la place.");
        } else {
            throw new SQLException("Clé de base de données inconnue: " + dbKey);
        }
    }

    /**
     * Récupère une connexion spécifique en fonction du type de base de données
     * @param dbKey clé identifiant la base de données (sqlserver ou mongodb)
     * @return Object qui peut être soit une Connection SQL soit une MongoDatabase
     * @throws SQLException si la clé est invalide ou si une erreur de connexion se produit
     */
    public Object getDbConnection(String dbKey) throws SQLException {
        if (dbKey == null || dbKey.isEmpty()) {
            throw new SQLException("Database key is null or empty");
        }

        if (dbKey.equals("sqlserver")) {
            return getSQLConnection();
        } else if (dbKey.equals("mongodb")) {
            return getMongoDatabase();
        } else {
            throw new SQLException("Clé de base de données inconnue: " + dbKey);
        }
    }

    /**
     * Ferme les ressources de connexion
     */
    public void closeConnections() {
        try {
            MongoDBConfigManager.getInstance().closeClient();
        } catch (Exception e) {
            System.err.println("Erreur lors de la fermeture du client MongoDB: " + e.getMessage());
        }
    }
}
