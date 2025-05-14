package be.helha.projects.GuerreDesRoyaumes.Service;

import be.helha.projects.GuerreDesRoyaumes.Model.Combat.ActionTour;
import be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat.Competence;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ServiceCombat {
    // Etape 1 : Initialisation
    void initialiserCombat();
    void demarrerCombat();
    void executerTour(ActionTour actionTour);

    // Etape 2 : Inventaire
    void gererSelectionInventaire();

    // Etape 3 : Competence
    void gererAchatCompetence();

    // Etape 4 : Tour par tour
    void utiliserCompetence(Joueur joueur, Competence competence);
    void gererSelectionAttaque();
    void gererSelectionDefense();

    // Etape 5 : Calculs et resultats
    void resoudreTour();
    void distribuerRecompenses(int combatId);

    // Etape 6 : Entree utilisateur
    void gererInputUtilisateur();

    String getStatutCombat();
}
