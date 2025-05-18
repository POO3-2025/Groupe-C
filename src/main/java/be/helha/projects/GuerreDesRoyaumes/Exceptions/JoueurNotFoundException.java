package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * Exception lancée lorsqu'un joueur recherché n'est pas trouvé dans le système.
 * <p>
 * Cette exception étend {@link GuerreDesRoyaumesException} et sert
 * à signaler l'absence d'un joueur identifié par son ID ou une autre clé.
 * </p>
 */
public class JoueurNotFoundException extends GuerreDesRoyaumesException {

    /**
     * Constructeur avec un message d'erreur personnalisé.
     *
     * @param message Message décrivant la raison pour laquelle le joueur n'a pas été trouvé.
     */
    public JoueurNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructeur avec un message d'erreur et une cause racine.
     *
     * @param message Message décrivant la raison de l'erreur.
     * @param cause   Cause originale de l'exception.
     */
    public JoueurNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructeur avec l'ID du joueur non trouvé.
     * Génère un message standard indiquant l'absence du joueur.
     *
     * @param id ID du joueur recherché qui n'a pas été trouvé.
     */
    public JoueurNotFoundException(int id) {
        super("Joueur avec l'ID " + id + " n'a pas été trouvé");
    }
}
