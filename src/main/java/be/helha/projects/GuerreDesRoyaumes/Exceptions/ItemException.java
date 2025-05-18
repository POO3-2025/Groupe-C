package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * Exception personnalisée lancée lors d'erreurs liées aux items du jeu.
 * <p>
 * Cette exception permet de gérer les problèmes spécifiques
 * rencontrés lors des opérations sur les items (création, modification,
 * suppression, validation) dans le projet Guerre des Royaumes.
 * </p>
 * <p>
 * Hérite de {@link GuerreDesRoyaumesException} pour assurer une gestion cohérente
 * des exceptions spécifiques au projet.
 * </p>
 */
public class ItemException extends GuerreDesRoyaumesException {

    /**
     * Constructeur avec un message d'erreur décrivant la nature du problème.
     *
     * @param message Message expliquant l'erreur liée à l'item
     */
    public ItemException(String message) {
        super(message);
    }

    /**
     * Constructeur avec un message d'erreur et une cause racine.
     *
     * @param message Message expliquant l'erreur liée à l'item
     * @param cause   Cause originale de l'exception
     */
    public ItemException(String message, Throwable cause) {
        super(message, cause);
    }
}
