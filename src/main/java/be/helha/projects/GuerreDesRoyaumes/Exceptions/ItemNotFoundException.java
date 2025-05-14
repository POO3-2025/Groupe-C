package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * Exception lancée lorsqu'un item demandé n'est pas trouvé dans le système.
 */
public class ItemNotFoundException extends ItemException {

    public ItemNotFoundException(String message) {
        super(message);
    }

    public ItemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ItemNotFoundException(int id) {
        super("Item avec l'ID " + id + " n'a pas été trouvé");
    }
}