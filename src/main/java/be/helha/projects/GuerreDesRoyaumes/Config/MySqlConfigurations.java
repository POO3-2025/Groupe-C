package be.helha.projects.GuerreDesRoyaumes.Config;

/**
 * La classe MySqlConfigurations représente la configuration d'une connexion à une base de données MySQL,
 * avec l'hôte, le port, la base de données, et les informations d'identification (utilisateur et mot de passe).
 */
public class MySqlConfigurations {

    /** Type de connexion (ex. DB, Serveur, API, etc) */
    public String ConnectionType;

    /** Type de base de données (ex. MySQL, SQLServer, etc) */
    public String DBType;

    /** Informations d'identification pour accéder à la base de données */
    public MySqlCredentials MySqlCredentials;

    /**
     * Constructeur de la classe MySqlConfigurations.
     *
     * @param connectionType Type de connexion (ex. DB, Serveur, API, etc)
     * @param dbType         Type de base de données (ex. MySQL, SQLServer, etc)
     * @param mySqlCredentials Informations d'identification pour accéder à la base de données
     */
    public MySqlConfigurations(String connectionType, String dbType, MySqlCredentials mySqlCredentials) {
        this.ConnectionType = connectionType;
        this.DBType = dbType;
        this.MySqlCredentials = mySqlCredentials;
    }

    /** Méthode toString pour en savoir plus sur l'instance */
    @Override
    public String toString() {
        return "MySqlConfigurations{" +
                "ConnectionType='" + ConnectionType + '\'' +
                ", DBType='" + DBType + '\'' +
                ", MySqlCredentials=" + MySqlCredentials +
                '}';
    }

    /** Getters pour les credentials
     *       @return MySqlCredentials
     */
    public MySqlCredentials getMySqlCredentials() {
        return MySqlCredentials;
    }
}
