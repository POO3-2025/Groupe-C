package be.helha.projects.GuerreDesRoyaumes.Model.Bot;

import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;

import java.util.Random;

/**
 * AdvancedBot représente une intelligence artificielle basique
 * capable de prendre des décisions de combat en fonction de
 * la situation actuelle du jeu.
 */
public class AdvancedBot {
    private final Random random = new Random();
    private final Joueur botJoueur;
    private int tour = 1;
    private int derniersDegatsCalcules = 0; // Stocke les derniers dégâts calculés pour la cohérence
    
    public AdvancedBot(Joueur botJoueur) {
        this.botJoueur = botJoueur;
    }
    
    /**
     * Choisit une action de combat en fonction de l'état actuel du jeu
     * 
     * @param adversaire Le joueur humain adverse
     * @param tourActuel Le tour actuel du combat
     * @return L'action choisie par le bot ("attaque", "defense", ou "special")
     */
    public String choisirAction(Joueur adversaire, int tourActuel) {
        this.tour = tourActuel;
        
        Personnage botPerso = botJoueur.getPersonnage();
        Personnage adversairePerso = adversaire.getPersonnage();
        
        double pvBot = botPerso.getPointsDeVie();
        double pvAdversaire = adversairePerso.getPointsDeVie();
        
        // Stratégie basée sur les points de vie restants
        if (pvBot < 30) {
            // En danger : 60% défense, 30% attaque, 10% spécial
            int choix = random.nextInt(100);
            if (choix < 60) {
                return "defense";
            } else if (choix < 90) {
                return "attaque";
            } else {
                return "special";
            }
        } else if (pvAdversaire < 30) {
            // Adversaire faible : 70% attaque, 20% spécial, 10% défense
            int choix = random.nextInt(100);
            if (choix < 70) {
                return "attaque";
            } else if (choix < 90) {
                return "special";
            } else {
                return "defense";
            }
        } else {
            // Situation normale : 50% attaque, 30% défense, 20% spécial
            int choix = random.nextInt(100);
            if (choix < 50) {
                return "attaque";
            } else if (choix < 80) {
                return "defense";
            } else {
                return "special";
            }
        }
    }
    
    /**
     * Simule l'exécution d'une action choisie par le bot
     * 
     * @param actionChoisie L'action que le bot va exécuter
     * @param adversaireAction L'action choisie par le joueur adversaire (pour la réaction)
     * @return Message descriptif de l'action du bot
     */
    public String executerAction(String actionChoisie, String adversaireAction) {
        StringBuilder message = new StringBuilder();
        
        message.append("Le bot a choisi: ").append(actionChoisie).append("\n");
        
        // Utiliser les derniers dégâts calculés pour assurer la cohérence
        switch (actionChoisie) {
            case "attaque":
                message.append("Il attaque et inflige ").append(derniersDegatsCalcules).append(" points de dégâts");
                
                if ("defense".equals(adversaireAction)) {
                    message.append(" (réduits de moitié grâce à votre défense)");
                }
                break;
                
            case "defense":
                message.append("Il se met en posture défensive, réduisant les dégâts reçus");
                break;
                
            case "special":
                message.append("Il utilise sa compétence spéciale et inflige ").append(derniersDegatsCalcules).append(" points de dégâts");
                
                if ("defense".equals(adversaireAction)) {
                    message.append(" (réduits de moitié grâce à votre défense)");
                }
                break;
                
            default:
                message.append("Action inconnue");
        }
        
        return message.toString();
    }
    
    /**
     * Calcule et retourne les dégâts causés par l'action du bot
     * 
     * @param actionChoisie L'action choisie par le bot
     * @return Les dégâts bruts (avant réduction éventuelle par défense)
     */
    public int calculerDegatsAction(String actionChoisie) {
        Personnage botPerso = botJoueur.getPersonnage();
        
        // Utiliser les statistiques du personnage bot pour le calcul des dégâts
        double degatsBase = botPerso.getDegats();
        
        System.out.println("Statistiques du bot " + botPerso.getNom() + ": " +
                "Vie=" + botPerso.getVie() + ", " +
                "Dégâts=" + botPerso.getDegats() + ", " +
                "Résistance=" + botPerso.getResistance());
        
        switch (actionChoisie) {
            case "attaque":
                // Attaque normale basée sur les dégâts du personnage
                derniersDegatsCalcules = calculerDegatsAvecStatistiques(degatsBase, 1.0);
                return derniersDegatsCalcules;
                
            case "special":
                // Attaque spéciale avec bonus de 50%
                derniersDegatsCalcules = calculerDegatsAvecStatistiques(degatsBase, 1.5);
                return derniersDegatsCalcules;
                
            default:
                derniersDegatsCalcules = 0;
                return 0; // Pas de dégâts pour défense ou action inconnue
        }
    }
    
    /**
     * Calcule des dégâts en fonction des statistiques et d'un multiplicateur
     */
    private int calculerDegatsAvecStatistiques(double degatsBase, double multiplicateur) {
        // Appliquer le multiplicateur aux dégâts de base
        double degats = degatsBase * multiplicateur;
        
        // Supprimer la variation aléatoire et utiliser les dégâts exacts
        // double variation = 0.95 + (random.nextDouble() * 0.1); // Entre 0.95 et 1.05
        // degats = degats * variation;
        
        // Convertir en entier sans arrondi
        return Math.max(1, (int)degats);
    }
    
    /**
     * Simule un délai de réponse du bot pour plus de réalisme
     */
    public void simulerDelaiReflexion() {
        try {
            // Délai aléatoire entre 0.5 et 2 secondes
            Thread.sleep(random.nextInt(1500) + 500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
} 