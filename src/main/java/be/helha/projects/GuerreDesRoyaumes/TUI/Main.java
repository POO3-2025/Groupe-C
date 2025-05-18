package be.helha.projects.GuerreDesRoyaumes.TUI;

import be.helha.projects.GuerreDesRoyaumes.Config.*;
import be.helha.projects.GuerreDesRoyaumes.Config.ConnexionConfig.ConnexionManager;
import be.helha.projects.GuerreDesRoyaumes.Config.InitialiserAPP;
import be.helha.projects.GuerreDesRoyaumes.Config.ConnexionConfig.MongoDBConfigManager;
import be.helha.projects.GuerreDesRoyaumes.Config.ConnexionConfig.SQLConfigManager;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.CoffreMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.InventaireMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.ItemMongoDAOImpl;
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

import java.sql.Connection;

public class Main {

    public static void main(String[] args) {
        Connection sqlConnection = null;
        MongoDatabase mongoDB = null;
        Screen screen = null;

        try {
            sqlConnection = InitialiserAPP.getSQLConnexion();
            mongoDB = InitialiserAPP.getMongoConnexion();

            System.out.println("Connexions aux bases de données établies avec succès!");

            // Initialisation des DAOs
            JoueurDAOImpl joueurDAO = JoueurDAOImpl.getInstance();

            // Initialiser les DAOs MongoDB
            ItemMongoDAOImpl itemDAO = ItemMongoDAOImpl.getInstance();
            CoffreMongoDAOImpl coffreDAO = CoffreMongoDAOImpl.getInstance();
            InventaireMongoDAOImpl inventaireDAO = InventaireMongoDAOImpl.getInstance();


            joueurDAO.setConnection(sqlConnection);
            System.out.println("DAOs initialisés avec les connexions SQL");


            // Initialisation des services
            ServiceAuthentification serviceAuth = new ServiceAuthentificationImpl(joueurDAO);
            System.out.println("Services initialisés");

            // Configuration de l'interface utilisateur
            DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
            terminalFactory.setInitialTerminalSize(new TerminalSize(80, 24));
            SwingTerminalFrame terminal = terminalFactory.createSwingTerminal();
            terminal.setVisible(true);
            terminal.setResizable(true); // Désactiver la redimension

            // Création de l'écran à partir du terminal
            screen = new TerminalScreen(terminal);
            screen.startScreen();
            WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);

            // Lancement de l'écran d'authentification
            new EcranAuthentification(serviceAuth, textGUI, screen, joueurDAO).afficher();

        } catch (Exception e) {
            System.err.println("Erreur d'initialisation: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (screen != null) screen.stopScreen();
                if (sqlConnection != null && !sqlConnection.isClosed()) sqlConnection.close();
                ConnexionManager.getInstance().closeConnections();
                System.out.println("Ressources correctement libérées");
            } catch (Exception e) {
                System.err.println("Erreur lors du nettoyage: " + e.getMessage());
            }
        }
    }
}