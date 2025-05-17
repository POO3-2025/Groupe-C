package be.helha.projects.GuerreDesRoyaumes.TUI.Ecran;

import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Coffre;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Slot;
import be.helha.projects.GuerreDesRoyaumes.Reseau.GestionnaireReseau;
import be.helha.projects.GuerreDesRoyaumes.Reseau.MessageCombat;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceCombat;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.screen.Screen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class EcranPreparationCombat {
    private final JoueurDAO joueurDAO;
    private final WindowBasedTextGUI textGUI;
    private final Screen screen;
    private final String pseudoJoueur;
    private final String pseudoAdversaire;
    private final ServiceCombat serviceCombat;
    private final GestionnaireReseau gestionnaireReseau;
    private boolean estHote;
    private List<Item> itemsSelectionnes;

    public EcranPreparationCombat(JoueurDAO joueurDAO, WindowBasedTextGUI textGUI, Screen screen,
                                  String pseudoJoueur, String pseudoAdversaire, ServiceCombat serviceCombat) {
        this.joueurDAO = joueurDAO;
        this.textGUI = textGUI;
        this.screen = screen;
        this.pseudoJoueur = pseudoJoueur;
        this.pseudoAdversaire = pseudoAdversaire;
        this.serviceCombat = serviceCombat;
        this.gestionnaireReseau = new GestionnaireReseau();

        // Déterminer si ce joueur est l'hôte (par exemple, celui qui a initié le combat)
        // Cette logique peut être ajustée selon votre système
        this.estHote = pseudoJoueur.compareTo(pseudoAdversaire) < 0;
        this.itemsSelectionnes = new ArrayList<>();
    }

    public void afficher() {
        Window fenetre = new BasicWindow("Préparation au combat");
        fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(1));

        panel.addComponent(new Label("Préparation au combat contre: " + pseudoAdversaire));
        panel.addComponent(new EmptySpace());

        // Tentative d'établir la connexion réseau
        panel.addComponent(new Label("Établissement de la connexion..."));

        // Établir la connexion en fonction de si le joueur est l'hôte ou non
        boolean connexionReussie = false;

        if (estHote) {
            panel.addComponent(new Label("Vous êtes l'hôte - En attente de connexion..."));
            connexionReussie = gestionnaireReseau.demarrerEnTantQuHote();
        } else {
            // Récupérer l'adresse IP de l'adversaire (pourrait être stockée dans la base de données)
            String adresseIPAdversaire = "127.0.0.1"; // Par défaut localhost pour le développement
            panel.addComponent(new Label("Connexion au serveur de l'adversaire..."));
            connexionReussie = gestionnaireReseau.connecterAuServeur(adresseIPAdversaire);
        }

        // Vérifier si la connexion a réussi
        if (!connexionReussie) {
            panel.addComponent(new Label("Échec de la connexion réseau!"));
            panel.addComponent(new Button("Retour", () -> {
                fenetre.close();
                new EcranSelectionAdversaire(joueurDAO, textGUI, screen, pseudoJoueur, serviceCombat).afficher();
            }));

            fenetre.setComponent(panel);
            textGUI.addWindowAndWait(fenetre);
            return;
        }

        panel.addComponent(new Label("Connexion établie avec succès!"));

        // Récupérer les informations des joueurs
        try {
            Joueur joueur = joueurDAO.obtenirJoueurParPseudo(pseudoJoueur);
            Joueur adversaire = joueurDAO.obtenirJoueurParPseudo(pseudoAdversaire);

            if (joueur == null || adversaire == null) {
                throw new RuntimeException("Impossible de récupérer les informations des joueurs");
            }

            // Initialiser le combat
            serviceCombat.initialiserCombat(joueur, adversaire, new ArrayList<>());

            // En mode développement, attendre la confirmation de l'utilisateur
            panel.addComponent(new Label("Préparation complète!"));
            panel.addComponent(new Label("Attendez que l'adversaire confirme..."));

            // Simuler une attente pour la confirmation
            // Dans une implémentation réelle, on attendrait un message réseau
            Panel panelTemporaire = new Panel(new GridLayout(1));
            Button btnPretSimule = new Button("Simuler adversaire prêt", () -> {
                fenetre.close();
                Window fenetreConfirmation = new BasicWindow("Confirmation");
                fenetreConfirmation.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

                Panel panelConfirmation = new Panel(new GridLayout(1));
                panelConfirmation.addComponent(new Label("L'adversaire est prêt!"));
                panelConfirmation.addComponent(new EmptySpace());
                panelConfirmation.addComponent(new Button("Commencer le combat", () -> {
                    fenetreConfirmation.close();
                    new EcranCombat(joueurDAO, textGUI, screen, joueur, adversaire, serviceCombat).afficher();
                }));

                fenetreConfirmation.setComponent(panelConfirmation);
                textGUI.addWindowAndWait(fenetreConfirmation);
            });

            panelTemporaire.addComponent(btnPretSimule);
            panel.addComponent(panelTemporaire);

            // Bouton de retour
            panel.addComponent(new Button("Annuler", () -> {
                fenetre.close();
                new EcranSelectionAdversaire(joueurDAO, textGUI, screen, pseudoJoueur, serviceCombat).afficher();
            }));

        } catch (Exception e) {
            panel.addComponent(new Label("Erreur: " + e.getMessage()));
            panel.addComponent(new Button("Retour", () -> {
                fenetre.close();
                new EcranSelectionAdversaire(joueurDAO, textGUI, screen, pseudoJoueur, serviceCombat).afficher();
            }));
        }

        fenetre.setComponent(panel);
        textGUI.addWindowAndWait(fenetre);
    }

    private void afficherMessageErreur(String message) {
        new MessageDialogBuilder()
                .setTitle("Information")
                .setText(message)
                .addButton(MessageDialogButton.OK)
                .build()
                .showDialog(textGUI);
    }
}
