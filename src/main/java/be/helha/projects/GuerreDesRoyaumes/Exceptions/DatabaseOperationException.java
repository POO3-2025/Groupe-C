package be.helha.projects.GuerreDesRoyaumes.Exceptions;

public class DatabaseOperationException extends RuntimeException {
    /**
     * Constructeur par défaut de DatabaseOperationException.
     * Crée une nouvelle exception sans message spécifique.
     */
    public DatabaseOperationException() {
        super();
    }

    /**
     * Constructeur de DatabaseOperationException avec un message personnalisé.
     *
     * @param message Le message décrivant l'erreur ou la cause de l'exception.
     */
    public DatabaseOperationException(String message) {
        super(message);
    }

    /**
     * Constructeur de DatabaseOperationException avec un message personnalisé et une exception interne.
     *
     * @param message Le message décrivant l'erreur ou la cause de l'exception.
     * @param cause   L'exception interne (par exemple, NegativeArgumentException).
     */
    public DatabaseOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
