package be.helha.projects.GuerreDesRoyaumes.ServiceImpl;

import be.helha.projects.GuerreDesRoyaumes.Model.Combat.Combat;
import be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat.Competence;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Model.Combat.ActionTour;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceCombat;
import org.springframework.stereotype.Service;

@Service
public class ServiceCombatImpl implements ServiceCombat {

    private Combat combatEnCours;


    @Override
    public void initialiserCombat() {
        // Logique d'initialisation du combat
        System.out.println("Initialisation du combat...");
    }


    @Override
    public void executerTour(ActionTour action) {
        // Logique d'exécution du tour
    }


    @Override
    public void demarrerCombat() {
        System.out.println("Debut du combat !");
        // Logique pour démarrer le combat
    }

    @Override
    public void gererSelectionInventaire() {
        System.out.println("Selection de l'inventaire.");
        // Logique pour gérer l'inventaire
    }

    @Override
    public void gererAchatCompetence() {
        System.out.println("Achat de compétence.");

        // Logique pour acheter une compétence
    }

    @Override
    public void utiliserCompetence(Joueur joueur, Competence competence) {
        System.out.println(joueur.getPseudo() + "Utilisation de la compétence : " + competence.getNom());
        // Appliquer l'effet de la compétence
        competence.appliquerEffet(joueur.getPersonnage());
    }

    @Override
    public void gererSelectionAttaque() {
        System.out.println("Selection de l'attaque.");
        // Logique pour gérer la sélection d'attaque

    }

    @Override
    public void gererSelectionDefense() {
        System.out.println("Selection de la défense.");
        // Logique pour gérer la sélection de défense

    }

    @Override
    public void resoudreTour() {
        System.out.println("Résolution du tour.");
        // Résoudre les actions du tour
    }

    @Override
    public void distribuerRecompenses(int combatId) {
        System.out.println("Distribution des récompenses pour le combat ID : " + combatId);
        // Logique pour distribuer les récompenses

    }

    @Override
    public void gererInputUtilisateur() {
        System.out.println("Gestion de l'entrée utilisateur.");
        // Logique pour gérer l'entrée utilisateur

    }

    public String getStatutCombat() {
        return combatEnCours != null ?
                "Tour " + combatEnCours.getNombreTours() + "/5\n" +
                        combatEnCours.getJoueur1().getPseudo() + ": " +
                        combatEnCours.getJoueur1().getPersonnage().getVie() + " PV\n" +
                        combatEnCours.getJoueur2().getPseudo() + ": " +
                        combatEnCours.getJoueur2().getPersonnage().getVie() + " PV" :
                "Aucun combat en cours";
    }
}


