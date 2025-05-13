package be.helha.projects.GuerreDesRoyaumes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

/**
 * Classe principale de l'application Guerre des Royaumes.
 * Configure et lance l'application Spring Boot avec support pour JWT.
 */
@SpringBootApplication
public class GuerreDesRoyaumesApplication {

    /** Constructeur par défaut */
    public GuerreDesRoyaumesApplication() {
    }

    /**
     * Méthode principale pour lancer l'application.
     *
     * @param args Les arguments de ligne de commande.
     */
    public static void main(String[] args) {
        // Démarre l'application Spring Boot
        SpringApplication.run(GuerreDesRoyaumesApplication.class, args);
    }
}
