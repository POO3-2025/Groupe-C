package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * Classe abstraite représentant une exception liée aux erreurs de connexion
 * à une base de données.
 * <p>
 * Cette classe sert de classe mère pour toutes les exceptions
 * spécifiques de connexion aux bases de données dans le projet.
 * </p>
 */
public abstract class DatabaseConnectionException extends Exception {

    /**
     * Constructeur avec un message d'erreur décrivant la cause de l'exception.
     *
     * @param message Message expliquant l'erreur de connexion
     */
    public DatabaseConnectionException(String message) {
        super(message);
    }

    /**
     * Constructeur avec un message d'erreur et une cause sous-jacente.
     *
     * @param message Message expliquant l'erreur de connexion
     * @param cause   Cause originale de l'exception
     */
    public DatabaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
