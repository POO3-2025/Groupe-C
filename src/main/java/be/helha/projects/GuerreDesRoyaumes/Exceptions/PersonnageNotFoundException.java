package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * Exception lancée lorsqu'un personnage recherché n'est pas trouvé dans le système.
 * <p>
 * Cette exception étend {@link PersonnageException} et signale
 * l'absence d'un personnage identifié par son ID ou une autre clé.
 * </p>
 */
public class PersonnageNotFoundException extends PersonnageException {

    /**
     * Constructeur avec un message d'erreur personnalisé.
     *
     * @param message Message décrivant la raison pour laquelle le personnage n'a pas été trouvé.
     */
    public PersonnageNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructeur avec un message d'erreur et une cause racine.
     *
     * @param message Message décrivant la raison de l'erreur.
     * @param cause   Cause originale de l'exception.
     */
    public PersonnageNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructeur avec l'ID du personnage non trouvé.
     * Génère un message standard indiquant l'absence du personnage.
     *
     * @param id ID du personnage recherché qui n'a pas été trouvé.
     */
    public PersonnageNotFoundException(int id) {
        super("Personnage avec l'ID " + id + " n'a pas été trouvé");
    }
}
