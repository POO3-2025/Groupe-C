package be.helha.projects.GuerreDesRoyaumes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class GuerreDesRoyaumesApplication {

    /** Constructeur par défaut */
    GuerreDesRoyaumesApplication()
    {
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
