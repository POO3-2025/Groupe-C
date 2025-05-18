package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * Exception personnalisée lancée lors d'erreurs liées aux royaumes dans le jeu.
 * <p>
 * Cette exception permet de gérer les problèmes spécifiques
 * rencontrés lors des opérations ou interactions relatives aux royaumes.
 * </p>
 * <p>
 * Hérite de {@link GuerreDesRoyaumesException} pour assurer une gestion cohérente
 * des exceptions spécifiques au projet.
 * </p>
 */
public class RoyaumeException extends GuerreDesRoyaumesException {

    /**
     * Constructeur avec un message d'erreur décrivant la nature du problème.
     *
     * @param message Message expliquant l'erreur liée au royaume
     */
    public RoyaumeException(String message) {
        super(message);
    }

    /**
     * Constructeur avec un message d'erreur et une cause racine.
     *
     * @param message Message expliquant l'erreur liée au royaume
     * @param cause   Cause originale de l'exception
     */
    public RoyaumeException(String message, Throwable cause) {
        super(message, cause);
    }
}
