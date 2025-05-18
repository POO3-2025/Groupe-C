package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * Exception personnalisée lancée lors d'erreurs relatives aux coffres des joueurs.
 * <p>
 * Cette exception permet de gérer spécifiquement les problèmes
 * rencontrés lors des opérations sur les coffres (ajout, suppression, transfert d'items).
 * </p>
 * <p>
 * Hérite de {@link GuerreDesRoyaumesException} pour une gestion cohérente
 * des exceptions propres au projet Guerre des Royaumes.
 * </p>
 */
public class CoffreException extends GuerreDesRoyaumesException {

    /**
     * Constructeur avec un message d'erreur.
     *
     * @param message Description de l'erreur liée au coffre
     */
    public CoffreException(String message) {
        super(message);
    }

    /**
     * Constructeur avec un message d'erreur et une cause sous-jacente.
     *
     * @param message Description de l'erreur liée au coffre
     * @param cause   Cause originale de l'exception
     */
    public CoffreException(String message, Throwable cause) {
        super(message, cause);
    }
}
