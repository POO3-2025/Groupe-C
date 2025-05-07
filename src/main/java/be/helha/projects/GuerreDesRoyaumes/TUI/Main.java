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

import java.io.IOException;
import java.sql.Connection;

public class Main {

    public static void main(String[] args) throws IOException {
        // Initialiser la connexion à la base de données
        Connection connection = null;
        try {
            connection = DatabaseConfigManager.getInstance().getSQLConnection("sqlserver");
        } catch (Exception e) {
            System.err.println("Erreur de connexion à la base de données: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        // Initialiser les DAOs
        JoueurDAO joueurDAO = new JoueurDAOImpl();
        // Initialisez d'autres DAO ici

        // Initialiser les services
        ServiceAuthentification serviceAuthentification = new ServiceAuthentificationImpl(joueurDAO, null); // Le PersonnageDAO sera implémenté plus tard
        // Initialisez d'autres services ici

        // Initialiser Lanterna
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen screen = new TerminalScreen(terminal);
        screen.startScreen();

        // Afficher l'écran d'authentification
        EcranAuthentification ecranAuthentification = new EcranAuthentification(serviceAuthentification, screen);
        ecranAuthentification.afficher();

        // Fermer l'écran et terminer l'application
        screen.stopScreen();
    }
}