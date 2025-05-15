package be.helha.projects.GuerreDesRoyaumes.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

@Component
public class SQLConfigManager {
    private static SQLConfigManager instance;

    // Configurations pour la connexion
    private String jdbcUrl;
    private String username;
    private String password;
    private String driverClassName;

    // Constructeur privé - pas d'initialisation par défaut
    private SQLConfigManager() {
        // Pas de valeurs par défaut
    }

    // Constructeur avec paramètres obligatoires
    public SQLConfigManager(String jdbcUrl, String username, String password, String driverClassName) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        this.driverClassName = driverClassName;

        // Singleton update
        instance = this;
        System.out.println("SQLConfigManager initialisé avec URL: " + jdbcUrl);
    }

    public static synchronized SQLConfigManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("SQLConfigManager n'a pas été initialisé. Utilisez d'abord le constructeur avec paramètres.");
        }
        return instance;
    }

    /**
     * Initialise l'instance avec les paramètres de connexion spécifiés
     */
    public static void initialize(String jdbcUrl, String username, String password, String driverClassName) {
        instance = new SQLConfigManager(jdbcUrl, username, password, driverClassName);
    }

    /**
     * Méthode maintenue pour compatibilité avec le code existant
     */
    public MySqlConfigurations getConfigurations(String dbKey) {
        String connectionType = "DB";
        String dbType = "SQLServer";

        MySqlCredentials credentials = new MySqlCredentials(
                getHostFromUrl(jdbcUrl),
                getPortFromUrl(jdbcUrl),
                getDatabaseNameFromUrl(jdbcUrl),
                username,
                password
        );

        return new MySqlConfigurations(connectionType, dbType, credentials);
    }

    private String getHostFromUrl(String url) {
        if (url != null && url.contains("//")) {
            String hostPart = url.split("//")[1].split(":")[0];
            return hostPart;
        }
        return "";
    }

    private String getPortFromUrl(String url) {
        if (url != null && url.contains(":") && url.split("//").length > 1) {
            String[] parts = url.split("//")[1].split(":");
            if (parts.length > 1) {
                return parts[1].split(";")[0];
            }
        }
        return "";
    }

    private String getDatabaseNameFromUrl(String url) {
        if (url != null && url.contains("databaseName=")) {
            String[] parts = url.split("databaseName=");
            if (parts.length > 1) {
                return parts[1].split(";")[0];
            }
        }
        return "";
    }

    public Connection getConnection() throws SQLException {
        if (jdbcUrl == null || username == null || password == null) {
            throw new SQLException("Paramètres de connexion non initialisés");
        }

        try {
            // Charger le driver si nécessaire
            if (driverClassName != null && !driverClassName.isEmpty()) {
                try {
                    Class.forName(driverClassName);
                } catch (ClassNotFoundException e) {
                    System.err.println("Driver SQL Server non trouvé: " + e.getMessage());
                }
            }

            // Utiliser DriverManager pour établir une connexion directe
            Connection conn = DriverManager.getConnection(jdbcUrl, username, password);
            System.out.println("Connexion à SQL Server réussie via DriverManager!");
            return conn;
        } catch (SQLException e) {
            System.err.println("Erreur de connexion SQL Server: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }



    /**
     * Méthode maintenue pour compatibilité avec le code existant
     * Retourne un DataSource pour les DAO qui en ont besoin
     */
    public DataSource getDataSource(String dbKey) {
        return createDataSource();
    }

    /**
     * Crée un DataSource basé sur les paramètres de connexion actuels
     */
    private DataSource createDataSource() {
        if (jdbcUrl == null || username == null || password == null || driverClassName == null) {
            throw new IllegalStateException("Paramètres de connexion non initialisés");
        }

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);
        System.out.println("DataSource créé avec URL: " + jdbcUrl);
        return dataSource;
    }

    /**
     * Configure les paramètres de connexion
     */
    public void setConnectionParams(String jdbcUrl, String username, String password, String driverClassName) {
        this.jdbcUrl = jdbcUrl;
        this.username = username;
        this.password = password;
        this.driverClassName = driverClassName;
    }

    /**
     * Retourne l'URL JDBC actuelle
     */
    public String getJdbcUrl() {
        return jdbcUrl;
    }

    /**
     * Retourne le nom d'utilisateur actuel
     */
    public String getUsername() {
        return username;
    }
}