package be.helha.projects.GuerreDesRoyaumes.Exceptions;

/**
 * Exception lancée lors d'erreurs liées aux transactions financières (achats, ventes, etc.).
 */
public class TransactionException extends GuerreDesRoyaumesException {

    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Exception spécifique pour les cas où un joueur n'a pas assez d'argent pour une transaction.
     *
     * @param argent Le montant actuel du joueur
     * @param montantNecessaire Le montant nécessaire pour l'achat
     * @return Une exception de type TransactionException avec un message approprié
     */
    public static TransactionException fondsInsuffisants(int argent, int montantNecessaire) {
        return new TransactionException("Fonds insuffisants: " + argent +
                " disponible, " + montantNecessaire + " nécessaire");
    }
}