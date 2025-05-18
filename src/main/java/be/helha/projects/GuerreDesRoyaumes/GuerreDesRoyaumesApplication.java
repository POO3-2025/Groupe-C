package be.helha.projects.GuerreDesRoyaumes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principale de l'application "Guerre des Royaumes".
 * <p>
 * Cette classe configure et lance le contexte Spring Boot de l'application.
 * Elle est le point d'entrée du programme.
 * </p>
 * <p>
 * Note : Cette configuration active l'auto-configuration de Spring Boot par défaut.
 * </p>
 */
@SpringBootApplication
public class GuerreDesRoyaumesApplication {

    /**
     * Constructeur par défaut.
     */
    public GuerreDesRoyaumesApplication() {
        // Constructeur vide
    }

    /**
     * Point d'entrée de l'application.
     * Cette méthode démarre le serveur Spring Boot et initialise
     * tous les composants nécessaires.
     *
     * @param args Arguments passés en ligne de commande au démarrage
     */
    public static void main(String[] args) {
        SpringApplication.run(GuerreDesRoyaumesApplication.class, args);
    }
}
