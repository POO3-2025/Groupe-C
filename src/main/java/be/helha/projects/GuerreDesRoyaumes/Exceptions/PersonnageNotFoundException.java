package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * Exception lancée lorsqu'un personnage demandé n'est pas trouvé dans le système.
 */
public class PersonnageNotFoundException extends PersonnageException {

    public PersonnageNotFoundException(String message) {
        super(message);
    }

    public PersonnageNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public PersonnageNotFoundException(int id) {
        super("Personnage avec l'ID " + id + " n'a pas été trouvé");
    }
}