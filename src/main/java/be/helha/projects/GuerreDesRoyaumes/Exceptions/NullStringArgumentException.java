package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * La classe NullStringArgumentException représente une exception spécifique levée lorsqu'une
 * chaîne de caractères null est fournie comme argument à une méthode qui ne l'accepte pas.
 */
public class NullStringArgumentException extends Exception {

    /**
     * Constructeur par défaut de NullStringArgumentException.
     * Crée une nouvelle exception sans message spécifique.
     */
    public NullStringArgumentException() {
        super();
    }

    /**
     * Constructeur de NullStringArgumentException avec un message personnalisé.
     *
     * @param message Le message décrivant l'erreur ou la cause de l'exception.
     */
    public NullStringArgumentException(String message) {
        super(message);
    }

    /**
     * Constructeur de NullStringArgumentException avec un message personnalisé et une exception interne.
     *
     * @param message Le message décrivant l'erreur ou la cause de l'exception.
     * @param cause   L'exception interne (par exemple, NullPointerException ou SQLException).
     */
    public NullStringArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
