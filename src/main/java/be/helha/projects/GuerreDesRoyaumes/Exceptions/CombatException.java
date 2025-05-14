package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * Exception lancée lors d'erreurs liées aux combats entre joueurs ou avec des PNJ.
 */
public class CombatException extends GuerreDesRoyaumesException {

    public CombatException(String message) {
        super(message);
    }

    public CombatException(String message, Throwable cause) {
        super(message, cause);
    }
}