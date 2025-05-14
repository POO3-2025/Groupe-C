package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * Exception lancée lorsqu'un royaume demandé n'est pas trouvé dans le système.
 */
public class RoyaumeNotFoundException extends RoyaumeException {

    public RoyaumeNotFoundException(String message) {
        super(message);
    }

    public RoyaumeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public RoyaumeNotFoundException(int id) {
        super("Royaume avec l'ID " + id + " n'a pas été trouvé");
    }
}