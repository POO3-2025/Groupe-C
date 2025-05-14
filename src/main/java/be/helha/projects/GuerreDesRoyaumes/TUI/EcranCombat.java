package be.helha.projects.GuerreDesRoyaumes.TUI;

import be.helha.projects.GuerreDesRoyaumes.Controller.CombatController;
import be.helha.projects.GuerreDesRoyaumes.Model.Combat.ActionTour;
import be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat.Competence;
import be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat.DoubleDegats;
import be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat.DoubleResistance;
import be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat.Regeneration;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import com.googlecode.lanterna.gui2.*;

import java.util.List;

public class EcranCombat {

    private final CombatController combatController;
    private final WindowBasedTextGUI textGUI;
    private final Joueur joueurActuel;

    public EcranCombat(CombatController combatController, WindowBasedTextGUI textGUI, Joueur joueurActuel) {
        this.combatController = combatController;
        this.textGUI = textGUI;
        this.joueurActuel = joueurActuel;
    }

    public void afficherInterfaceCombat() {
        Window combatWindow = new BasicWindow("Combat");
        combatWindow.setHints(List.of(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(1));
        panel.addComponent(new Label("Combat en cours..."));
        panel.addComponent(new Label(combatController.getStatutCombat()));
        panel.addComponent(new Label("Combat contre: " + combatController.getCombatEnCours().getJoueur2().getPseudo()));

        Button attackButton = new Button("Attaquer", () -> {
            ActionTour action = new ActionTour(joueurActuel, "Attaque", null);
            combatController.executerTour(action, getActionAdversaire());
            afficherStatutCombat();
        });

        Button defenseButton = new Button("Défendre", () -> {
            ActionTour action = new ActionTour(joueurActuel, "Defense", null);
            combatController.executerTour(action, getActionAdversaire());
            afficherStatutCombat();
        });

        Button competenceButton = new Button("Compétences", this::afficherAchatCompetences);

        panel.addComponent(attackButton);
        panel.addComponent(defenseButton);
        panel.addComponent(competenceButton);

        combatWindow.setComponent(panel);
        textGUI.addWindowAndWait(combatWindow);
    }

    private void afficherStatutCombat() {
        Panel panel = new Panel(new GridLayout(2));
        String statut = combatController.getStatutCombat();
        Label statutLabel = new Label(statut);
        //panel.addComponent(competenceButton);

    }


    private ActionTour getActionAdversaire() {
        // Logique pour obtenir l'action de l'adversaire
        // Exemple d'action par défaut:
        return new ActionTour(combatController.getCombatEnCours().getJoueur2(), "Attaque", null);
    }

    private void afficherAchatCompetences() {
        // Affichage des compétences
        // Par exemple, ajouter un bouton pour chaque compétence
        Button competenceButton1 = new Button("Double Dégâts", () -> acheterCompetence(new DoubleDegats()));
        Button competenceButton2 = new Button("Double Résistance", () -> acheterCompetence(new DoubleResistance()));
        Button competenceButton3 = new Button("Régénération", () -> acheterCompetence(new Regeneration()));

        // Ajoutez ces boutons à l'interface
    }

    private void acheterCompetence(Competence competence) {
        combatController.acheterCompetence(joueurActuel, competence);
    }
}
