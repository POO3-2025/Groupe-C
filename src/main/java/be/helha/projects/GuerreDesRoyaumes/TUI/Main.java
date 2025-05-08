package be.helha.projects.GuerreDesRoyaumes.TUI;

import be.helha.projects.GuerreDesRoyaumes.Config.DatabaseConfigManager;
import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.JoueurDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.ServiceImpl.ServiceAuthentificationImpl;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceAuthentification;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.mongodb.client.MongoDatabase;

import java.io.IOException;
import java.sql.Connection;

public class Main {

    public static void main(String[] args) {
        Connection sqlConnection = null;
        Screen screen = null;

        try {
            // Étape 1 : Obtenir l'instance du gestionnaire de configuration de la base de données
            DatabaseConfigManager dbConfigManager = DatabaseConfigManager.getInstance();
            System.out.println("Gestionnaire de configuration initialisé avec succès!");

            try {
                // Étape 2 : Obtenir la connexion SQL
                sqlConnection = dbConfigManager.getSQLConnection("sqlserver");
                System.out.println("Connexion à la base de données SQL réussie!");
            } catch (Exception e) {
                System.err.println("Erreur lors de la connexion à la base de données SQL: " + e.getMessage());
                e.printStackTrace();
                // On continue quand même pour permettre de tester l'interface
            }

            try {
                // Étape 3 : Obtenir la base de données MongoDB
                MongoDatabase mongoDatabase = dbConfigManager.getMongoDatabase("mongodb");
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

            // Initialiser Lanterna
            Terminal terminal = new DefaultTerminalFactory().createTerminal();
            screen = new TerminalScreen(terminal);
            screen.startScreen();
            System.out.println("Interface Lanterna initialisée avec succès!");

            // Afficher l'écran d'authentification
            EcranAuthentification ecranAuthentification = new EcranAuthentification(serviceAuthentification, screen);
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

                // Fermer le client MongoDB
                DatabaseConfigManager.getInstance().closeMongoClient();
                System.out.println("Client MongoDB fermé avec succès!");

            } catch (Exception e) {
                System.err.println("Erreur lors de la fermeture des ressources: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}