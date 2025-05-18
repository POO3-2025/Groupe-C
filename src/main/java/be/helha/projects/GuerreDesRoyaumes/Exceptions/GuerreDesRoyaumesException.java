package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * Exception de base abstraite pour toutes les exceptions personnalisées
 * spécifiques au projet Guerre des Royaumes.
 * <p>
 * Cette classe permet une gestion centralisée et cohérente
 * des exceptions métier dans l'application en héritant de {@link RuntimeException}.
 * </p>
 */
public abstract class GuerreDesRoyaumesException extends RuntimeException {

    /**
     * Constructeur avec un message décrivant l'exception.
     *
     * @param message Message expliquant la cause de l'exception
     */
    public GuerreDesRoyaumesException(String message) {
        super(message);
    }

    /**
     * Constructeur avec un message et une cause sous-jacente.
     *
     * @param message Message expliquant la cause de l'exception
     * @param cause   Cause originale de l'exception
     */
    public GuerreDesRoyaumesException(String message, Throwable cause) {
        super(message, cause);
    }
}
