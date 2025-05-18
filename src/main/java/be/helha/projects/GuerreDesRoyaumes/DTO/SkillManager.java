package be.helha.projects.GuerreDesRoyaumes.DTO;

import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import org.springframework.stereotype.Component;

@Component
public class SkillManager {
    // Gère l'utilisation des compétences achetées
    public boolean peutUtiliserCompetence(Joueur joueur, String competenceId) {
        // Vérifier si le joueur peut utiliser la compétence
        if (joueur == null || competenceId == null || competenceId.isEmpty()) {
            return false;
        }
        
        // Implémenter la logique pour vérifier si le joueur possède la compétence
        // et si les conditions pour l'utiliser sont remplies (cooldown, ressources, etc.)
        return true; // Temporairement toujours vrai
    }
    
    public void utiliserCompetence(Joueur joueur, String competenceId) {
        // Utiliser la compétence
        if (joueur == null || competenceId == null || competenceId.isEmpty()) {
            return;
        }
        
        // Implémenter la logique pour utiliser la compétence
        // et appliquer ses effets au joueur ou à sa cible
    }
    
    public void reinitialiserCompetences(Joueur joueur) {
        // Réinitialiser les compétences du joueur
        if (joueur == null) {
            return;
        }
        
        // Implémenter la logique pour réinitialiser les cooldowns, ressources, etc.
    }
}