package be.helha.projects.GuerreDesRoyaumes.Config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.springframework.stereotype.Component;

@Component
public class MongoDBConfigManager {
    private static MongoDBConfigManager instance;

    // Propriétés pour la connexion
    private String host;
    private String port;
    private String dbName;
    private String username;
    private String password;

    // Client MongoDB pour la connexion manuelle
    private MongoClient mongoClient;

    // Indique si nous utilisons Spring ou non
    private boolean useSpring;

    // Constructeur privé - pas d'initialisation par défaut
    private MongoDBConfigManager() {
        // Pas de valeurs par défaut
    }

    // Constructeur pour initialisation manuelle (sans Spring)
    public MongoDBConfigManager(String host, String port, String dbName, String username, String password) {
        this.host = host;
        this.port = port;
        this.dbName = dbName;
        this.username = username;
        this.password = password;
        this.useSpring = false;

        // Singleton update
        instance = this;
        System.out.println("MongoDBConfigManager initialisé manuellement");
    }

    public static synchronized MongoDBConfigManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("MongoDBConfigManager n'a pas été initialisé. Utilisez d'abord le constructeur avec paramètres.");
        }
        return instance;
    }

    /**
     * Initialise l'instance avec les paramètres de connexion spécifiés
     */
    public static void initialize(String host, String port, String dbName, String username, String password) {
        instance = new MongoDBConfigManager(host, port, dbName, username, password);
    }

    /**
     * Méthode maintenue pour compatibilité avec le code existant
     */
    public MongoDBConfigurations getConfigurations(String dbKey) {
        String connectionType = "DB";
        String dbType = "MongoDB";

        MongoDBCredentials credentials = new MongoDBCredentials(
                host,
                port,
                dbName,
                username,
                password
        );

        return new MongoDBConfigurations(connectionType, dbType, credentials);
    }

    /**
     * Méthode maintenue pour compatibilité avec le code existant
     */
    public MongoDatabase getDatabase(String dbKey) {
        return getDatabase();
    }

    public MongoDatabase getDatabase() {
        // Connexion directe à MongoDB
        if (host == null || port == null || dbName == null) {
            throw new IllegalStateException("Paramètres de connexion MongoDB non initialisés");
        }

        try {
            System.out.println("Tentative de connexion directe à MongoDB...");

            // Utiliser une connexion simple avec connectionString pour éviter les problèmes de dépendances
            String connectionString = "mongodb://" + host + ":" + port;
            System.out.println("URI de connexion: " + connectionString);

            if (mongoClient == null) {
                // Utiliser la surcharge plus simple qui ne dépend pas de StreamFactory
                mongoClient = MongoClients.create(connectionString);
            }
            return mongoClient.getDatabase(dbName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la connexion directe à MongoDB: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Impossible de se connecter à MongoDB", e);
        }
    }

    /**
     * Configure les paramètres de connexion
     */
    public void setConnectionParams(String host, String port, String dbName, String username, String password) {
        this.host = host;
        this.port = port;
        this.dbName = dbName;
        this.username = username;
        this.password = password;
    }

    /**
     * Ferme les ressources MongoDB si nécessaire.
     */
    public void closeClient() {
        if (mongoClient != null) {
            try {
                mongoClient.close();
                System.out.println("Client MongoDB fermé avec succès");
                mongoClient = null;
            } catch (Exception e) {
                System.err.println("Erreur lors de la fermeture du client MongoDB: " + e.getMessage());
            }
        } else {
            System.out.println("Méthode closeClient() appelée - Les ressources MongoDB sont gérées par Spring ou aucun client n'est ouvert");
        }
    }
}