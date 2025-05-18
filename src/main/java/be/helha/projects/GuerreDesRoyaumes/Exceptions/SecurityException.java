package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * Exception personnalisée lancée lors d'erreurs liées à la sécurité de l'application,
 * telles que les accès non autorisés ou les problèmes liés aux tokens JWT.
 * <p>
 * Permet une gestion centralisée des problèmes de sécurité dans le projet
 * Guerre des Royaumes.
 * </p>
 * <p>
 * Hérite de {@link GuerreDesRoyaumesException} pour une gestion cohérente des exceptions.
 * </p>
 */
public class SecurityException extends GuerreDesRoyaumesException {

    /**
     * Constructeur avec un message décrivant l'erreur de sécurité.
     *
     * @param message Description de l'erreur de sécurité survenue
     */
    public SecurityException(String message) {
        super(message);
    }

    /**
     * Constructeur avec un message et une cause sous-jacente.
     *
     * @param message Description de l'erreur de sécurité survenue
     * @param cause   Cause originale de l'exception
     */
    public SecurityException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Méthode utilitaire statique pour créer une exception indiquant
     * un accès non autorisé à une ressource spécifique.
     *
     * @param ressource La ressource à laquelle l'accès a été tenté
     * @return Une instance de {@link SecurityException} avec un message détaillé
     */
    public static SecurityException accesNonAutorise(String ressource) {
        return new SecurityException("Accès non autorisé à la ressource: " + ressource);
    }

    /**
     * Méthode utilitaire statique pour créer une exception indiquant
     * un problème avec un token JWT invalide ou expiré.
     *
     * @return Une instance de {@link SecurityException} avec un message adapté
     */
    public static SecurityException tokenInvalide() {
        return new SecurityException("Token d'authentification invalide ou expiré");
    }
}
