package be.helha.projects.GuerreDesRoyaumes.TUI;

import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceAuthentification;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.SimpleTheme;
import com.googlecode.lanterna.graphics.Theme;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.screen.Screen;

import java.util.Collections;
import java.util.regex.Pattern;

public class EcranAuthentification {

    private final ServiceAuthentification serviceAuthentification;
    private final Screen screen;
    private final WindowBasedTextGUI textGUI;

    // Thèmes personnalisés pour les différents composants
    private final Theme themeBase = new SimpleTheme(
            TextColor.ANSI.BLUE,
            TextColor.ANSI.BLACK,
            SGR.BOLD);

    private final Theme themeErreur = new SimpleTheme(
            TextColor.ANSI.RED,
            TextColor.ANSI.BLACK);

    private final Theme themeBouton = new SimpleTheme(
            TextColor.ANSI.YELLOW,
            TextColor.ANSI.BLUE,
            SGR.BOLD);

    private final Theme themeSucces = new SimpleTheme(
            TextColor.ANSI.GREEN,
            TextColor.ANSI.BLACK,
            SGR.BOLD);

    public EcranAuthentification(ServiceAuthentification serviceAuthentification, Screen screen) {
        this.serviceAuthentification = serviceAuthentification;
        this.screen = screen;
        this.textGUI = new MultiWindowTextGUI(screen);
    }

