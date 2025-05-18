package be.helha.projects.GuerreDesRoyaumes.DTO;

import be.helha.projects.GuerreDesRoyaumes.DAO.CombatSessionMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.CombatSessionMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Model.Combat.CombatSession;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Arme;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Bouclier;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Potion;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Classe responsable de la résolution des actions de combat.
 * Cette classe calcule les résultats des actions des joueurs et met à jour les points de vie.
 */
@Component
public class CombatResolver {

    private final JoueurDAO joueurDAO;
    private final CombatSessionMongoDAO sessionDAO;
    private final Random random = new Random();
    
    @Autowired
    public CombatResolver(JoueurDAO joueurDAO, CombatSessionMongoDAO sessionDAO) {
        this.joueurDAO = joueurDAO;
        this.sessionDAO = sessionDAO;
    }
    
    /**
     * Résout les actions des joueurs pour un tour spécifique.
     * 
     * @param combatId L'identifiant de la session de combat
     * @param tour Le numéro du tour
     * @return true si la résolution a réussi, false sinon
     */
    public boolean resoudreActions(String combatId, int tour) {
        try {
            // Charger la session de combat
            CombatSession session = sessionDAO.chargerSession(combatId);
            
            if (session == null) {
                System.err.println("Session de combat non trouvée: " + combatId);
                return false;
            }
            
            // Vérifier si les deux joueurs ont soumis leurs actions
            if (!sessionDAO.actionsCompletes(combatId, tour)) {
                System.err.println("Les actions ne sont pas complètes pour le tour " + tour);
                return false;
            }
            
            // Récupérer les joueurs et leurs actions
            Joueur joueur1 = joueurDAO.obtenirJoueurParId(session.getJoueur1Id());
            Joueur joueur2 = joueurDAO.obtenirJoueurParId(session.getJoueur2Id());
            
            if (joueur1 == null || joueur2 == null) {
                System.err.println("Joueurs non trouvés: " + session.getJoueur1Id() + ", " + session.getJoueur2Id());
                return false;
            }
            
            Map<Integer, Map<String, Object>> actions = sessionDAO.obtenirActions(combatId, tour);
            
            if (actions.isEmpty()) {
                System.err.println("Aucune action trouvée pour le tour " + tour);
                return false;
            }
            
            // Calculer les résultats
            Map<String, Object> resultats = calculerResultats(session, joueur1, joueur2, actions, tour);
            
            // Sauvegarder les résultats
            return sessionDAO.sauvegarderResultatTour(combatId, tour, resultats);
        } catch (Exception e) {
            System.err.println("Erreur lors de la résolution des actions: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private Map<String, Object> calculerResultats(CombatSession session, Joueur joueur1, Joueur joueur2, 
                                                 Map<Integer, Map<String, Object>> actions, int tour) {
        Map<String, Object> resultats = new HashMap<>();
        
        // Récupérer les actions des joueurs
        Map<String, Object> actionJoueur1 = actions.getOrDefault(joueur1.getId(), new HashMap<>());
        Map<String, Object> actionJoueur2 = actions.getOrDefault(joueur2.getId(), new HashMap<>());
        
        String typeActionJ1 = (String) actionJoueur1.getOrDefault("typeAction", "");
        String typeActionJ2 = (String) actionJoueur2.getOrDefault("typeAction", "");
        
        // Points de vie initiaux
        double pvJ1 = session.getPvJoueur1();
        double pvJ2 = session.getPvJoueur2();
        
        resultats.put("tour", tour);
        resultats.put("actionJoueur1", typeActionJ1);
        resultats.put("actionJoueur2", typeActionJ2);
        
        // Initialiser les variables pour les résultats
        double degatsJ1 = 0;
        double degatsJ2 = 0;
        double defenseBonusJ1 = 0;
        double defenseBonusJ2 = 0;
        double healJ1 = 0;
        double healJ2 = 0;
        
        // Traiter les actions de défense d'abord (priorité la plus élevée)
        if ("defense".equals(typeActionJ1)) {
            defenseBonusJ1 = calculerBonusDefense(joueur1);
            resultats.put("defenseBonusJoueur1", defenseBonusJ1);
        }
        
        if ("defense".equals(typeActionJ2)) {
            defenseBonusJ2 = calculerBonusDefense(joueur2);
            resultats.put("defenseBonusJoueur2", defenseBonusJ2);
        }
        
        // Traiter les items et compétences spéciales ensuite
        if ("special".equals(typeActionJ1)) {
            Map<String, Double> resultatSpecial = executerCompetenceSpeciale(joueur1, joueur2);
            degatsJ1 += resultatSpecial.getOrDefault("degats", 0.0);
            healJ1 += resultatSpecial.getOrDefault("healing", 0.0);
            resultats.put("competenceJoueur1", resultatSpecial.get("nom"));
        }
        
        if ("special".equals(typeActionJ2)) {
            Map<String, Double> resultatSpecial = executerCompetenceSpeciale(joueur2, joueur1);
            degatsJ2 += resultatSpecial.getOrDefault("degats", 0.0);
            healJ2 += resultatSpecial.getOrDefault("healing", 0.0);
            resultats.put("competenceJoueur2", resultatSpecial.get("nom"));
        }
        
        if ("utiliser_item".equals(typeActionJ1)) {
            int itemId = ((Number) actionJoueur1.getOrDefault("itemId", 0)).intValue();
            Map<String, Object> resultatItem = utiliserItem(joueur1, joueur2, itemId);
            degatsJ1 += ((Number) resultatItem.getOrDefault("degats", 0.0)).doubleValue();
            healJ1 += ((Number) resultatItem.getOrDefault("healing", 0.0)).doubleValue();
            resultats.put("itemJoueur1", resultatItem.get("nom"));
        }
        
        if ("utiliser_item".equals(typeActionJ2)) {
            int itemId = ((Number) actionJoueur2.getOrDefault("itemId", 0)).intValue();
            Map<String, Object> resultatItem = utiliserItem(joueur2, joueur1, itemId);
            degatsJ2 += ((Number) resultatItem.getOrDefault("degats", 0.0)).doubleValue();
            healJ2 += ((Number) resultatItem.getOrDefault("healing", 0.0)).doubleValue();
            resultats.put("itemJoueur2", resultatItem.get("nom"));
        }
        
        // Enfin, traiter les attaques basiques
        if ("attaque".equals(typeActionJ1)) {
            degatsJ1 += calculerDegatsAttaque(joueur1, joueur2);
        }
        
        if ("attaque".equals(typeActionJ2)) {
            degatsJ2 += calculerDegatsAttaque(joueur2, joueur1);
        }
        
        // Appliquer la défense aux dégâts
        if (defenseBonusJ2 > 0) {
            degatsJ1 = Math.max(0, degatsJ1 * (1 - defenseBonusJ2 / 100.0));
        }
        
        if (defenseBonusJ1 > 0) {
            degatsJ2 = Math.max(0, degatsJ2 * (1 - defenseBonusJ1 / 100.0));
        }
        
        // Appliquer les dégâts et les soins
        pvJ1 = Math.min(100, pvJ1 + healJ1 - degatsJ2);
        pvJ2 = Math.min(100, pvJ2 + healJ2 - degatsJ1);
        
        // S'assurer que les PV ne sont pas négatifs
        pvJ1 = Math.max(0, pvJ1);
        pvJ2 = Math.max(0, pvJ2);
        
        // Stocker les résultats finaux
        resultats.put("degatsJoueur1", degatsJ1);
        resultats.put("degatsJoueur2", degatsJ2);
        resultats.put("healingJoueur1", healJ1);
        resultats.put("healingJoueur2", healJ2);
        resultats.put("pvJoueur1", pvJ1);
        resultats.put("pvJoueur2", pvJ2);
        
        // Déterminer si le combat est terminé
        boolean combatTermine = pvJ1 <= 0 || pvJ2 <= 0 || tour >= 5;
        resultats.put("combatTermine", combatTermine);
        
        if (combatTermine) {
            int vainqueurId = 0;
            if (pvJ1 <= 0 && pvJ2 <= 0) {
                // Match nul
                vainqueurId = 0;
            } else if (pvJ1 <= 0) {
                vainqueurId = joueur2.getId();
            } else if (pvJ2 <= 0) {
                vainqueurId = joueur1.getId();
            } else if (pvJ1 > pvJ2) {
                vainqueurId = joueur1.getId();
            } else if (pvJ2 > pvJ1) {
                vainqueurId = joueur2.getId();
            }
            
            resultats.put("vainqueurId", vainqueurId);
        }
        
        return resultats;
    }
    
    private double calculerBonusDefense(Joueur joueur) {
        // Bonus de base pour la défense
        double bonus = 25; // 25% de réduction des dégâts
        
        // Vérifier s'il y a un bouclier équipé
        Personnage personnage = joueur.getPersonnage();
        for (be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Slot slot : personnage.getInventaire().getSlots()) {
            if (slot.getItem() instanceof Bouclier) {
                Bouclier bouclier = (Bouclier) slot.getItem();
                bonus += bouclier.getDefense();
                break;
            }
        }
        
        // Limiter le bonus à 75% maximum
        return Math.min(75, bonus);
    }
    
    private double calculerDegatsAttaque(Joueur attaquant, Joueur defenseur) {
        Personnage persAttaquant = attaquant.getPersonnage();
        Personnage persDefenseur = defenseur.getPersonnage();
        
        // Dégâts de base du personnage
        double degats = persAttaquant.getDegats();
        
        // Vérifier s'il y a une arme équipée
        for (be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Slot slot : persAttaquant.getInventaire().getSlots()) {
            if (slot.getItem() instanceof Arme) {
                Arme arme = (Arme) slot.getItem();
                degats += arme.getDegats();
                break;
            }
        }
        
        // Réduction de base par la défense du personnage défenseur
        double reduction = persDefenseur.getResistance() / 100.0;
        
        // Appliquer la réduction
        degats = degats * (1 - reduction);
        
        // Variation aléatoire de +/- 10%
        double variation = 0.9 + (random.nextDouble() * 0.2); // Entre 0.9 et 1.1
        degats = degats * variation;
        
        return Math.max(1, degats); // Au moins 1 point de dégâts
    }
    
    private Map<String, Double> executerCompetenceSpeciale(Joueur joueur, Joueur adversaire) {
        Map<String, Double> resultat = new HashMap<>();
        
        // Implémenter les différentes compétences selon le type de personnage
        String typePersonnage = joueur.getPersonnage().getClass().getSimpleName();
        
        switch (typePersonnage) {
            case "Golem":
                // Par exemple: Frappe dévastatrice (dégâts élevés)
                resultat.put("degats", 30.0 + (random.nextDouble() * 10.0)); // Entre 30 et 40
                break;
            case "Titan":
                // Par exemple: Onde de choc (dégâts modérés mais ignore une partie de la défense)
                resultat.put("degats", 25.0 + (random.nextDouble() * 5.0)); // Entre 25 et 30
                // Cette attaque ignorerait la défense, mais c'est géré lors de l'application des dégâts
                break;
            default:
                // Compétence générique: Attaque puissante
                resultat.put("degats", 20.0 + (random.nextDouble() * 10.0)); // Entre 20 et 30
                break;
        }
        
        return resultat;
    }
    
    private Map<String, Object> utiliserItem(Joueur joueur, Joueur adversaire, int itemId) {
        Map<String, Object> resultat = new HashMap<>();
        
        // Trouver l'item dans l'inventaire
        Item item = null;
        
        for (be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Slot slot : joueur.getPersonnage().getInventaire().getSlots()) {
            if (slot.getItem() != null && slot.getItem().getId() == itemId) {
                item = slot.getItem();
                break;
            }
        }
        
        if (item == null) {
            resultat.put("nom", "Aucun item trouvé");
            return resultat;
        }
        
        // Utiliser l'item en fonction de son type
        if (item instanceof Potion) {
            Potion potion = (Potion) item;
            resultat.put("nom", potion.getNom());
            
            if (potion.getSoin() > 0) {
                // Potion de soin
                resultat.put("healing", (double) potion.getSoin());
            } else if (potion.getDegats() > 0) {
                // Potion de dégâts
                resultat.put("degats", (double) potion.getDegats());
            } else {
                // Potion à effet aléatoire
                if (random.nextBoolean()) {
                    // Effet positif: soins
                    double valeurSoin = 15 + (random.nextDouble() * 15); // Entre 15 et 30
                    resultat.put("healing", valeurSoin);
                } else {
                    // Effet négatif: dégâts à l'adversaire
                    double valeurDegats = 10 + (random.nextDouble() * 20); // Entre 10 et 30
                    resultat.put("degats", valeurDegats);
                }
            }
        } else if (item instanceof Arme) {
            // Utilisation spéciale d'une arme (attaque critique)
            Arme arme = (Arme) item;
            resultat.put("nom", "Attaque critique avec " + arme.getNom());
            resultat.put("degats", (double) arme.getDegats() * 1.5);
        } else if (item instanceof Bouclier) {
            // Utilisation spéciale d'un bouclier (parade parfaite)
            Bouclier bouclier = (Bouclier) item;
            resultat.put("nom", "Parade parfaite avec " + bouclier.getNom());
            // La parade parfaite n'inflige pas de dégâts mais accorde une défense exceptionnelle
            // Cet effet serait géré au niveau de la résolution globale des actions
        } else {
            // Item non reconnu, effet générique aléatoire
            int effetAleatoire = random.nextInt(3);
            
            switch (effetAleatoire) {
                case 0: // Soins
                    double valeurSoin = 10 + (random.nextDouble() * 20); // Entre 10 et 30
                    resultat.put("nom", "Effet mystérieux (Soins)");
                    resultat.put("healing", valeurSoin);
                    break;
                case 1: // Dégâts
                    double valeurDegats = 10 + (random.nextDouble() * 15); // Entre 10 et 25
                    resultat.put("nom", "Effet mystérieux (Dégâts)");
                    resultat.put("degats", valeurDegats);
                    break;
                case 2: // Effet mixte
                    resultat.put("nom", "Effet mystérieux (Mixte)");
                    resultat.put("healing", 5 + (random.nextDouble() * 10)); // Entre 5 et 15
                    resultat.put("degats", 5 + (random.nextDouble() * 10)); // Entre 5 et 15
                    break;
            }
        }
        
        return resultat;
    }
}