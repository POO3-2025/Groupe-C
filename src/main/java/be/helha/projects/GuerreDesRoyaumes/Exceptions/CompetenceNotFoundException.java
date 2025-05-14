package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * Exception lancée lorsqu'une compétence demandée n'est pas trouvée dans le système.
 */
public class CompetenceNotFoundException extends CompetenceException {

    public CompetenceNotFoundException(String message) {
        super(message);
    }

    public CompetenceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructeur spécifique pour les cas où la compétence n'est pas trouvée par son ID
     * @param competenceId L'ID de la compétence non trouvée
     */
    public CompetenceNotFoundException(int competenceId) {
        super("Compétence avec l'ID " + competenceId + " n'a pas été trouvée");
    }
}