package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * Exception personnalisée lancée lors d'erreurs liées aux personnages dans le jeu.
 * <p>
 * Cette exception permet de gérer les problèmes spécifiques
 * rencontrés lors des opérations ou interactions concernant les personnages.
 * </p>
 * <p>
 * Hérite de {@link GuerreDesRoyaumesException} pour assurer une gestion cohérente
 * des exceptions propres au projet.
 * </p>
 */
public class PersonnageException extends GuerreDesRoyaumesException {

    /**
     * Constructeur avec un message d'erreur décrivant la nature du problème.
     *
     * @param message Message expliquant l'erreur liée au personnage
     */
    public PersonnageException(String message) {
        super(message);
    }

    /**
     * Constructeur avec un message d'erreur et une cause racine.
     *
     * @param message Message expliquant l'erreur liée au personnage
     * @param cause   Cause originale de l'exception
     */
    public PersonnageException(String message, Throwable cause) {
        super(message, cause);
    }
}
