package be.helha.projects.GuerreDesRoyaumes.TUI;

import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceAuthentification;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.screen.Screen;

public class EcranPrincipal {

    private final ServiceAuthentification serviceAuthentification;
    private final JoueurDAO joueurDAO;
    private final String pseudo;
    private final Screen screen;
    private final WindowBasedTextGUI textGUI;

    public EcranPrincipal(ServiceAuthentification serviceAuthentification, JoueurDAO joueurDAO, String pseudo, Screen screen) {
        this.serviceAuthentification = serviceAuthentification;
        this.joueurDAO = joueurDAO;
        this.pseudo = pseudo;
        this.screen = screen;
        this.textGUI = new MultiWindowTextGUI(screen);
    }

    public void afficher() {
        Joueur joueur = joueurDAO.obtenirJoueurParPseudo(pseudo);
        if (joueur == null) {
            afficherMessageErreur("Joueur non trouvé");
            return;
        }

        Window fenetre = new BasicWindow("Guerre des Royaumes - Menu Principal");
        fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(1));
        panel.addComponent(new Label("Bienvenue, " + joueur.getPseudo() + " !"));
        panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));

        panel.addComponent(new Button("Gérer Profil", () -> {
            fenetre.close();
            afficherEcranGestionProfil(joueur);
        }));

        panel.addComponent(new Button("Choisir Personnage", () -> {
            fenetre.close();
            afficherEcranChoixPersonnage(joueur);
        }));

        panel.addComponent(new Button("Gérer Inventaire", () -> {
            fenetre.close();
            afficherEcranGestionInventaire(joueur);
        }));

        panel.addComponent(new Button("Boutique", () -> {
            fenetre.close();
            afficherEcranBoutique(joueur);
        }));

        panel.addComponent(new Button("Gérer Royaume", () -> {
            fenetre.close();
            afficherEcranGestionRoyaume(joueur);
        }));

        panel.addComponent(new Button("Combattre", () -> {
            fenetre.close();
            afficherEcranCombat(joueur);
        }));

        panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));
        panel.addComponent(new Button("Déconnexion", fenetre::close));

        fenetre.setComponent(panel);
        textGUI.addWindowAndWait(fenetre);
    }

    private void afficherEcranGestionProfil(Joueur joueur) {
        // Implémentation à venir pour la gestion du profil
    }

    private void afficherEcranChoixPersonnage(Joueur joueur) {
        // Implémentation à venir pour le choix du personnage
    }

    private void afficherEcranGestionInventaire(Joueur joueur) {
        // Implémentation à venir pour la gestion de l'inventaire
    }

    private void afficherEcranBoutique(Joueur joueur) {
        // Implémentation à venir pour la boutique
    }

    private void afficherEcranGestionRoyaume(Joueur joueur) {
        // Implémentation à venir pour la gestion du royaume
    }

    private void afficherEcranCombat(Joueur joueur) {
        // Implémentation à venir pour le combat
    }

    private void afficherMessageErreur(String message) {
        MessageDialogBuilder dialogBuilder = new MessageDialogBuilder()
                .setTitle("Erreur")
                .setText(message)
                .addButton(MessageDialogButton.OK);
        dialogBuilder.build().showDialog(textGUI);
    }
}