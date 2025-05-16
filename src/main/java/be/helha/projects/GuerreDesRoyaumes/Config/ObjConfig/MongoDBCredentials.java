package be.helha.projects.GuerreDesRoyaumes.Config.ObjConfig;

/**
 * La classe Credentials contient les informations d'identification nécessaires pour accéder à une base de données,
 * y compris le nom d'hôte, le nom d'utilisateur, le mot de passe, le nom de la base de données et le port.
 */
public class MongoDBCredentials {

    /**
     * Nom d'hôte ou adresse IP du serveur de base de données.
     */
    public String DB_HOST;

    /**
     *  Numéro de port utilisé pour la connexion à la base de données.
     */
    public String DB_PORT;

    /**
     * Nom de la base de données cible.
     */
    public String DB_NAME;
    /**
     * Nom d'utilisateur pour se connecter à la base de données.
     */
    public String DB_USER;

    /**
     * Mot de passe associé à l'utilisateur pour la base de données.
     */
    public String DB_PASSWORD;

    public MongoDBCredentials(String DB_HOST, String DB_PORT, String DB_NAME, String DB_USER, String DB_PASSWORD) {
        this.DB_HOST = DB_HOST;
        this.DB_PORT = DB_PORT;
        this.DB_NAME = DB_NAME;
        this.DB_USER = DB_USER;
        this.DB_PASSWORD = DB_PASSWORD;
    }
}
