package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * Exception lancée lorsqu'un item demandé n'est pas trouvé dans le système.
 * <p>
 * Cette exception étend {@link ItemException} et est utilisée
 * pour signaler l'absence d'un item recherché, identifié par son ID ou
 * par une autre clé.
 * </p>
 */
public class ItemNotFoundException extends ItemException {

    /**
     * Constructeur avec un message d'erreur personnalisé.
     *
     * @param message Message décrivant la raison pour laquelle l'item n'a pas été trouvé.
     */
    public ItemNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructeur avec un message d'erreur personnalisé et une cause racine.
     *
     * @param message Message décrivant la raison de l'erreur.
     * @param cause   Cause originale de l'exception.
     */
    public ItemNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructeur avec l'ID de l'item non trouvé.
     * Génère un message standard indiquant l'absence de l'item.
     *
     * @param id ID de l'item recherché qui n'a pas été trouvé.
     */
    public ItemNotFoundException(int id) {
        super("Item avec l'ID " + id + " n'a pas été trouvé");
    }
}
