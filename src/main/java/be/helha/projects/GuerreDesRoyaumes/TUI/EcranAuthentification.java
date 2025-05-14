package be.helha.projects.GuerreDesRoyaumes.TUI;

import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceAuthentification;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.screen.Screen;

import java.io.IOException;

public class EcranAuthentification {
    private final ServiceAuthentification serviceAuthentification;
    private final WindowBasedTextGUI textGUI;
    private final Screen screen;
    private final EcranConnexion ecranConnexion;
    private final EcranInscription ecranInscription;
    private final JoueurDAO joueurDAO;

    public EcranAuthentification(ServiceAuthentification serviceAuthentification, WindowBasedTextGUI textGUI, Screen screen, JoueurDAO joueurDAO) {
        this.serviceAuthentification = serviceAuthentification;
        this.textGUI = textGUI;
        this.screen = screen;
        this.joueurDAO = joueurDAO;
        this.ecranConnexion = new EcranConnexion(serviceAuthentification, textGUI, screen, this, joueurDAO);
        this.ecranInscription = new EcranInscription(serviceAuthentification, textGUI, screen, this);
    }

    public void afficher() {
        Window fenetre = new BasicWindow("Guerre des Royaumes - Authentification");
        fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(1));
        panel.setLayoutData(GridLayout.createLayoutData(
            GridLayout.Alignment.CENTER,
            GridLayout.Alignment.CENTER,
            true,
            true
        ));

        // Titre
        panel.addComponent(new Label("Bienvenue dans Guerre des Royaumes"));
        panel.addComponent(new EmptySpace());

        // Boutons
        panel.addComponent(new Button("Se connecter", () -> {
            fenetre.close();
            ecranConnexion.afficher();
        }));

        panel.addComponent(new EmptySpace());

        panel.addComponent(new Button("S'inscrire", () -> {
            fenetre.close();
            ecranInscription.afficher();
        }));

        panel.addComponent(new EmptySpace());

        panel.addComponent(new Button("Quitter", () -> {
            fenetre.close();
            try {
                screen.stopScreen();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));

        fenetre.setComponent(panel);
        textGUI.addWindowAndWait(fenetre);
    }
}