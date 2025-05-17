package be.helha.projects.GuerreDesRoyaumes.TUI.Ecran;

import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Reseau.GestionnaireReseau;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceCombat;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.screen.Screen;

public class EcranConnexionReseau {
    private final WindowBasedTextGUI textGUI;
    private final Screen screen;
    private final ServiceCombat serviceCombat;
    private final Joueur joueur;
    private final Joueur adversaire;
    private final GestionnaireReseau gestionnaireReseau;

    public EcranConnexionReseau(WindowBasedTextGUI textGUI, Screen screen,
                                ServiceCombat serviceCombat, Joueur joueur, Joueur adversaire) {
        this.textGUI = textGUI;
        this.screen = screen;
        this.serviceCombat = serviceCombat;
        this.joueur = joueur;
        this.adversaire = adversaire;
        this.gestionnaireReseau = new GestionnaireReseau();
    }

    public void afficher() {
        Window fenetre = new BasicWindow("Configuration réseau pour le combat");
        fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(1));

        panel.addComponent(new Label("Sélectionnez le mode de connexion pour affronter " + adversaire.getPseudo()));
        panel.addComponent(new EmptySpace());

        // Option pour héberger (serveur)
        panel.addComponent(new Button("Héberger le combat (être hôte)", () -> {
            fenetre.close();
            demarrerServeur();
        }));

        // Option pour rejoindre (client)
        panel.addComponent(new Button("Rejoindre un combat (être client)", () -> {
            fenetre.close();
            afficherEcranConnexionClient();
        }));

        // Bouton retour
        panel.addComponent(new Button("Retour", fenetre::close));

        fenetre.setComponent(panel);
        textGUI.addWindowAndWait(fenetre);
    }

    private void demarrerServeur() {
        // Afficher un message d'attente
        Window fenetreAttente = new BasicWindow("En attente d'un adversaire");
        fenetreAttente.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(1));
        panel.addComponent(new Label("En attente que " + adversaire.getPseudo() + " se connecte..."));
        panel.addComponent(new EmptySpace());
        panel.addComponent(new Label("Votre adresse IP est requise par l'adversaire"));
        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Annuler", fenetreAttente::close));

        fenetreAttente.setComponent(panel);
        textGUI.addWindow(fenetreAttente);

        // Démarrer le serveur dans un thread séparé
        new Thread(() -> {
            boolean succes = gestionnaireReseau.demarrerEnTantQuHote();

            if (succes) {
                // Fermer la fenêtre d'attente
                try {
                    fenetreAttente.close();
                } catch (Exception e) {
                    // Ignorer
                }

                // Continuer vers l'écran de préparation
                serviceCombat.initialiserCombat(joueur, adversaire, null);
                new EcranPreparationCombat(null, textGUI, screen, joueur.getPseudo(), adversaire.getPseudo(), serviceCombat).afficher();
            } else {
                // Afficher un message d'erreur
                afficherMessageErreur("Impossible de démarrer le serveur");
                try {
                    fenetreAttente.close();
                } catch (Exception e) {
                    // Ignorer
                }
            }
        }).start();
    }

    private void afficherEcranConnexionClient() {
        Window fenetreConnexion = new BasicWindow("Connexion au combat");
        fenetreConnexion.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(2));
        panel.addComponent(new Label("Adresse IP de l'hôte:"));

        TextBox ipBox = new TextBox();
        panel.addComponent(ipBox);

        panel.addComponent(new EmptySpace());
        panel.addComponent(new EmptySpace());

        panel.addComponent(new Button("Se connecter", () -> {
            String adresseIP = ipBox.getText().trim();

            if (adresseIP.isEmpty()) {
                afficherMessageErreur("Veuillez entrer une adresse IP");
                return;
            }

            fenetreConnexion.close();
            connecterAuServeur(adresseIP);
        }));

        panel.addComponent(new Button("Annuler", fenetreConnexion::close));

        fenetreConnexion.setComponent(panel);
        textGUI.addWindowAndWait(fenetreConnexion);
    }

    private void connecterAuServeur(String adresseIP) {
        // Afficher un message d'attente
        Window fenetreAttente = new BasicWindow("Connexion en cours");
        fenetreAttente.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(1));
        panel.addComponent(new Label("Tentative de connexion à " + adresseIP + "..."));

        fenetreAttente.setComponent(panel);
        textGUI.addWindow(fenetreAttente);

        // Se connecter dans un thread séparé
        new Thread(() -> {
            boolean succes = gestionnaireReseau.connecterAuServeur(adresseIP);

            if (succes) {
                // Fermer la fenêtre d'attente
                try {
                    fenetreAttente.close();
                } catch (Exception e) {
                    // Ignorer
                }

                // Continuer vers l'écran de préparation
                serviceCombat.initialiserCombat(joueur, adversaire, null);
                new EcranPreparationCombat(null, textGUI, screen, joueur.getPseudo(), adversaire.getPseudo(), serviceCombat).afficher();
            } else {
                // Afficher un message d'erreur
                afficherMessageErreur("Impossible de se connecter au serveur " + adresseIP);
                try {
                    fenetreAttente.close();
                } catch (Exception e) {
                    // Ignorer
                }
            }
        }).start();
    }

    private void afficherMessageErreur(String message) {
        new MessageDialogBuilder()
                .setTitle("Erreur")
                .setText(message)
                .addButton(MessageDialogButton.OK)
                .build()
                .showDialog(textGUI);
    }
}