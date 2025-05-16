package be.helha.projects.GuerreDesRoyaumes.Config.ConnexionConfig;

import be.helha.projects.GuerreDesRoyaumes.Config.ObjConfig.MongoDBConfigurations;
import be.helha.projects.GuerreDesRoyaumes.Config.ObjConfig.MongoDBCredentials;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
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

    // Template MongoDB injecté par Spring
    private MongoTemplate mongoTemplate;

    // Client MongoDB pour la connexion manuelle
    private MongoClient mongoClient;

    // Indicateur du mode de fonctionnement
    private boolean useSpringTemplate;

    @Autowired(required = false)
    public MongoDBConfigManager(
            @Value("${spring.data.mongodb.host:}") String host,
            @Value("${spring.data.mongodb.port:}") String port,
            @Value("${spring.data.mongodb.database:}") String dbName,
            @Value("${spring.data.mongodb.username:}") String username,
            @Value("${spring.data.mongodb.password:}") String password,
            MongoTemplate mongoTemplate) {

        this.host = host;
        this.port = port;
        this.dbName = dbName;
        this.username = username;
        this.password = password;
        this.mongoTemplate = mongoTemplate;
        this.useSpringTemplate = (mongoTemplate != null);

        // Singleton update
        instance = this;
        System.out.println("MongoDBConfigManager initialisé via Spring");
    }

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
        this.useSpringTemplate = false;

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
     * Initialise l'instance avec une URI MongoDB complète
     */
    public static void initializeWithUri(String uri) {
        if (uri == null || uri.isEmpty()) {
            throw new IllegalArgumentException("L'URI MongoDB ne peut pas être nulle ou vide");
        }
        MongoDBConfigManager manager = new MongoDBConfigManager();
        manager.useSpringTemplate = false;
        manager.mongoClient = MongoClients.create(uri);
        // Extraction du nom de la base de données depuis l'URI
        String dbName = null;
        try {
            String[] parts = uri.split("/");
            if (parts.length > 3) {
                dbName = parts[3].split("\\?")[0];
            }
        } catch (Exception e) {
            System.err.println("Impossible d'extraire le nom de la base de données de l'URI: " + e.getMessage());
        }
        manager.dbName = dbName;
        instance = manager;
        System.out.println("MongoDBConfigManager initialisé avec URI: " + uri);
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
        if (useSpringTemplate) {
            try {
                // On utilise directement le template MongoDB
                MongoDatabase database = mongoTemplate.getDb();
                System.out.println("MongoDB obtenu via template Spring: " + database.getName());
                return database;
            } catch (Exception e) {
                System.err.println("Erreur lors de l'accès à MongoDB via template Spring: " + e.getMessage());
                e.printStackTrace();
                // Fallback au mode manuel si Spring échoue
            }
        }

        // Cas d'initialisation par URI : mongoClient et dbName sont présents
        if (mongoClient != null && dbName != null) {
            return mongoClient.getDatabase(dbName);
        }

        // Connexion directe en cas d'absence de Spring ou d'échec du template
        if (host == null || port == null || dbName == null) {
            throw new IllegalStateException("Paramètres de connexion MongoDB non initialisés");
        }

        try {
            System.out.println("Tentative de connexion directe à MongoDB...");
            int portNumber;
            try {
                portNumber = Integer.parseInt(port.trim());
            } catch (NumberFormatException e) {
                throw new IllegalStateException("Port MongoDB invalide: " + port);
            }

            String uri = String.format("mongodb://%s:%d/%s", host, portNumber, dbName);
            System.out.println("URI de connexion: " + uri);

            if (mongoClient == null) {
                mongoClient = MongoClients.create(uri);
            }
            return mongoClient.getDatabase(dbName);
        } catch (Exception e) {
            System.err.println("Erreur lors de la connexion directe à MongoDB: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Impossible de se connecter à MongoDB", e);
        }
    }

    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
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
        if (!useSpringTemplate && mongoClient != null) {
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