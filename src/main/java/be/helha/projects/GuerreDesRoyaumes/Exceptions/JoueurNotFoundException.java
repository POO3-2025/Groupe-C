package be.helha.projects.GuerreDesRoyaumes.Exceptions;

public class JoueurNotFoundException extends RuntimeException {

    public JoueurNotFoundException(String message) {
        super(message);
    }

    public JoueurNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
