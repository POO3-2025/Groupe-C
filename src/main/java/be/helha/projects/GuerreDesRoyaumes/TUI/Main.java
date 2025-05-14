package be.helha.projects.GuerreDesRoyaumes.TUI;

import be.helha.projects.GuerreDesRoyaumes.Config.*;
import be.helha.projects.GuerreDesRoyaumes.Controller.CombatController;
import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.CombatDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.JoueurDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceCombat;
import be.helha.projects.GuerreDesRoyaumes.ServiceImpl.ServiceAuthentificationImpl;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceAuthentification;
import be.helha.projects.GuerreDesRoyaumes.ServiceImpl.ServiceCombatImpl;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.WindowBasedTextGUI;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import com.mongodb.client.MongoDatabase;

import java.io.IOException;
import java.sql.Connection;

public class Main {

    public static void main(String[] args) {
        Connection sqlConnection = null;
        Screen screen = null;


        try {
            // Étape 1 : Obtenir les instances des gestionnaires de base de données
            SQLConfigManager sqlManager = SQLConfigManager.getInstance();
            MongoDBConfigManager mongoManager = MongoDBConfigManager.getInstance();
            System.out.println("Gestionnaires de configuration initialisés avec succès!");

            try {
                // Étape 2 : Obtenir la connexion SQL
                sqlConnection = sqlManager.getConnection("sqlserver");
                System.out.println("Connexion à la base de données SQL réussie!");
            } catch (Exception e) {
                System.err.println("Erreur lors de la connexion à la base de données SQL: " + e.getMessage());
                e.printStackTrace();
                // On continue quand même pour permettre de tester l'interface
            }

            try {
                // Étape 3 : Obtenir la base de données MongoDB
                MongoDatabase mongoDatabase = mongoManager.getDatabase("mongodb");
                System.out.println("Connexion à la base de données MongoDB réussie!");
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
            ecranAuthentification.afficher(); // Utilisez afficher() au lieu de afficherEcranConnexion()



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

                // Fermer le client MongoDB
                MongoDBConfigManager.getInstance().closeClient();
                System.out.println("Client MongoDB fermé avec succès!");

            } catch (Exception e) {
                System.err.println("Erreur lors de la fermeture des ressources: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
