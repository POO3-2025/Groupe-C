package be.helha.projects.GuerreDesRoyaumes.Exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.util.Map;

/**
 * Gestionnaire global d'exceptions pour l'application.
 * Intercepte les exceptions et les convertit en réponses HTTP appropriées.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Gère les exceptions liées aux joueurs non trouvés
     */
    @ExceptionHandler(JoueurNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleJoueurNotFoundException(JoueurNotFoundException ex, WebRequest request) {
        logger.error("Joueur non trouvé: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", ex.getMessage()));
    }

    /**
     * Gère les exceptions liées aux items non trouvés
     */
    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleItemNotFoundException(ItemNotFoundException ex, WebRequest request) {
        logger.error("Item non trouvé: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", ex.getMessage()));
    }

    /**
     * Gère les exceptions liées aux personnages non trouvés
     */
    @ExceptionHandler(PersonnageNotFoundException.class)
    public ResponseEntity<Map<String, String>> handlePersonnageNotFoundException(PersonnageNotFoundException ex, WebRequest request) {
        logger.error("Personnage non trouvé: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", ex.getMessage()));
    }

    /**
     * Gère les exceptions liées aux royaumes non trouvés
     */
    @ExceptionHandler(RoyaumeNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleRoyaumeNotFoundException(RoyaumeNotFoundException ex, WebRequest request) {
        logger.error("Royaume non trouvé: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", ex.getMessage()));
    }

    /**
     * Gère les exceptions liées aux compétences non trouvées
     */
    @ExceptionHandler(CompetenceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCompetenceNotFoundException(CompetenceNotFoundException ex, WebRequest request) {
        logger.error("Compétence non trouvée: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("message", ex.getMessage()));
    }

    /**
     * Gère les exceptions liées à l'authentification
     */
    @ExceptionHandler(AuthentificationException.class)
    public ResponseEntity<Map<String, String>> handleAuthentificationException(AuthentificationException ex, WebRequest request) {
        logger.error("Erreur d'authentification: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("message", ex.getMessage()));
    }

    /**
     * Gère les exceptions liées aux accès refusés
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
        logger.error("Accès refusé: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", "Vous n'avez pas les droits nécessaires pour effectuer cette action"));
    }

    /**
     * Gère les exceptions liées aux transactions
     */
    @ExceptionHandler(TransactionException.class)
    public ResponseEntity<Map<String, String>> handleTransactionException(TransactionException ex, WebRequest request) {
        logger.error("Erreur de transaction: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage()));
    }

    /**
     * Gère les exceptions liées aux coffres
     */
    @ExceptionHandler(CoffreException.class)
    public ResponseEntity<Map<String, String>> handleCoffreException(CoffreException ex, WebRequest request) {
        logger.error("Erreur de coffre: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage()));
    }

    /**
     * Gère les exceptions liées aux items
     */
    @ExceptionHandler(ItemException.class)
    public ResponseEntity<Map<String, String>> handleItemException(ItemException ex, WebRequest request) {
        logger.error("Erreur d'item: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage()));
    }

    /**
     * Gère les exceptions liées aux combats
     */
    @ExceptionHandler(CombatException.class)
    public ResponseEntity<Map<String, String>> handleCombatException(CombatException ex, WebRequest request) {
        logger.error("Erreur de combat: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("message", ex.getMessage()));
    }

    /**
     * Gère les exceptions liées à la base de données
     */
    @ExceptionHandler(DatabaseException.class)
    public ResponseEntity<Map<String, String>> handleDatabaseException(DatabaseException ex, WebRequest request) {
        logger.error("Erreur de base de données: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Une erreur est survenue lors de l'accès à la base de données"));
    }

    /**
     * Gère les exceptions de sécurité
     */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, String>> handleSecurityException(SecurityException ex, WebRequest request) {
        logger.error("Erreur de sécurité: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of("message", ex.getMessage()));
    }

    /**
     * Gère les exceptions génériques du projet
     */
    @ExceptionHandler(GuerreDesRoyaumesException.class)
    public ResponseEntity<Map<String, String>> handleGuerreDesRoyaumesException(GuerreDesRoyaumesException ex, WebRequest request) {
        logger.error("Erreur générique: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", ex.getMessage()));
    }

    /**
     * Gère toutes les autres exceptions non spécifiquement traitées
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleGlobalException(Exception ex, WebRequest request) {
        logger.error("Erreur non gérée: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("message", "Une erreur inattendue est survenue. Veuillez réessayer ultérieurement."));
    }
}