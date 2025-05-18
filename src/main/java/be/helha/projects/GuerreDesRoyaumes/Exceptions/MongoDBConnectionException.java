package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * Exception spécifique lancée lors d'erreurs de connexion à une base de données MongoDB.
 * <p>
 * Cette classe hérite de {@link DatabaseConnectionException} et représente
 * les problèmes survenant lors de l'établissement ou de la gestion
 * d'une connexion MongoDB dans le projet Guerre des Royaumes.
 * </p>
 */
public class MongoDBConnectionException extends DatabaseConnectionException {

    /**
     * Constructeur avec un message décrivant l'erreur de connexion MongoDB.
     *
     * @param message Description de l'erreur lors de la connexion à MongoDB
     */
    public MongoDBConnectionException(String message) {
        super(message);
    }

    /**
     * Constructeur avec un message et une cause sous-jacente.
     *
     * @param message Description de l'erreur lors de la connexion à MongoDB
     * @param cause   Cause originale de l'exception
     */
    public MongoDBConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
