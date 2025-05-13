package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * Exception lancée lors d'erreurs liées aux personnages.
 */
public class PersonnageException extends GuerreDesRoyaumesException {

    public PersonnageException(String message) {
        super(message);
    }

    public PersonnageException(String message, Throwable cause) {
        super(message, cause);
    }
}