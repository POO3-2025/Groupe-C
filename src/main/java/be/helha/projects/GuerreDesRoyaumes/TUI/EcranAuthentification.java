package be.helha.projects.GuerreDesRoyaumes.TUI;

import be.helha.projects.GuerreDesRoyaumes.Service.ServiceAuthentification;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.screen.Screen;

public class EcranAuthentification {

    private final ServiceAuthentification serviceAuthentification;
    private final Screen screen;
    private final WindowBasedTextGUI textGUI;

    public EcranAuthentification(ServiceAuthentification serviceAuthentification, Screen screen) {
        this.serviceAuthentification = serviceAuthentification;
        this.screen = screen;
        this.textGUI = new MultiWindowTextGUI(screen);
    }

    public void afficher() {
        // Création de la fenêtre d'authentification
        Window fenetre = new BasicWindow("Authentification");
        fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        // Création d'un panel pour organiser les éléments
        Panel panel = new Panel(new GridLayout(2));

        // Ajout des champs pour la connexion
        panel.addComponent(new Label("Pseudo:"));
        TextBox champPseudo = new TextBox(new TerminalSize(30, 1));
        panel.addComponent(champPseudo);

        panel.addComponent(new Label("Mot de passe:"));
        TextBox champMotDePasse = new TextBox(new TerminalSize(30, 1)).setMask('*');
        panel.addComponent(champMotDePasse);

        // Ajout de boutons pour les actions
        Panel boutonsPanel = new Panel(new GridLayout(2));
        boutonsPanel.addComponent(new Button("Connexion", () -> {
            boolean authentifie = serviceAuthentification.authentifierJoueur(
                    champPseudo.getText(),
                    champMotDePasse.getText()
            );

            if (authentifie) {
                // Afficher l'écran principal
                fenetre.close();
                afficherEcranPrincipal(champPseudo.getText());
            } else {
                afficherMessageErreur("Authentification échouée");
            }
        }));

        boutonsPanel.addComponent(new Button("Inscription", () -> {
            fenetre.close();
            afficherEcranInscription();
        }));

        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        panel.addComponent(boutonsPanel);

        fenetre.setComponent(panel);
        textGUI.addWindowAndWait(fenetre);
    }

    private void afficherEcranInscription() {
        Window fenetre = new BasicWindow("Inscription");
        fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(2));

        panel.addComponent(new Label("Nom:"));
        TextBox champNom = new TextBox(new TerminalSize(30, 1));
        panel.addComponent(champNom);

        panel.addComponent(new Label("Prénom:"));
        TextBox champPrenom = new TextBox(new TerminalSize(30, 1));
        panel.addComponent(champPrenom);

        panel.addComponent(new Label("Pseudo:"));
        TextBox champPseudo = new TextBox(new TerminalSize(30, 1));
        panel.addComponent(champPseudo);

        panel.addComponent(new Label("Mot de passe:"));
        TextBox champMotDePasse = new TextBox(new TerminalSize(30, 1)).setMask('*');
        panel.addComponent(champMotDePasse);

        panel.addComponent(new EmptySpace(new TerminalSize(0, 0)));
        Button boutonInscrire = new Button("S'inscrire", () -> {
            // Vérification des champs vides
            if (champNom.getText().isEmpty() || champPrenom.getText().isEmpty() || champPseudo.getText().isEmpty() || champMotDePasse.getText().isEmpty()) {
                afficherMessageErreur("Tous les champs doivent être remplis.");
                return;
            }

            // Validation du mot de passe
            if (champMotDePasse.getText().length() < 6) {
                afficherMessageErreur("Le mot de passe doit contenir au moins 6 caractères.");
                return;
            }

            // Validation du pseudo
            if (!champPseudo.getText().matches("[a-zA-Z0-9_]+")) {
                afficherMessageErreur("Le pseudo ne peut contenir que des lettres, chiffres et underscores.");
                return;
            }

            try {
                serviceAuthentification.inscrireJoueur(
                        champNom.getText(),
                        champPrenom.getText(),
                        champPseudo.getText(),
                        champMotDePasse.getText()
                );
                fenetre.close();
                afficher(); // Retour à l'écran d'authentification
            } catch (IllegalArgumentException e) {
                afficherMessageErreur("Erreur : " + e.getMessage());
            } catch (RuntimeException e) {
                afficherMessageErreur("Erreur lors de l'inscription : " + e.getMessage());
            } catch (Exception e) {
                afficherMessageErreur("Erreur inattendue : " + e.getMessage());
                e.printStackTrace(); // Affiche la stack trace dans la console pour le débogage
            }
        });
        panel.addComponent(boutonInscrire);

        fenetre.setComponent(panel);
        textGUI.addWindowAndWait(fenetre);
    }

    private void afficherEcranPrincipal(String pseudo) {
        EcranPrincipal ecranPrincipal = new EcranPrincipal(serviceAuthentification, pseudo, screen);
        ecranPrincipal.afficher();
    }

    private void afficherMessageErreur(String message) {
        MessageDialogBuilder dialogBuilder = new MessageDialogBuilder()
                .setTitle("Erreur")
                .setText(message)
                .addButton(MessageDialogButton.OK);
        dialogBuilder.build().showDialog(textGUI);
    }
}