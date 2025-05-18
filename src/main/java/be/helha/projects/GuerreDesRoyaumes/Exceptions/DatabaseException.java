package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * Exception personnalisée lancée lors d'erreurs liées à l'accès
 * ou à la manipulation de la base de données.
 * <p>
 * Cette exception permet de gérer les problèmes génériques
 * qui peuvent survenir lors des opérations CRUD ou des interactions
 * avec la base de données dans le projet Guerre des Royaumes.
 * </p>
 * <p>
 * Hérite de {@link GuerreDesRoyaumesException} pour une gestion
 * cohérente des exceptions du projet.
 * </p>
 */
public class DatabaseException extends GuerreDesRoyaumesException {

    /**
     * Constructeur avec un message décrivant l'erreur.
     *
     * @param message Description de l'erreur liée à la base de données
     */
    public DatabaseException(String message) {
        super(message);
    }

    /**
     * Constructeur avec un message et une cause sous-jacente.
     *
     * @param message Description de l'erreur liée à la base de données
     * @param cause   Cause originale de l'exception
     */
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
}
