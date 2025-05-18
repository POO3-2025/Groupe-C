package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * Exception personnalisée lancée en cas d'erreurs liées
 * aux compétences des personnages dans le jeu.
 * <p>
 * Cette exception facilite la gestion des problèmes
 * rencontrés lors de la manipulation ou de l'exécution
 * des compétences des personnages.
 * </p>
 * <p>
 * Elle hérite de {@link GuerreDesRoyaumesException} pour
 * assurer une gestion cohérente des exceptions spécifiques au projet.
 * </p>
 */
public class CompetenceException extends GuerreDesRoyaumesException {

    /**
     * Constructeur avec un message d'erreur décrivant la nature du problème.
     *
     * @param message Message décrivant l'erreur liée à la compétence
     */
    public CompetenceException(String message) {
        super(message);
    }

    /**
     * Constructeur avec un message d'erreur et une cause racine.
     *
     * @param message Message décrivant l'erreur liée à la compétence
     * @param cause   Cause originale de l'exception
     */
    public CompetenceException(String message, Throwable cause) {
        super(message, cause);
    }
}
