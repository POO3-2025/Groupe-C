package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * Exception lancée lors d'erreurs liées aux compétences des personnages.
 */
public class CompetenceException extends GuerreDesRoyaumesException {

    public CompetenceException(String message) {
        super(message);
    }

    public CompetenceException(String message, Throwable cause) {
        super(message, cause);
    }
}