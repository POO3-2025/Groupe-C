package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * Exception lancée lors d'erreurs liées à la sécurité de l'application,
 * comme les accès non autorisés ou les problèmes de token JWT.
 */
public class SecurityException extends GuerreDesRoyaumesException {

    public SecurityException(String message) {
        super(message);
    }

    public SecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Exception spécifique pour les accès non autorisés
     *
     * @param ressource La ressource à laquelle l'accès a été tenté
     * @return Une exception de type SecurityException avec un message approprié
     */
    public static SecurityException accesNonAutorise(String ressource) {
        return new SecurityException("Accès non autorisé à la ressource: " + ressource);
    }

    /**
     * Exception spécifique pour les problèmes de token JWT
     *
     * @return Une exception de type SecurityException avec un message approprié
     */
    public static SecurityException tokenInvalide() {
        return new SecurityException("Token d'authentification invalide ou expiré");
    }
}