    public Joueur afficher() {
        // Création de la fenêtre d'authentification
        BasicWindow fenetre = new BasicWindow("Guerre Des Royaumes");
        fenetre.setTheme(themeBase);
        fenetre.setHints(Collections.singletonList(Window.Hint.CENTERED));

        // Panneau principal avec une mise en page verticale - augmenté la largeur à 80
        Panel panelPrincipal = new Panel(new LinearLayout(Direction.VERTICAL));
        panelPrincipal.setPreferredSize(new TerminalSize(80, 25));

        // Boîte de titre en haut
        Panel titrePanel = new Panel(new GridLayout(1));
        Label titrePrincipal = new Label("⚔️ GUERRE DES ROYAUMES ⚔️");
        titrePrincipal.setForegroundColor(TextColor.ANSI.YELLOW);
        titrePrincipal.addStyle(SGR.BOLD);
        titrePanel.addComponent(titrePrincipal);
        panelPrincipal.addComponent(titrePanel);

        // Partie "Guerre" en ASCII Art (en cyan)
        String[] guerreArt = {
                " _____                            ",
                "|  __ \\                           ",
                "| |  \\/ _   _  ___ _ __ _ __ ___  ",
                "| | __ | | | |/ _ \\ '__| '__/ _ \\ ",
                "| |_\\ \\| |_| |  __/ |  | | |  __/ ",
                " \\____/ \\__,_|\\___|_|  |_|  \\___| "
        };

        // Partie "Des" en ASCII Art (en jaune)
        String[] desArt = {
                " _____                 ",
                "|  __ \\                ",
                "| |  | | ___  ___      ",
                "| |  | |/ _ \\/ __|     ",
                "| |__| |  __/\\__ \\     ",
                "|_____/ \\___||___/     "
        };

        // Partie "Royaumes" en ASCII Art (en vert)
        String[] royaumesArt = {
                " _____                                             ",
                "|  __ \\                                            ",
                "| |__) |___  _   _  __ _ _   _ _ __ ___   ___  ___ ",
                "|  _  // _ \\| | | |/ _` | | | | '_ ` _ \\ / _ \\/ __|",
                "| | \\ \\ (_) | |_| | (_| | |_| | | | | | |  __/\\__ \\",
                "|_|  \\_\\___/ \\__, |\\__,_|\\__,_|_| |_| |_|\\___||___/",
                "              __/ |                                 ",
                "             |___/                                  "
        };

        // Affichage du mot "Guerre" en cyan
        for (String ligne : guerreArt) {
            Label logoLabel = new Label(ligne);
            logoLabel.setForegroundColor(TextColor.ANSI.CYAN);
            Panel lignePanel = new Panel(new GridLayout(1));
            lignePanel.addComponent(logoLabel);
            panelPrincipal.addComponent(lignePanel);
        }

        // Affichage du mot "Des" en jaune
        for (String ligne : desArt) {
            Label logoLabel = new Label(ligne);
            logoLabel.setForegroundColor(TextColor.ANSI.YELLOW);
            logoLabel.addStyle(SGR.BOLD);
            Panel lignePanel = new Panel(new GridLayout(1));
            lignePanel.addComponent(logoLabel);
            panelPrincipal.addComponent(lignePanel);
        }

        // Affichage du mot "Royaumes" en vert
        for (String ligne : royaumesArt) {
            Label logoLabel = new Label(ligne);
            logoLabel.setForegroundColor(TextColor.ANSI.GREEN);
            Panel lignePanel = new Panel(new GridLayout(1));
            lignePanel.addComponent(logoLabel);
            panelPrincipal.addComponent(lignePanel);
        }

        // Section de connexion
        Panel sectionConnexion = new Panel(new GridLayout(2).setHorizontalSpacing(3));

        // Utilisation d'une simple étiquette pour le titre de section
        Label titreConnexion = new Label("=== CONNEXION ===");
        titreConnexion.setForegroundColor(TextColor.ANSI.YELLOW);
        titreConnexion.addStyle(SGR.BOLD);

        // Panel pour centrer le titre
        Panel titreSectionPanel = new Panel(new GridLayout(1));
        titreSectionPanel.addComponent(titreConnexion);
        panelPrincipal.addComponent(new EmptySpace(TerminalSize.ONE));
        panelPrincipal.addComponent(titreSectionPanel);
        panelPrincipal.addComponent(new EmptySpace(TerminalSize.ONE));

        // Champ pseudo avec style
        Label labelPseudo = new Label("Pseudo:");
        labelPseudo.setForegroundColor(TextColor.ANSI.CYAN);
        sectionConnexion.addComponent(labelPseudo);

        TextBox champPseudo = new TextBox(new TerminalSize(30, 1));
        champPseudo.setTheme(themeBase);
        sectionConnexion.addComponent(champPseudo);

        // Champ mot de passe avec style
        Label labelMotDePasse = new Label("Mot de passe:");
        labelMotDePasse.setForegroundColor(TextColor.ANSI.CYAN);
        sectionConnexion.addComponent(labelMotDePasse);

        TextBox champMotDePasse = new TextBox(new TerminalSize(30, 1)).setMask('*');
        champMotDePasse.setTheme(themeBase);
        sectionConnexion.addComponent(champMotDePasse);

        // Ajout de la section de connexion au panneau principal
        Panel connexionPanel = new Panel(new GridLayout(1));
        connexionPanel.addComponent(sectionConnexion);
        panelPrincipal.addComponent(connexionPanel);
        panelPrincipal.addComponent(new EmptySpace(TerminalSize.ONE));

        // Panneau de boutons avec style
        Panel boutonsPanel = new Panel(new GridLayout(2).setHorizontalSpacing(4));

        // Bouton de connexion avec style
        Button boutonConnexion = new Button("Connexion", () -> {
            String pseudo = champPseudo.getText();
            String motDePasse = champMotDePasse.getText();

            if (pseudo.isEmpty() || motDePasse.isEmpty()) {
                afficherMessageErreur("Veuillez remplir tous les champs!");
                return;
            }

            Joueur joueurConnecte = serviceAuthentification.authentifierJoueur(pseudo, motDePasse);

            if (joueurConnecte != null) {
                afficherMessageSucces("Connexion réussie, bienvenue " + pseudo + " !");
                fenetre.close();
                // On retourne le joueur connecté
                // Le reste de la navigation se fait dans le Main
            } else {
                afficherMessageErreur("Authentification échouée");
            }
        });
        boutonConnexion.setTheme(themeBouton);
        boutonsPanel.addComponent(boutonConnexion);

        // Bouton d'inscription avec style
        Button boutonInscription = new Button("Inscription", () -> {
            fenetre.close();
            afficherEcranInscription();
        });
        boutonInscription.setTheme(themeBouton);
        boutonsPanel.addComponent(boutonInscription);

        // Centrer les boutons
        Panel centeredButtonsPanel = new Panel(new GridLayout(1));
        centeredButtonsPanel.addComponent(boutonsPanel);
        panelPrincipal.addComponent(centeredButtonsPanel);

        // Message de bas de page
        Label copyright = new Label("© 2023 Guerre Des Royaumes - Tous droits réservés");
        copyright.setForegroundColor(TextColor.ANSI.WHITE);
        copyright.addStyle(SGR.ITALIC);
        panelPrincipal.addComponent(new EmptySpace(TerminalSize.ONE));

        // Centrer le copyright
        Panel copyrightPanel = new Panel(new GridLayout(1));
        copyrightPanel.addComponent(copyright);
        panelPrincipal.addComponent(copyrightPanel);

        // Finalisation de la fenêtre
        fenetre.setComponent(panelPrincipal);

        // Affichage de la fenêtre et attente
        textGUI.addWindowAndWait(fenetre);

        // On retourne le joueur connecté ou null si l'authentification a échoué
        if (champPseudo.getText().isEmpty()) {
            return null;
        } else {
            return serviceAuthentification.authentifierJoueur(champPseudo.getText(), champMotDePasse.getText());
        }
    }

