package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * Exception lancée lorsqu'un joueur demandé n'est pas trouvé dans le système.
 */
public class JoueurNotFoundException extends GuerreDesRoyaumesException {

    public JoueurNotFoundException(String message) {
        super(message);
    }

    public JoueurNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public JoueurNotFoundException(int id) {
        super("Joueur avec l'ID " + id + " n'a pas été trouvé");
    }
}
