package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * Exception lancée lors d'erreurs liées aux coffres des joueurs.
 */
public class CoffreException extends GuerreDesRoyaumesException {

    public CoffreException(String message) {
        super(message);
    }

    public CoffreException(String message, Throwable cause) {
        super(message, cause);
    }
}