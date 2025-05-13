package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * Exception lancée lors d'erreurs liées aux royaumes.
 */
public class RoyaumeException extends GuerreDesRoyaumesException {

    public RoyaumeException(String message) {
        super(message);
    }

    public RoyaumeException(String message, Throwable cause) {
        super(message, cause);
    }
}