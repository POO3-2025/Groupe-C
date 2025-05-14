package be.helha.projects.GuerreDesRoyaumes.Config;

import com.google.gson.JsonObject;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class MongoDBConfigManager {
    private static MongoDBConfigManager instance;
    private final ConfigJsonReader configJsonReader;
    private MongoClient mongoClient;

    private MongoDBConfigManager() {
        this.configJsonReader = ConfigJsonReader.getInstance();
    }

    public static synchronized MongoDBConfigManager getInstance() {
        if (instance == null) {
            instance = new MongoDBConfigManager();
        }
        return instance;
    }

    public MongoDBConfigurations getConfigurations(String dbKey) {
        try {
            JsonObject dbConfig = configJsonReader.getDBConfig().getAsJsonObject("db").getAsJsonObject(dbKey);
            JsonObject creds = dbConfig.getAsJsonObject("BDCredentials");

            return new MongoDBConfigurations(
                    dbConfig.get("ConnectionType").getAsString(),
                    dbConfig.get("DBType").getAsString(),
                    new MongoDBCredentials(
                            creds.get("HostName").getAsString(),
                            creds.get("Port").getAsString(),
                            creds.get("DBName").getAsString(),
                            creds.get("UserName").getAsString(),
                            creds.get("Password").getAsString()
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Erreur MongoDB config pour " + dbKey, e);
        }
    }

    public MongoDatabase getDatabase(String dbKey) {
        MongoDBConfigurations config = getConfigurations(dbKey);
        MongoDBCredentials creds = config.getMongoDBCredentials();

        try {
            if (mongoClient == null) {
                String uri = String.format("mongodb://%s:%s@%s:%s/%s?authSource=admin",
                        creds.DB_USER, creds.DB_PASSWORD, creds.DB_HOST, creds.DB_PORT, creds.DB_NAME);
                mongoClient = MongoClients.create(uri);
            }

            MongoDatabase database = mongoClient.getDatabase(creds.DB_NAME);
            verifyDatabaseExistence(creds.DB_NAME);
            return database;
        } catch (Exception e) {
            throw new RuntimeException("Erreur connexion MongoDB", e);
        }
    }

    private void verifyDatabaseExistence(String dbName) {
        boolean exists = mongoClient.listDatabaseNames().into(new java.util.ArrayList<>()).contains(dbName);
        if (!exists) {
            System.out.println("Base MongoDB '" + dbName + "' sera créée au premier insert");
        }
    }

    public void closeClient() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
        }
    }
}