    private void afficherEcranInscription() {
        BasicWindow fenetre = new BasicWindow("Inscription");
        fenetre.setTheme(themeBase);
        fenetre.setHints(Collections.singletonList(Window.Hint.CENTERED));

        Panel panelPrincipal = new Panel(new LinearLayout(Direction.VERTICAL));
        panelPrincipal.setPreferredSize(new TerminalSize(80, 25));

        // Titre avec style
        Label titrePage = new Label("=== CRÉATION DE COMPTE ===");
        titrePage.setForegroundColor(TextColor.ANSI.YELLOW);
        titrePage.addStyle(SGR.BOLD);

        // Centrer le titre
        Panel titrePannel = new Panel(new GridLayout(1));
        titrePannel.addComponent(titrePage);
        panelPrincipal.addComponent(titrePannel);
        panelPrincipal.addComponent(new EmptySpace(TerminalSize.ONE));

        // Formulaire d'inscription
        Panel formPanel = new Panel(new GridLayout(2).setHorizontalSpacing(3).setVerticalSpacing(1));

        // Champ Nom
        Label labelNom = new Label("Nom:");
        labelNom.setForegroundColor(TextColor.ANSI.CYAN);
        formPanel.addComponent(labelNom);

        TextBox champNom = new TextBox(new TerminalSize(30, 1));
        champNom.setTheme(themeBase);
        formPanel.addComponent(champNom);

        // Champ Prénom
        Label labelPrenom = new Label("Prénom:");
        labelPrenom.setForegroundColor(TextColor.ANSI.CYAN);
        formPanel.addComponent(labelPrenom);

        TextBox champPrenom = new TextBox(new TerminalSize(30, 1));
        champPrenom.setTheme(themeBase);
        formPanel.addComponent(champPrenom);

        // Champ Pseudo
        Label labelPseudo = new Label("Pseudo:");
        labelPseudo.setForegroundColor(TextColor.ANSI.CYAN);
        formPanel.addComponent(labelPseudo);

        TextBox champPseudo = new TextBox(new TerminalSize(30, 1));
        champPseudo.setTheme(themeBase);
        formPanel.addComponent(champPseudo);

        // Champ Email
        Label labelEmail = new Label("Email:");
        labelEmail.setForegroundColor(TextColor.ANSI.CYAN);
        formPanel.addComponent(labelEmail);

        TextBox champEmail = new TextBox(new TerminalSize(30, 1));
        champEmail.setTheme(themeBase);
        formPanel.addComponent(champEmail);

        // Champ Mot de passe
        Label labelMotDePasse = new Label("Mot de passe:");
        labelMotDePasse.setForegroundColor(TextColor.ANSI.CYAN);
        formPanel.addComponent(labelMotDePasse);

        TextBox champMotDePasse = new TextBox(new TerminalSize(30, 1)).setMask('*');
        champMotDePasse.setTheme(themeBase);
        formPanel.addComponent(champMotDePasse);

        // Ajouter le formulaire au panel principal
        Panel centeredFormPanel = new Panel(new GridLayout(1));
        centeredFormPanel.addComponent(formPanel);
        panelPrincipal.addComponent(centeredFormPanel);
        panelPrincipal.addComponent(new EmptySpace(TerminalSize.ONE));

        // Panneau de boutons
        Panel boutonsPanel = new Panel(new GridLayout(2).setHorizontalSpacing(3));

        // Bouton d'inscription
        Button boutonInscrire = new Button("S'inscrire", () -> {
            String nom = champNom.getText();
            String prenom = champPrenom.getText();
            String pseudo = champPseudo.getText();
            String email = champEmail.getText();
            String motDePasse = champMotDePasse.getText();

            // Validation des champs
            if (nom.isEmpty() || prenom.isEmpty() || pseudo.isEmpty() ||
                    email.isEmpty() || motDePasse.isEmpty()) {
                afficherMessageErreur("Tous les champs sont obligatoires!");
                return;
            }

            // Validation de l'email
            Pattern emailPattern = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
            if (!emailPattern.matcher(email).matches()) {
                afficherMessageErreur("Format d'email invalide!");
                return;
            }

            // Validation du mot de passe
            if (motDePasse.length() < 6) {
                afficherMessageErreur("Le mot de passe doit contenir au moins 6 caractères!");
                return;
            }

            try {
                boolean inscrit = serviceAuthentification.inscrireJoueur(
                        nom, prenom, pseudo, email, motDePasse
                );

                if (inscrit) {
                    afficherMessageSucces("Inscription réussie! Vous pouvez maintenant vous connecter.");
                    fenetre.close();
                    afficher(); // Retour à l'écran d'authentification
                } else {
                    afficherMessageErreur("L'inscription a échoué. Veuillez réessayer.");
                }
            } catch (Exception e) {
                afficherMessageErreur(e.getMessage());
            }
        });
        boutonInscrire.setTheme(themeBouton);
        boutonsPanel.addComponent(boutonInscrire);

        // Bouton Retour
        Button boutonRetour = new Button("Retour", () -> {
            fenetre.close();
            afficher(); // Retour à l'écran d'authentification
        });
        boutonRetour.setTheme(new SimpleTheme(TextColor.ANSI.WHITE, TextColor.ANSI.RED, SGR.BOLD));
        boutonsPanel.addComponent(boutonRetour);

        // Centrer les boutons
        Panel centeredButtonsPanel = new Panel(new GridLayout(1));
        centeredButtonsPanel.addComponent(boutonsPanel);
        panelPrincipal.addComponent(centeredButtonsPanel);

        fenetre.setComponent(panelPrincipal);
        textGUI.addWindowAndWait(fenetre);
    }

    private void afficherMessageErreur(String message) {
        MessageDialogBuilder dialogBuilder = new MessageDialogBuilder()
                .setTitle("⚠️ Erreur ⚠️")
                .setText(message)
                .addButton(MessageDialogButton.OK);
        dialogBuilder.build().showDialog(textGUI);
    }

    private void afficherMessageSucces(String message) {
        MessageDialogBuilder dialogBuilder = new MessageDialogBuilder()
                .setTitle("✅ Succès ✅")
                .setText(message)
                .addButton(MessageDialogButton.OK);
        dialogBuilder.build().showDialog(textGUI);
    }
}