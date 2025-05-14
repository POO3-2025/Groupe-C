package be.helha.projects.GuerreDesRoyaumes.Config;

import com.google.gson.JsonObject;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLConfigManager {
    private static SQLConfigManager instance;
    private final ConfigJsonReader configJsonReader;

    private SQLConfigManager() {
        this.configJsonReader = ConfigJsonReader.getInstance();
    }

    public static synchronized SQLConfigManager getInstance() {
        if (instance == null) {
            instance = new SQLConfigManager();
        }
        return instance;
    }

    public MySqlConfigurations getConfigurations(String dbKey) {
        try {
            JsonObject dbConfig = configJsonReader.getDBConfig().getAsJsonObject("db").getAsJsonObject(dbKey);
            JsonObject creds = dbConfig.getAsJsonObject("BDCredentials");

            return new MySqlConfigurations(
                    dbConfig.get("ConnectionType").getAsString(),
                    dbConfig.get("DBType").getAsString(),
                    new MySqlCredentials(
                            creds.get("HostName").getAsString(),
                            creds.get("Port").getAsString(),
                            creds.get("DBName").getAsString(),
                            creds.get("UserName").getAsString(),
                            creds.get("Password").getAsString()
                    )
            );
        } catch (Exception e) {
            throw new RuntimeException("Erreur SQL config pour " + dbKey, e);
        }
    }

    public Connection getConnection(String dbKey) throws SQLException {
        MySqlConfigurations config = getConfigurations(dbKey);
        MySqlCredentials creds = config.getMySqlCredentials();

        String url = String.format("jdbc:sqlserver://%s:%s;databaseName=%s;encrypt=false",
                creds.DB_HOST, creds.DB_PORT, creds.DB_NAME);
        return DriverManager.getConnection(url, creds.DB_USER, creds.DB_PASSWORD);
    }
}