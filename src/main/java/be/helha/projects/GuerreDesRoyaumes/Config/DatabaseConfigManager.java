package be.helha.projects.GuerreDesRoyaumes.Config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;
import lombok.Getter;


public class DatabaseConfigManager {

    private static DatabaseConfigManager instance;

    @Getter
    private final JsonObject config;
    private MongoClient mongoClient;

    private DatabaseConfigManager() {
        try (InputStreamReader reader = new InputStreamReader(
                Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("db_config.json")),
                StandardCharsets.UTF_8
        ))
        {
            config = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du chargement de config.json", e);
        }
    }

    public static DatabaseConfigManager getInstance() {
        if (instance == null) {
            synchronized (DatabaseConfigManager.class) {
                if (instance == null) {
                    instance = new DatabaseConfigManager();
                }
            }
        }
        return instance;
    }

    public Connection getSQLConnection(String dbKey) throws SQLException {
        try {
            JsonObject db = config.getAsJsonObject("db");
            JsonObject section = db.getAsJsonObject(dbKey);
            JsonObject creds = section.getAsJsonObject("BDCredentials");

            String dbType = creds.get("DBType").getAsString();
            String host = creds.get("HostName").getAsString();
            String port = creds.get("Port").getAsString();
            String dbName = creds.get("DBName").getAsString();
            String user = creds.get("UserName").getAsString();
            String password = creds.get("Password").getAsString();

            String dbUrl = "jdbc:sqlserver://" + host + ":" + port + ";databaseName=" + dbName + ";encrypt=false";

            return DriverManager.getConnection(dbUrl, user, password);

        } catch (Exception e) {
            throw new SQLException("Erreur de connexion à SQL pour la clé " + dbKey, e);
        }
    }

    public MongoDatabase getMongoDatabase(String dbKey) {
        try {
            JsonObject dbConfig = config.getAsJsonObject("db").getAsJsonObject(dbKey).getAsJsonObject("BDCredentials");

            if (mongoClient == null) {
                String uri = "mongodb://" +
                        dbConfig.get("UserName").getAsString() + ":" +
                        dbConfig.get("Password").getAsString() + "@" +
                        dbConfig.get("HostName").getAsString() + ":" +
                        dbConfig.get("Port").getAsString() + "/" +
                        dbConfig.get("DBName").getAsString() + "?authSource=admin";

                mongoClient = MongoClients.create(uri);
            }

            String dbName = dbConfig.get("DBName").getAsString();
            MongoDatabase database = mongoClient.getDatabase(dbName);

            boolean exists = mongoClient.listDatabaseNames().into(new java.util.ArrayList<>()).contains(dbName);
            if (!exists) {
                System.out.println("La base MongoDB " + dbName + " n'existe pas, elle sera créée à la première insertion.");
            }

            return database;

        } catch (Exception e) {
            throw new RuntimeException("Erreur de connexion à MongoDB pour la clé " + dbKey, e);
        }
    }

    public void closeMongoClient() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
        }
    }
}