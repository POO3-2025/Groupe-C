package be.helha.projects.GuerreDesRoyaumes.TUI;

import be.helha.projects.GuerreDesRoyaumes.Config.*;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.JoueurDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.ServiceImpl.ServiceAuthentificationImpl;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceAuthentification;
import be.helha.projects.GuerreDesRoyaumes.TUI.Ecran.EcranAuthentification;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import com.mongodb.client.MongoDatabase;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

public class Main {

    public static void main(String[] args) {
        Connection sqlConnection = null;
        Screen screen = null;
        MongoDatabase mongoDB = null;

        try {
            // Chargement des propriétés à partir du fichier application.properties
            Properties properties = loadApplicationProperties();

            // Récupération des propriétés SQL
            String sqlUrl = properties.getProperty("spring.datasource.url");
            String sqlUsername = properties.getProperty("spring.datasource.username");
            String sqlPassword = properties.getProperty("spring.datasource.password");
            String sqlDriverClassName = properties.getProperty("spring.datasource.driver-class-name");

            // Récupération des propriétés MongoDB
            String mongoHost = properties.getProperty("spring.data.mongodb.host");
            String mongoPort = properties.getProperty("spring.data.mongodb.port");
            String mongoDatabaseName = properties.getProperty("spring.data.mongodb.database");
            String mongoUsername = properties.getProperty("spring.data.mongodb.username");
            String mongoPassword = properties.getProperty("spring.data.mongodb.password");

            // Vérification des propriétés obligatoires
            if (sqlUrl == null || sqlUsername == null || sqlPassword == null || sqlDriverClassName == null) {
                throw new IllegalStateException("Propriétés SQL obligatoires manquantes dans application.properties");
            }

            if (mongoHost == null || mongoPort == null || mongoDatabaseName == null) {
                throw new IllegalStateException("Propriétés MongoDB obligatoires manquantes dans application.properties");
            }

            // Initialisation des gestionnaires avec les propriétés chargées
            // Configuration SQL Server
            SQLConfigManager.initialize(sqlUrl, sqlUsername, sqlPassword, sqlDriverClassName);

            // Configuration MongoDB
            MongoDBConfigManager.initialize(mongoHost, mongoPort, mongoDatabaseName, mongoUsername, mongoPassword);

            System.out.println("Gestionnaires de configuration initialisés avec les propriétés de application.properties!");

            // Initialiser le ConnexionManager
            ConnexionManager connexionManager = ConnexionManager.getInstance();

            try {
                // Obtenir les connexions via le ConnexionManager
                sqlConnection = connexionManager.getSQLConnection();
                System.out.println("Connexion à la base de données SQL réussie via ConnexionManager!");
            } catch (Exception e) {
                System.err.println("Erreur lors de la connexion à la base de données SQL: " + e.getMessage());
                e.printStackTrace();
                // On continue quand même pour permettre de tester l'interface
            }

            try {
                // Obtenir la base de données MongoDB via le ConnexionManager
                mongoDB = connexionManager.getMongoDatabase();
                System.out.println("Connexion à la base de données MongoDB réussie via ConnexionManager!");
            } catch (Exception e) {
                System.err.println("Erreur lors de la connexion à la base de données MongoDB: " + e.getMessage());
                e.printStackTrace();
                // On continue quand même pour permettre de tester l'interface
            }

            // Initialiser les DAOs
            JoueurDAOImpl joueurDAO = JoueurDAOImpl.getInstance();

            // Définir la connexion pour le DAO si disponible
            if (sqlConnection != null) {
                joueurDAO.setConnection(sqlConnection);
                System.out.println("DAO Joueur initialisé avec succès!");
            } else {
                System.err.println("Attention: DAO Joueur initialisé sans connexion à la base de données!");
            }

            // Initialiser les services
            ServiceAuthentification serviceAuthentification = new ServiceAuthentificationImpl(joueurDAO, null);
            System.out.println("Service d'authentification initialisé avec succès!");

            // Utilisation de DefaultTerminalFactory pour créer un terminal Swing
            DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
            // Spécifiez les dimensions ici
            terminalFactory.setInitialTerminalSize(new TerminalSize(80, 24));
            // Ajout du SwingTerminal dans un SwingTerminalFrame
            SwingTerminalFrame terminal = terminalFactory.createSwingTerminal();
            terminal.setVisible(true);
            terminal.setResizable(true); // Désactiver la redimension

            // Création de l'écran à partir du terminal
            screen = new TerminalScreen(terminal);

            screen.startScreen(); // Démarre l'écran du terminal
            System.out.println("Interface Lanterna initialisée avec succès!");

            // Après avoir initialisé l'écran (screen), créez la GUI
            WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);

            // Affiche l'écran d'authentification
            EcranAuthentification ecranAuthentification = new EcranAuthentification(serviceAuthentification, textGUI, screen, joueurDAO);
            ecranAuthentification.afficher();

        } catch (Exception e) {
            System.err.println("Erreur fatale lors de l'initialisation de l'application: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                // Fermer l'écran si ouvert
                if (screen != null) {
                    screen.stopScreen();
                }

                // Fermer la connexion SQL si ouverte
                if (sqlConnection != null && !sqlConnection.isClosed()) {
                    sqlConnection.close();
                    System.out.println("Connexion SQL fermée avec succès!");
                }

                // Fermer toutes les ressources via ConnexionManager
                try {
                    ConnexionManager.getInstance().closeConnections();
                    System.out.println("Toutes les connexions ont été fermées via ConnexionManager");
                } catch (Exception e) {
                    System.err.println("Erreur lors de la fermeture des connexions: " + e.getMessage());
                }

            } catch (Exception e) {
                System.err.println("Erreur lors de la fermeture des ressources: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Charge les propriétés depuis le fichier application.properties
     * @return Objet Properties contenant les propriétés
     */
    private static Properties loadApplicationProperties() {
        Properties properties = new Properties();
        try {
            // Essayer de charger depuis le classpath (pour l'exécution avec Spring)
            try (InputStream input = Main.class.getClassLoader().getResourceAsStream("application.properties")) {
                if (input != null) {
                    properties.load(input);
                    return properties;
                }
            }

            // Si échec, essayer de charger depuis le chemin du système de fichiers
            try (FileInputStream fileInput = new FileInputStream("src/main/resources/application.properties")) {
                properties.load(fileInput);
            }
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement de application.properties: " + e.getMessage());
            e.printStackTrace();
        }
        return properties;
    }
}
