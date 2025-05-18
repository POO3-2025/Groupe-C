package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * Exception spécifique lancée lors d'erreurs de connexion à une base de données SQL.
 * <p>
 * Cette classe étend {@link DatabaseConnectionException} et représente
 * les problèmes rencontrés lors de l'établissement ou de la gestion
 * d'une connexion SQL dans le projet Guerre des Royaumes.
 * </p>
 */
public class SQLConnectionException extends DatabaseConnectionException {

  /**
   * Constructeur avec un message décrivant l'erreur de connexion SQL.
   *
   * @param message Description de l'erreur survenue lors de la connexion SQL
   */
  public SQLConnectionException(String message) {
    super(message);
  }

  /**
   * Constructeur avec un message et une cause sous-jacente.
   *
   * @param message Description de l'erreur survenue lors de la connexion SQL
   * @param cause   Cause originale de l'exception
   */
  public SQLConnectionException(String message, Throwable cause) {
    super(message, cause);
  }
}
