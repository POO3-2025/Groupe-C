package be.helha.projects.GuerreDesRoyaumes.TUI.Ecran;

import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Golem;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Guerrier;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Voleur;
import be.helha.projects.GuerreDesRoyaumes.Model.Royaume;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceAuthentification;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.screen.Screen;

public class EcranInscription {
    private final ServiceAuthentification serviceAuthentification;
    private final WindowBasedTextGUI textGUI;
    private final Screen screen;
    private final EcranAuthentification ecranAuthentification;

    public EcranInscription(ServiceAuthentification serviceAuthentification, WindowBasedTextGUI textGUI, Screen screen, EcranAuthentification ecranAuthentification) {
        this.serviceAuthentification = serviceAuthentification;
        this.textGUI = textGUI;
        this.screen = screen;
        this.ecranAuthentification = ecranAuthentification;
    }

    public void afficher() {
        Window fenetre = new BasicWindow("Inscription - Guerre des Royaumes");
        fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(2));
        
        // Champs d'inscription
        panel.addComponent(new Label("Nom :"));
        TextBox nomBox = new TextBox();
        panel.addComponent(nomBox);

        panel.addComponent(new Label("Prénom :"));
        TextBox prenomBox = new TextBox();
        panel.addComponent(prenomBox);

        panel.addComponent(new Label("Pseudo :"));
        TextBox pseudoBox = new TextBox();
        panel.addComponent(pseudoBox);

        panel.addComponent(new Label("Mot de passe :"));
        TextBox mdpBox = new TextBox().setMask('*');
        panel.addComponent(mdpBox);

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("S'inscrire", () -> {
            String nom = nomBox.getText();
            String prenom = prenomBox.getText();
            String pseudo = pseudoBox.getText();
            String motDePasse = mdpBox.getText();

            if (nom.isEmpty() || prenom.isEmpty() || pseudo.isEmpty() || motDePasse.isEmpty()) {
                afficherMessageErreur("Tous les champs sont obligatoires");
                return;
            }

            try {
                serviceAuthentification.inscrireJoueur(nom, prenom, pseudo, motDePasse);
                fenetre.close();
                afficherInitialisation(pseudo);
            } catch (IllegalArgumentException e) {
                afficherMessageErreur(e.getMessage());
            }
        }));

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Retour", () -> {
            fenetre.close();
            ecranAuthentification.afficher();
        }));

        fenetre.setComponent(panel);
        textGUI.addWindowAndWait(fenetre);
    }

    private void afficherInitialisation(String pseudo) {
        Window fenetreInit = new BasicWindow("Initialisation du compte - " + pseudo);
        fenetreInit.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(1));

        // Création du royaume
        panel.addComponent(new Label("Étape 1 : Création de votre royaume"));
        panel.addComponent(new Label("Choisissez un nom pour votre royaume :"));
        
        TextBox nomRoyaumeBox = new TextBox();
        panel.addComponent(nomRoyaumeBox);

        // Choix du personnage
        panel.addComponent(new EmptySpace());
        panel.addComponent(new Label("Étape 2 : Choix de votre personnage"));
        panel.addComponent(new Label("Choisissez votre personnage :"));

        // Description des personnages
        Panel panelPersonnages = new Panel(new GridLayout(1));
        
        // Guerrier
        Panel panelGuerrier = new Panel(new GridLayout(1));
        panelGuerrier.addComponent(new Label("Guerrier :"));
        panelGuerrier.addComponent(new Label("Points de vie : 100"));
        panelGuerrier.addComponent(new Label("Dégâts : 40"));
        panelGuerrier.addComponent(new Label("Résistance : 20"));
        panelGuerrier.addComponent(new Label("Spécialité : Combat rapproché"));
        panelPersonnages.addComponent(panelGuerrier);

        // Voleur
        Panel panelVoleur = new Panel(new GridLayout(1));
        panelVoleur.addComponent(new Label("Voleur :"));
        panelVoleur.addComponent(new Label("Points de vie : 90"));
        panelVoleur.addComponent(new Label("Dégâts : 35"));
        panelVoleur.addComponent(new Label("Résistance : 15"));
        panelVoleur.addComponent(new Label("Spécialité : Gagne 2x plus d'argent"));
        panelPersonnages.addComponent(panelVoleur);

        // Golem
        Panel panelGolem = new Panel(new GridLayout(1));
        panelGolem.addComponent(new Label("Golem :"));
        panelGolem.addComponent(new Label("Points de vie : 120"));
        panelGolem.addComponent(new Label("Dégâts : 18"));
        panelGolem.addComponent(new Label("Résistance : 50"));
        panelGolem.addComponent(new Label("Spécialité : Grande résistance aux dégâts"));
        panelPersonnages.addComponent(panelGolem);

        panel.addComponent(panelPersonnages);

        // Boutons de sélection
        Panel panelBoutons = new Panel(new GridLayout(3));
        panelBoutons.addComponent(new Button("Guerrier", () -> finaliserInitialisation(pseudo, nomRoyaumeBox.getText(), new Guerrier())));
        panelBoutons.addComponent(new Button("Voleur", () -> finaliserInitialisation(pseudo, nomRoyaumeBox.getText(), new Voleur())));
        panelBoutons.addComponent(new Button("Golem", () -> finaliserInitialisation(pseudo, nomRoyaumeBox.getText(), new Golem())));
        
        panel.addComponent(panelBoutons);

        fenetreInit.setComponent(panel);
        textGUI.addWindowAndWait(fenetreInit);
    }

    private void finaliserInitialisation(String pseudo, String nomRoyaume, Personnage personnage) {
        if (nomRoyaume.isEmpty()) {
            afficherMessageErreur("Veuillez donner un nom à votre royaume");
            return;
        }

        try {
            // Créer le royaume
            Royaume royaume = new Royaume(0, nomRoyaume, 1);
            
            // Mettre à jour le joueur avec le royaume et le personnage
            serviceAuthentification.initialiserJoueur(pseudo, royaume, personnage);
            
            // Afficher un message de succès
            new MessageDialogBuilder()
                .setTitle("Initialisation réussie")
                .setText("Votre compte a été initialisé avec succès !\n" +
                        "Royaume : " + nomRoyaume + "\n" +
                        "Personnage : " + personnage.getNom())
                .addButton(MessageDialogButton.OK)
                .build()
                .showDialog(textGUI);

            // Retourner à l'écran d'authentification
            ecranAuthentification.afficher();
        } catch (Exception e) {
            afficherMessageErreur("Erreur lors de l'initialisation : " + e.getMessage());
        }
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