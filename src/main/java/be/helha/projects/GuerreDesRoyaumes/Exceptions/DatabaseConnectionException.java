package be.helha.projects.GuerreDesRoyaumes.Exceptions;

// DatabaseConnectionException.java (classe mère)
public abstract class DatabaseConnectionException extends Exception {
    public DatabaseConnectionException(String message) {
        super(message);
    }

    public DatabaseConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}