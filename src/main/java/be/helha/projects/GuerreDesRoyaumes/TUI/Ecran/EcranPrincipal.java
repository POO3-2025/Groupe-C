package be.helha.projects.GuerreDesRoyaumes.TUI.Ecran;

import be.helha.projects.GuerreDesRoyaumes.DAO.ItemDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.ItemMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Guerrier;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Voleur;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Golem;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceAuthentification;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceBoutique;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceCombat;
import be.helha.projects.GuerreDesRoyaumes.ServiceImpl.ServiceBoutiqueImpl;
import be.helha.projects.GuerreDesRoyaumes.ServiceImpl.ServiceCombatImpl;
import com.googlecode.lanterna.TerminalSize;
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
        panel.addComponent(new Button("Déconnexion", () -> {
            // Déconnecter le joueur
            if (serviceAuthentification != null) {
                serviceAuthentification.deconnecterJoueur(joueur.getPseudo());
            }
            fenetre.close();
        }));

        fenetre.setComponent(panel);
        textGUI.addWindowAndWait(fenetre);
    }

    private void afficherEcranGestionProfil(Joueur joueur) {
        // Implémentation à venir pour la gestion du profil
    }

    private void afficherEcranChoixPersonnage(Joueur joueur) {
        Window fenetre = new BasicWindow("Choix du Personnage");
        fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(1));
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

        panelBoutons.addComponent(new Button("Guerrier", () -> {
            try {
                Personnage guerrier = new Guerrier();
                joueur.setPersonnage(guerrier);
                joueurDAO.mettreAJourJoueur(joueur);
                afficherMessageSucces("Vous avez choisi le personnage Guerrier !");
                fenetre.close();
                afficher(); // Retour au menu principal
            } catch (Exception e) {
                afficherMessageErreur("Erreur lors de la sélection du personnage: " + e.getMessage());
            }
        }));

        panelBoutons.addComponent(new Button("Voleur", () -> {
            try {
                Personnage voleur = new Voleur();
                joueur.setPersonnage(voleur);
                joueurDAO.mettreAJourJoueur(joueur);
                afficherMessageSucces("Vous avez choisi le personnage Voleur !");
                fenetre.close();
                afficher(); // Retour au menu principal
            } catch (Exception e) {
                afficherMessageErreur("Erreur lors de la sélection du personnage: " + e.getMessage());
            }
        }));

        panelBoutons.addComponent(new Button("Golem", () -> {
            try {
                Personnage golem = new Golem();
                joueur.setPersonnage(golem);
                joueurDAO.mettreAJourJoueur(joueur);
                afficherMessageSucces("Vous avez choisi le personnage Golem !");
                fenetre.close();
                afficher(); // Retour au menu principal
            } catch (Exception e) {
                afficherMessageErreur("Erreur lors de la sélection du personnage: " + e.getMessage());
            }
        }));

        panel.addComponent(panelBoutons);

        // Bouton retour
        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Retour", () -> {
            fenetre.close();
            afficher(); // Retour au menu principal
        }));

        fenetre.setComponent(panel);
        textGUI.addWindowAndWait(fenetre);
    }

    private void afficherEcranGestionInventaire(Joueur joueur) {
        try {
            EcranGestionInventaire ecranGestionInventaire = new EcranGestionInventaire(joueur, screen);
            ecranGestionInventaire.afficher();
        } catch (Exception e) {
            System.err.println("Erreur lors de l'affichage de l'écran de gestion d'inventaire: " + e.getMessage());
            e.printStackTrace();
            afficherMessageErreur("Erreur lors de l'affichage de l'écran de gestion d'inventaire");
        }
    }

    public void afficherEcranBoutique(Joueur joueur) {
        try {
            // Utiliser ItemMongoDAOImpl pour la gestion des items
            ItemDAO itemDAO = ItemMongoDAOImpl.getInstance();
            ServiceBoutique serviceBoutique = ServiceBoutiqueImpl.getInstance();

            EcranBoutique ecranBoutique = new EcranBoutique(serviceBoutique, itemDAO, joueur, screen);
            ecranBoutique.afficher();
        } catch (Exception e) {
            System.err.println("Erreur lors de l'affichage de l'écran boutique: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void afficherEcranGestionRoyaume(Joueur joueur) {
        // Implémentation à venir pour la gestion du royaume
    }

    private void afficherEcranCombat(Joueur joueur) {
        // Vérifier si le joueur a un personnage
        if (joueur.getPersonnage() == null) {
            afficherMessageErreur("Vous devez d'abord choisir un personnage avant de combattre.");
            return;
        }

        try {
            // Initialisation du service de combat
            ServiceCombat serviceCombat = new ServiceCombatImpl(joueurDAO);

            // Afficher l'écran de sélection d'adversaire
            new EcranSelectionAdversaire(joueurDAO, textGUI, screen, joueur.getPseudo(), serviceCombat).afficher();

        } catch (Exception e) {
            afficherMessageErreur("Erreur lors de l'initialisation du combat: " + e.getMessage());
        }
    }

    private void afficherMessageErreur(String message) {
        MessageDialogBuilder dialogBuilder = new MessageDialogBuilder()
                .setTitle("Erreur")
                .setText(message)
                .addButton(MessageDialogButton.OK);
        dialogBuilder.build().showDialog(textGUI);
    }

    private void afficherMessageSucces(String message) {
        MessageDialogBuilder dialogBuilder = new MessageDialogBuilder()
                .setTitle("Succès")
                .setText(message)
                .addButton(MessageDialogButton.OK);
        dialogBuilder.build().showDialog(textGUI);
    }

}