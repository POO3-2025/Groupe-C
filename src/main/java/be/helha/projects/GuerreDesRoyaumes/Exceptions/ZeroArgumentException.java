package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * La classe ZeroArgumentException représente une exception spécifique levée lorsqu'un
 * argument égal à zéro est fourni à une méthode ou une opération qui ne l'accepte pas.
 */
public class ZeroArgumentException extends Exception {

    /**
     * Constructeur par défaut de ZeroArgumentException.
     * Crée une nouvelle exception sans message spécifique.
     */
    public ZeroArgumentException() {
        super();
    }

    /**
     * Constructeur de ZeroArgumentException avec un message personnalisé.
     *
     * @param message Le message décrivant l'erreur ou la cause de l'exception.
     */
    public ZeroArgumentException(String message) {
        super(message);
    }

    /**
     * Constructeur de ZeroArgumentException avec un message personnalisé et une cause sous forme d'exception interne.
     *
     * @param message Le message décrivant l'erreur ou la cause de l'exception.
     * @param cause   L'exception interne qui est à l'origine de cette exception.
     */
    public ZeroArgumentException(String message, Throwable cause) {
        super(message, cause);
    }
}
