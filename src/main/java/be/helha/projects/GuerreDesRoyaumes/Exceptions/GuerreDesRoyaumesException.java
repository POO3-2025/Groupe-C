package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * Exception de base pour toutes les exceptions spécifiques au projet Guerre Des Royaumes.
 * Fournit une catégorisation et une gestion cohérente des exceptions.
 */
public abstract class GuerreDesRoyaumesException extends RuntimeException {

    public GuerreDesRoyaumesException(String message) {
        super(message);
    }

    public GuerreDesRoyaumesException(String message, Throwable cause) {
        super(message, cause);
    }
}