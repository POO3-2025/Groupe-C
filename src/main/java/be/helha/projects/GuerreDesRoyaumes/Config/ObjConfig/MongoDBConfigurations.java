package be.helha.projects.GuerreDesRoyaumes.Config.ObjConfig;

/**
 * La classe Configuration représente la configuration d'une connexion à une base de données,
 * avec le type de connexion, le type de base de données et les informations d'identification.
 */
public class MongoDBConfigurations {

    /** Type de connexion (ex. DB, Serveur, API, etc) */
    public String ConnectionType;

    /** Type de base de données (ex. MySQL, SQLServer, etc) */
    public String DBType;

    /** Informations d'identification pour accéder à la base de données */
    public MongoDBCredentials MongoBDCredentials;

    /**
     * Constructeur de la classe MongoDBConfigurations.
     *
     * @param connectionType Type de connexion (ex. DB, Serveur, API, etc)
     * @param dbType         Type de base de données (ex. MySQL, SQLServer, etc)
     * @param mongoBDCredentials Informations d'identification pour accéder à la base de données
     */
    public MongoDBConfigurations(String connectionType, String dbType, MongoDBCredentials mongoBDCredentials) {
        this.ConnectionType = connectionType;
        this.DBType = dbType;
        this.MongoBDCredentials = mongoBDCredentials;
    }

    /** Méthode toString pour en savoir plus sur l'instance */
    @Override
    public String toString() {
        return "MongoDBConfigurations{" +
                "ConnectionType='" + ConnectionType + '\'' +
                ", DBType='" + DBType + '\'' +
                ", MongoBDCredentials=" + MongoBDCredentials +
                '}';
    }

    /** Getters pour les credentials
     *       @return MongoDBCredentials
     */
    public MongoDBCredentials getMongoDBCredentials() {
        return MongoBDCredentials;
    }
}
