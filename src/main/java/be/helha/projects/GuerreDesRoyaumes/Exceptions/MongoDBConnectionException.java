package be.helha.projects.GuerreDesRoyaumes.Exceptions;

// MongoDBConnectionException.java
public class MongoDBConnectionException extends DatabaseConnectionException {
    public MongoDBConnectionException(String message) {
        super(message);
    }

    public MongoDBConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}