package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * Exception personnalisée lancée lors d'erreurs survenant dans le cadre des combats,
 * qu'il s'agisse de combats entre joueurs ou contre des PNJ (personnages non joueurs).
 * <p>
 * Cette exception permet de gérer les problèmes spécifiques liés
 * à la mécanique des combats dans le jeu.
 * </p>
 * <p>
 * Hérite de {@link GuerreDesRoyaumesException} afin d'assurer
 * une gestion unifiée des exceptions du projet.
 * </p>
 */
public class CombatException extends GuerreDesRoyaumesException {

    /**
     * Constructeur avec un message décrivant l'erreur de combat.
     *
     * @param message Description de l'erreur
     */
    public CombatException(String message) {
        super(message);
    }

    /**
     * Constructeur avec un message et une cause sous-jacente.
     *
     * @param message Description de l'erreur
     * @param cause   Cause originale de l'exception
     */
    public CombatException(String message, Throwable cause) {
        super(message, cause);
    }
}
