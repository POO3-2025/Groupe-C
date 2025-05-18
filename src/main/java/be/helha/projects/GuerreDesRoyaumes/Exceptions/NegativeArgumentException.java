package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * La classe NegativeArgumentException représente une exception spécifique levée lorsqu'un
 * argument négatif est fourni à une méthode ou une opération qui ne l'accepte pas.
 */
public class NegativeArgumentException extends Exception {

  /**
   * Constructeur par défaut de NegativeArgumentException.
   * Crée une nouvelle exception sans message spécifique.
   */
  public NegativeArgumentException() {
    super();
  }

  /**
   * Constructeur de NegativeArgumentException avec un message personnalisé.
   *
   * @param message Le message décrivant l'erreur ou la cause de l'exception.
   */
  public NegativeArgumentException(String message) {
    super(message);
  }

  /**
   * Constructeur de NegativeArgumentException avec un message personnalisé et une exception interne.
   *
   * @param message Le message décrivant l'erreur ou la cause de l'exception.
   * @param cause   L'exception interne (par exemple, NullPointerException ou SQLException).
   */
  public NegativeArgumentException(String message, Throwable cause) {
    super(message, cause);
  }
}