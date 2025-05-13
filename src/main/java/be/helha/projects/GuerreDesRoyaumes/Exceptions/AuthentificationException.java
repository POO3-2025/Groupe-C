package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * Exception lancée lors d'erreurs liées à l'authentification des joueurs.
 */
public class AuthentificationException extends GuerreDesRoyaumesException {

    public AuthentificationException(String message) {
        super(message);
    }

    public AuthentificationException(String message, Throwable cause) {
        super(message, cause);
    }
}