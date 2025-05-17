package be.helha.projects.GuerreDesRoyaumes.Exceptions;

// SQLConnectionException.java
public class SQLConnectionException extends DatabaseConnectionException {
  public SQLConnectionException(String message) {
    super(message);
  }

  public SQLConnectionException(String message, Throwable cause) {
    super(message, cause);
  }
}