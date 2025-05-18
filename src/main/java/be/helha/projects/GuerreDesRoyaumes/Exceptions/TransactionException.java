package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * Exception personnalisée lancée lors d'erreurs liées aux transactions financières
 * telles que les achats ou ventes dans le jeu.
 * <p>
 * Permet de gérer les cas d'erreurs spécifiques au système de transaction monétaire
 * dans le projet Guerre des Royaumes.
 * </p>
 * <p>
 * Hérite de {@link GuerreDesRoyaumesException} pour une gestion cohérente des exceptions.
 * </p>
 */
public class TransactionException extends GuerreDesRoyaumesException {

    /**
     * Constructeur avec un message décrivant l'erreur liée à la transaction.
     *
     * @param message Description de l'erreur survenue lors de la transaction
     */
    public TransactionException(String message) {
        super(message);
    }

    /**
     * Constructeur avec un message et une cause sous-jacente.
     *
     * @param message Description de l'erreur survenue lors de la transaction
     * @param cause   Cause originale de l'exception
     */
    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Méthode utilitaire statique pour créer une exception indiquant
     * que les fonds disponibles sont insuffisants pour effectuer une transaction.
     *
     * @param argent            Montant d'argent actuellement disponible
     * @param montantNecessaire Montant d'argent requis pour la transaction
     * @return Une instance de {@link TransactionException} avec un message détaillé
     */
    public static TransactionException fondsInsuffisants(int argent, int montantNecessaire) {
        return new TransactionException("Fonds insuffisants: " + argent +
                " disponible, " + montantNecessaire + " nécessaire");
    }
}
