package be.helha.projects.GuerreDesRoyaumes.TUI.Ecran;

import be.helha.projects.GuerreDesRoyaumes.Controller.CombatController;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Coffre;
import be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat.Competence;
import com.googlecode.lanterna.gui2.*;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Slot;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;

import java.util.ArrayList;
import java.util.List;

public class EcranPreparationCombat {
    private final CombatController combatController;
    private final WindowBasedTextGUI textGUI;
    private List<Slot> selectedItems;  // Liste des items sélectionnés
    private int retryCount = 0;        // Compteur de tentatives de sélection (max 2 tentatives)

    public EcranPreparationCombat(CombatController combatController, WindowBasedTextGUI textGUI) {
        this.combatController = combatController;
        this.textGUI = textGUI;
        this.selectedItems = new ArrayList<>();
    }

    // Méthode pour afficher l'écran de préparation
    public void afficher() {
        Window window = new BasicWindow("Préparation au combat");
        Panel panel = new Panel(new GridLayout(2));

        // Affichage du coffre du joueur actuel
        panel.addComponent(new Label("Sélectionnez 5 items pour le combat"));

        // Affichage du coffre
        Coffre coffre = combatController.getJoueurActuel().getCoffre();
        coffre.getSlots().forEach(slot -> {
            panel.addComponent(new Label(slot.toString()));
            panel.addComponent(new Button("Sélectionner", () -> {
                if (slot.getQuantity() > 0 && selectedItems.size() < 5) {
                    selectedItems.add(slot);
                    combatController.transfererItemsCoffreVersInventaire(combatController.getJoueurActuel(), slot.getItem(), 1);
                    afficher(); // Rafraîchir l'écran
                } else {
                    afficherMessageErreur("Item épuisé, sélection impossible.");
                }
            }));
        });


        // Bouton pour confirmer ou recommencer la sélection
        panel.addComponent(new Button("Confirmer", () -> {
            if (selectedItems.size() == 5) {
                afficherCompetencesBonus();
            } else {
                afficherMessageErreur("Vous devez sélectionner 5 items.");
            }
        }));

        // Bouton pour recommencer la sélection
        panel.addComponent(new Button("Recommencer", () -> {
            if (retryCount < 2) {
                retryCount++;
                selectedItems.clear();  // Réinitialise la sélection des items
                afficher();  // Rafraîchit l'écran pour recommencer
            } else {
                afficherMessageErreur("Vous avez atteint le nombre maximum de tentatives.");
            }
        }));

        window.setComponent(panel);
        textGUI.addWindowAndWait(window);
    }

    // Afficher un message d'erreur
    private void afficherMessageErreur(String message) {
        new MessageDialogBuilder()
                .setTitle("Erreur")
                .setText(message)
                .addButton(MessageDialogButton.OK)
                .build()
                .showDialog(textGUI);
    }

    // Afficher les compétences bonus à acheter
    private void afficherCompetencesBonus() {
        Window window = new BasicWindow("Sélection des compétences bonus");
        Panel panel = new Panel(new GridLayout(2));

        // Afficher les compétences disponibles
        List<Competence> competencesBonus = combatController.getCompetencesBonusDisponibles();
        competencesBonus.forEach(competence -> {
            panel.addComponent(new Label(competence.getNom() + " : " + competence.getDescription()));
            panel.addComponent(new Button("Acheter", () -> {
                combatController.acheterCompetence(competence);
            }));
        });

        // Bouton pour passer cette étape
        panel.addComponent(new Button("Skip", () -> {
            commencerCombat();  // Passe à l'écran de combat
        }));

        window.setComponent(panel);
        textGUI.addWindowAndWait(window);
    }

    // Démarre le combat
    private void commencerCombat() {
        new EcranCombat(
                combatController,
                textGUI,
                combatController.getJoueurActuel()
        ).afficherInterfaceCombat();
    }
}
