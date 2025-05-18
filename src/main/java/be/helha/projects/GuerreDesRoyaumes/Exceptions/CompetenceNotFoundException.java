package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * Exception lancée lorsqu'une compétence demandée n'est pas trouvée dans le système.
 * <p>
 * Cette exception étend {@link CompetenceException} et est utilisée
 * spécifiquement pour signaler l'absence d'une compétence recherchée,
 * que ce soit par son identifiant ou par un autre critère.
 * </p>
 */
public class CompetenceNotFoundException extends CompetenceException {

    /**
     * Constructeur avec un message d'erreur personnalisé.
     *
     * @param message Message décrivant la raison pour laquelle la compétence n'a pas été trouvée.
     */
    public CompetenceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructeur avec un message d'erreur personnalisé et une cause racine.
     *
     * @param message Message décrivant la raison de l'erreur.
     * @param cause   Cause originale de l'exception.
     */
    public CompetenceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructeur avec l'ID de la compétence non trouvée.
     * Génère un message d'erreur standard indiquant l'absence de la compétence.
     *
     * @param competenceId L'ID de la compétence qui n'a pas été trouvée.
     */
    public CompetenceNotFoundException(int competenceId) {
        super("Compétence avec l'ID " + competenceId + " n'a pas été trouvée");
    }
}
