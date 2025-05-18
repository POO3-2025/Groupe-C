package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * Exception personnalisée lancée lorsqu'une erreur survient
 * lors du processus d'authentification d'un joueur.
 * <p>
 * Hérite de {@link GuerreDesRoyaumesException} pour permettre
 * une gestion centralisée des exceptions spécifiques au projet.
 * </p>
 */
public class AuthentificationException extends GuerreDesRoyaumesException {

    /**
     * Constructeur avec un message d'erreur.
     *
     * @param message Message décrivant l'erreur d'authentification
     */
    public AuthentificationException(String message) {
        super(message);
    }

    /**
     * Constructeur avec un message d'erreur et une cause sous-jacente.
     *
     * @param message Message décrivant l'erreur d'authentification
     * @param cause   Cause originale de l'exception
     */
    public AuthentificationException(String message, Throwable cause) {
        super(message, cause);
    }
}
