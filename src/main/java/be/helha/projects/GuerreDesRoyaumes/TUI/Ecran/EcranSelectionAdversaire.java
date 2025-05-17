package be.helha.projects.GuerreDesRoyaumes.TUI.Ecran;

import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceCombat;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.screen.Screen;

import java.util.ArrayList;
import java.util.List;

public class EcranSelectionAdversaire {
    private final JoueurDAO joueurDAO;
    private final WindowBasedTextGUI textGUI;
    private final Screen screen;
    private final String pseudoJoueur;
    private final ServiceCombat serviceCombat;

    public EcranSelectionAdversaire(JoueurDAO joueurDAO, WindowBasedTextGUI textGUI, Screen screen, String pseudoJoueur, ServiceCombat serviceCombat) {
        this.joueurDAO = joueurDAO;
        this.textGUI = textGUI;
        this.screen = screen;
        this.pseudoJoueur = pseudoJoueur;
        this.serviceCombat = serviceCombat;
    }

    public void afficher() {
        Window fenetre = new BasicWindow("Sélection d'adversaire");
        fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(1));

        // Titre
        panel.addComponent(new Label("Sélectionnez un adversaire connecté:"));

        try {
            // Obtenir la liste des joueurs actifs
            List<Joueur> joueursActifs = joueurDAO.obtenirJoueursActifs();
            List<String> pseudosActifs = new ArrayList<>();

            if (joueursActifs == null) {
                afficherMessageErreur("Erreur lors de la récupération des joueurs actifs");
                return;
            }

            // Filtrer pour ne pas inclure le joueur actuel
            for (Joueur j : joueursActifs) {
                if (j != null && j.getPseudo() != null && !j.getPseudo().equals(pseudoJoueur)) {
                    pseudosActifs.add(j.getPseudo());
                }
            }

            if (pseudosActifs.isEmpty()) {
                panel.addComponent(new Label("Aucun adversaire disponible pour le moment."));
            } else {
                // Créer une liste déroulante avec les pseudos des joueurs actifs
                ComboBox<String> comboJoueurs = new ComboBox<>(pseudosActifs);
                panel.addComponent(comboJoueurs);

                // Bouton de sélection
                panel.addComponent(new Button("Sélectionner", () -> {
                    String pseudoAdversaire = comboJoueurs.getSelectedItem();

                    if (pseudoAdversaire == null || pseudoAdversaire.isEmpty()) {
                        afficherMessageErreur("Veuillez sélectionner un adversaire");
                        return;
                    }

                    try {
                        Joueur adversaire = joueurDAO.obtenirJoueurParPseudo(pseudoAdversaire);

                        if (adversaire == null) {
                            afficherMessageErreur("Joueur introuvable");
                            return;
                        }

                        // Si l'adversaire est trouvé, on peut passer à l'écran de préparation de combat
                        fenetre.close();
                        new EcranPreparationCombat(joueurDAO, textGUI, screen, pseudoJoueur, pseudoAdversaire, serviceCombat).afficher();

                    } catch (Exception e) {
                        afficherMessageErreur("Erreur lors de la sélection: " + e.getMessage());
                    }
                }));
            }
        } catch (Exception e) {
            panel.addComponent(new Label("Erreur lors de la récupération des adversaires: " + e.getMessage()));
        }

        // Bouton retour
        panel.addComponent(new Button("Retour", () -> {
            fenetre.close();
            try {
                Joueur joueur = joueurDAO.obtenirJoueurParPseudo(pseudoJoueur);
                new EcranPrincipal(null, joueurDAO, pseudoJoueur, screen).afficher();
            } catch (Exception e) {
                afficherMessageErreur("Erreur lors du retour au menu principal: " + e.getMessage());
            }
        }));

        fenetre.setComponent(panel);
        textGUI.addWindowAndWait(fenetre);
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