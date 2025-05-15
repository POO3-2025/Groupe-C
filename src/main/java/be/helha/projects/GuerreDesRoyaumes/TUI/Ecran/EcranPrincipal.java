package be.helha.projects.GuerreDesRoyaumes.TUI.Ecran;

import be.helha.projects.GuerreDesRoyaumes.Config.ConnexionManager;
import be.helha.projects.GuerreDesRoyaumes.Config.SQLConfigManager;
import be.helha.projects.GuerreDesRoyaumes.Controller.CombatController;
import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.CombatDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceAuthentification;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceCombat;
import be.helha.projects.GuerreDesRoyaumes.ServiceImpl.ServiceCombatImpl;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.screen.Screen;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

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
            try {
                afficherEcranCombat(joueur);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
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

    private void afficherEcranCombat(Joueur joueur) throws SQLException {
        // Vérifier si le joueur a un personnage
        if (joueur.getPersonnage() == null) {
            afficherMessageErreur("Vous devez d'abord choisir un personnage avant de combattre.");
            return;
        }

        // Obtenir la liste des joueurs disponibles pour le combat
        List<Joueur> adversairesPotentiels = joueurDAO.obtenirTousLesJoueurs();
        adversairesPotentiels.removeIf(j ->
                j.getPseudo().equals(joueur.getPseudo()) || // Enlever le joueur actuel
                        j.getPersonnage() == null // Enlever les joueurs sans personnage
        );

        if (adversairesPotentiels.isEmpty()) {
            afficherMessageErreur("Aucun adversaire disponible pour le combat. Assurez-vous qu'il y a au moins un autre joueur avec un personnage.");
            return;
        }

        // Créer une fenêtre pour choisir l'adversaire
        Window fenetreAdversaire = new BasicWindow("Choisir un adversaire");
        Panel panelAdversaire = new Panel(new GridLayout(1));
        panelAdversaire.addComponent(new Label("Choisissez votre adversaire :"));

        // Ajouter un bouton pour chaque adversaire potentiel
        for (Joueur adversaire : adversairesPotentiels) {
            Button boutonAdversaire = new Button(adversaire.getPseudo() + " (" + adversaire.getPersonnage().getNom() + ")", () -> {
                fenetreAdversaire.close();
                demarrerCombat(joueur, adversaire);
            });
            panelAdversaire.addComponent(boutonAdversaire);
        }

        fenetreAdversaire.setComponent(panelAdversaire);
        textGUI.addWindowAndWait(fenetreAdversaire);
    }

    private void demarrerCombat(Joueur joueur, Joueur adversaire) {
        // Initialisation du service de combat
        ServiceCombat serviceCombat = new ServiceCombatImpl();

        // Création et configuration du DAO
        CombatDAOImpl combatDAO = new CombatDAOImpl();

        try {
            // Obtenir une connexion directe via ConnexionManager
            Connection connection = ConnexionManager.getInstance().getSQLConnection();
            combatDAO.setConnection(connection);
            System.out.println("Connexion SQL établie pour le combat via ConnexionManager");
        } catch (SQLException e) {
            afficherMessageErreur("Erreur de connexion à la base de données: " + e.getMessage());
            return;
        }

        // Initialisation du CombatController avec les deux joueurs
        CombatController combatController = new CombatController(
                serviceCombat,
                combatDAO,
                joueur,
                adversaire
        );

        try {
            combatController.initialiserCombat();
        } catch (IllegalStateException e) {
            afficherMessageErreur("Erreur lors de l'initialisation du combat : " + e.getMessage());
            return;
        }

        // Vérifier si le combat est correctement initialisé
        if (combatController.getCombatEnCours() == null) {
            afficherMessageErreur("Erreur lors de l'initialisation du combat");
            return;
        }

        // Afficher l'écran de préparation au combat
        EcranPreparationCombat ecranPreparationCombat = new EcranPreparationCombat(combatController, textGUI);
        ecranPreparationCombat.afficher();
    }

    private void afficherMessageErreur(String message) {
        MessageDialogBuilder dialogBuilder = new MessageDialogBuilder()
                .setTitle("Erreur")
                .setText(message)
                .addButton(MessageDialogButton.OK);
        dialogBuilder.build().showDialog(textGUI);
    }

}