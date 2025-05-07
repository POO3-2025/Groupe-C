package be.helha.projects.GuerreDesRoyaumes.TUI;

import be.helha.projects.GuerreDesRoyaumes.Config.DatabaseConfigManager;
import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.JoueurDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.ServiceImpl.ServiceAuthentificationImpl;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceAuthentification;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceBoutique;
import be.helha.projects.GuerreDesRoyaumes.ServiceImpl.ServiceBoutiqueImpl;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;

import java.io.IOException;
import java.sql.Connection;
import java.awt.Font;

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
        ServiceBoutique serviceBoutique = new ServiceBoutiqueImpl(); // Initialisation du service boutique

        // Configurer une taille de terminal adaptée à notre interface
        TerminalSize terminalSize = new TerminalSize(100, 40);

        // Créer un terminal avec une configuration personnalisée
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory()
                .setInitialTerminalSize(terminalSize)
                .setTerminalEmulatorTitle("Guerre Des Royaumes");

        try {
            // Configurer une police qui supporte bien les caractères spéciaux
            SwingTerminalFontConfiguration fontConfig = SwingTerminalFontConfiguration.newInstance(
                    new Font("Monospaced", Font.BOLD, 14));
            terminalFactory.setTerminalEmulatorFontConfiguration(fontConfig);
        } catch (Exception e) {
            System.err.println("Erreur de configuration de la police: " + e.getMessage());
        }

        Terminal terminal = terminalFactory.createTerminal();
        Screen screen = new TerminalScreen(terminal);

        // Effacer l'écran et définir un fond bleu foncé
        screen.startScreen();
        screen.clear();

        // Afficher l'écran d'authentification
        EcranAuthentification ecranAuthentification = new EcranAuthentification(serviceAuthentification, screen);
        Joueur joueurConnecte = ecranAuthentification.afficher();

        // Si l'authentification est réussie, afficher l'écran de la boutique
        if (joueurConnecte != null) {
            EcranBoutique ecranBoutique = new EcranBoutique(serviceBoutique, joueurConnecte, screen);
            ecranBoutique.afficher();
        }

        // Fermer l'écran et terminer l'application
        screen.stopScreen();
    }
}