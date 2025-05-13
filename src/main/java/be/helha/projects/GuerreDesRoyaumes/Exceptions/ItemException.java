package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * Exception lancée lors d'erreurs liées aux items du jeu.
 */
public class ItemException extends GuerreDesRoyaumesException {

    public ItemException(String message) {
        super(message);
    }

    public ItemException(String message, Throwable cause) {
        super(message, cause);
    }
}