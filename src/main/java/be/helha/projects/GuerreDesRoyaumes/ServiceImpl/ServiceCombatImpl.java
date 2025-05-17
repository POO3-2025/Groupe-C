package be.helha.projects.GuerreDesRoyaumes.ServiceImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.CombatDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceCombat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ServiceCombatImpl implements ServiceCombat {

    private final JoueurDAO joueurDAO;
    private final CombatDAO combatDAO;
    private final Random random = new Random();

    // Stockage temporaire des combats en cours avec thread-safety
    private Map<String, Map<String, Object>> combatsEnCours = new ConcurrentHashMap<>();
    // Stockage des actions des joueurs par tour
    private Map<String, Map<String, String>> actionsJoueurs = new ConcurrentHashMap<>();
    // Stockage des résultats des actions
    private Map<String, Map<String, String>> resultatsActions = new ConcurrentHashMap<>();

    @Autowired
    public ServiceCombatImpl(JoueurDAO joueurDAO, CombatDAO combatDAO) {
        this.joueurDAO = joueurDAO;
        this.combatDAO = combatDAO;
    }

    @Override
    public void initialiserCombat(Joueur joueur1, Joueur joueur2, List<Item> itemsSelectionnes) {
        System.out.println("DEBUG: Initialisation du combat entre " + joueur1.getPseudo() + " et " + joueur2.getPseudo());
        // Création d'un ID unique pour ce combat
        String combatId = joueur1.getId() + "-" + joueur2.getId() + "-" + System.currentTimeMillis();

        // S'assurer que les joueurs ont bien leurs personnages avec le type correct depuis la BDD
        try {
            // Recharger les joueurs depuis la base de données pour s'assurer d'avoir les bons personnages
            Joueur j1FromDB = joueurDAO.obtenirJoueurParId(joueur1.getId());
            Joueur j2FromDB = joueurDAO.obtenirJoueurParId(joueur2.getId());

            // Vérifier si les objets ont été correctement chargés
            if (j1FromDB != null && j1FromDB.getPersonnage() != null) {
                // Préserver l'identité des objets en copiant seulement les attributs nécessaires
                joueur1.setPersonnage(j1FromDB.getPersonnage());
                System.out.println("DEBUG: Personnage J1 chargé: " + joueur1.getPersonnage().getNom());
            }

            if (j2FromDB != null && j2FromDB.getPersonnage() != null) {
                joueur2.setPersonnage(j2FromDB.getPersonnage());
                System.out.println("DEBUG: Personnage J2 chargé: " + joueur2.getPersonnage().getNom());
            }
        } catch (Exception e) {
            System.err.println("DEBUG: Erreur lors du chargement des personnages: " + e.getMessage());
        }

        // Réinitialiser les points de vie au début du combat
        joueur1.getPersonnage().setPointsDeVie(100);
        joueur2.getPersonnage().setPointsDeVie(100);

        // Persister les points de vie initiaux
        try {
            joueurDAO.mettreAJourJoueur(joueur1);
            joueurDAO.mettreAJourJoueur(joueur2);
        } catch (Exception e) {
            System.err.println("DEBUG: Erreur lors de l'initialisation des PV: " + e.getMessage());
        }

        System.out.println("DEBUG: Points de vie initiaux: J1=" + joueur1.getPersonnage().getPointsDeVie() +
                ", J2=" + joueur2.getPersonnage().getPointsDeVie());

        // Stockage des informations du combat
        Map<String, Object> infoCombat = new HashMap<>();
        infoCombat.put("joueur1", joueur1);
        infoCombat.put("joueur2", joueur2);
        infoCombat.put("items", itemsSelectionnes);
        infoCombat.put("tourActuel", 1);
        infoCombat.put("joueurActif", joueur1.getId()); // Le joueur1 commence
        infoCombat.put("termine", false);
        infoCombat.put("derniereMiseAJour", System.currentTimeMillis()); // Horodatage pour détecter les blocages

        // Sauvegarder les PV initiaux pour référence
        infoCombat.put("pvInitiauxJoueur1", 100.0);
        infoCombat.put("pvInitiauxJoueur2", 100.0);

        // Initialiser le stockage des actions et résultats
        Map<String, String> actionsTour1 = new HashMap<>();
        Map<String, String> resultatsTour1 = new HashMap<>();
        actionsJoueurs.put(combatId + "-1", actionsTour1);
        resultatsActions.put(combatId + "-1", resultatsTour1);

        // Ajouter à la liste des combats en cours
        combatsEnCours.put(combatId, infoCombat);

        System.out.println("DEBUG: Combat initialisé avec ID: " + combatId);
    }

    @Override
    public String executerAction(Joueur joueur, Joueur adversaire, String typeAction, int tour) {
        System.out.println("DEBUG: Exécution action - Joueur: " + joueur.getPseudo() +
                ", Action: " + typeAction + ", Tour: " + tour);

        // Trouver l'ID du combat
        String combatId = trouverCombatId(joueur, adversaire);
        if (combatId == null) {
            System.err.println("DEBUG: Combat non trouvé");
            return "Combat non trouvé";
        }

        // Récupérer les informations du combat
        Map<String, Object> infoCombat = combatsEnCours.get(combatId);

        // Vérifier si le combat est déjà terminé
        if ((boolean) infoCombat.getOrDefault("termine", false)) {
            System.err.println("DEBUG: Combat déjà terminé");
            return "Le combat est déjà terminé";
        }

        // Vérifier si c'est le tour du joueur
        int joueurActif = (int) infoCombat.get("joueurActif");
        if (joueur.getId() != joueurActif) {
            System.err.println("DEBUG: Ce n'est pas le tour de " + joueur.getPseudo() +
                    ", tour actuel: " + joueurActif);
            return "Ce n'est pas votre tour de jouer";
        }

        // Enregistrer l'action du joueur pour ce tour
        String actionsTourKey = combatId + "-" + tour;
        String resultatsTourKey = combatId + "-" + tour;

        Map<String, String> actionsTour = actionsJoueurs.getOrDefault(actionsTourKey, new HashMap<>());
        Map<String, String> resultatsTour = resultatsActions.getOrDefault(resultatsTourKey, new HashMap<>());

        // Récupérer les joueurs dans l'ordre correct
        Joueur joueur1 = (Joueur) infoCombat.get("joueur1");
        Joueur joueur2 = (Joueur) infoCombat.get("joueur2");

        System.out.println("DEBUG: État avant action - J1 PV: " + joueur1.getPersonnage().getPointsDeVie() +
                ", J2 PV: " + joueur2.getPersonnage().getPointsDeVie());

        // Exécuter l'action du joueur et calculer le résultat
        String resultat = "";
        if (typeAction.equals("attaque")) {
            int degats = calculerDegats(joueur, adversaire, typeAction);

            // Appliquer les dégâts à l'adversaire
            double pvActuels = adversaire.getPersonnage().getPointsDeVie();
            double nouveauxPV = Math.max(0, pvActuels - degats);
            adversaire.getPersonnage().setPointsDeVie(nouveauxPV);

            // Mettre à jour le DAO pour persister les changements
            try {
                joueurDAO.mettreAJourJoueur(adversaire);
            } catch (Exception e) {
                System.err.println("DEBUG: Erreur lors de la mise à jour de l'adversaire: " + e.getMessage());
            }

            resultat = "Vous avez attaqué et infligé " + degats + " points de dégâts";

            // Stocker le résultat pour l'adversaire
            String resultatAdversaire = "L'adversaire a attaqué et vous a infligé " + degats + " points de dégâts";
            resultatsTour.put(String.valueOf(adversaire.getId()), resultatAdversaire);

        } else if (typeAction.equals("defense")) {
            resultat = "Vous vous êtes mis en posture défensive";

            // Stocker le résultat pour l'adversaire
            String resultatAdversaire = "L'adversaire s'est mis en posture défensive";
            resultatsTour.put(String.valueOf(adversaire.getId()), resultatAdversaire);

        } else if (typeAction.equals("special")) {
            int degats = calculerDegats(joueur, adversaire, typeAction);

            // Appliquer les dégâts à l'adversaire
            double pvActuels = adversaire.getPersonnage().getPointsDeVie();
            double nouveauxPV = Math.max(0, pvActuels - degats);
            adversaire.getPersonnage().setPointsDeVie(nouveauxPV);

            // Mettre à jour le DAO pour persister les changements
            try {
                joueurDAO.mettreAJourJoueur(adversaire);
            } catch (Exception e) {
                System.err.println("DEBUG: Erreur lors de la mise à jour de l'adversaire: " + e.getMessage());
            }

            resultat = "Vous avez utilisé une compétence spéciale et infligé " + degats + " points de dégâts";

            // Stocker le résultat pour l'adversaire
            String resultatAdversaire = "L'adversaire a utilisé une compétence spéciale et vous a infligé " + degats + " points de dégâts";
            resultatsTour.put(String.valueOf(adversaire.getId()), resultatAdversaire);
        }

        // Enregistrer l'action du joueur
        actionsTour.put(String.valueOf(joueur.getId()), typeAction);
        actionsJoueurs.put(actionsTourKey, actionsTour);

        // Stocker le résultat pour le joueur actuel
        resultatsTour.put(String.valueOf(joueur.getId()), resultat);
        resultatsActions.put(resultatsTourKey, resultatsTour);

        System.out.println("DEBUG: Action exécutée - Résultat: " + resultat);
        System.out.println("DEBUG: État après action - J1 PV: " + joueur1.getPersonnage().getPointsDeVie() +
                ", J2 PV: " + joueur2.getPersonnage().getPointsDeVie());

        // Changer le joueur actif (passer au joueur adverse)
        infoCombat.put("joueurActif", adversaire.getId());

        // Vérifier si le combat est terminé après l'action
        boolean combatTermine = estCombatTermine(joueur1, joueur2, tour);
        if (combatTermine) {
            infoCombat.put("termine", true);
            System.out.println("DEBUG: Combat terminé après l'action de " + joueur.getPseudo());
        }

        // Si le tour est terminé (les deux joueurs ont joué), préparer le tour suivant
        boolean tourTermine = actionsTour.containsKey(String.valueOf(joueur1.getId())) &&
                actionsTour.containsKey(String.valueOf(joueur2.getId()));

        if (tourTermine) {
            int nouveauTour = tour + 1;
            infoCombat.put("tourActuel", nouveauTour);

            // Préparer le stockage pour le prochain tour
            Map<String, String> actionsProchainTour = new HashMap<>();
            Map<String, String> resultatsProchainTour = new HashMap<>();
            actionsJoueurs.put(combatId + "-" + nouveauTour, actionsProchainTour);
            resultatsActions.put(combatId + "-" + nouveauTour, resultatsProchainTour);

            // Revenir au joueur1 pour le tour suivant
            infoCombat.put("joueurActif", joueur1.getId());

            System.out.println("DEBUG: Tour " + tour + " terminé, passage au tour " + nouveauTour);
        }

        return resultat;
    }

    /**
     * Obtient le résultat de l'action adverse pour le joueur spécifié
     */
    @Override
    public String obtenirResultatActionAdverse(Joueur joueur, Joueur adversaire, int tour) {
        String combatId = trouverCombatId(joueur, adversaire);
        if (combatId == null) {
            return "Combat non trouvé";
        }

        String resultatsTourKey = combatId + "-" + tour;
        Map<String, String> resultatsTour = resultatsActions.getOrDefault(resultatsTourKey, new HashMap<>());

        String resultat = resultatsTour.getOrDefault(String.valueOf(joueur.getId()), "Aucun résultat disponible");
        System.out.println("DEBUG: Résultat action adverse pour " + joueur.getPseudo() + ": " + resultat);
        return resultat;
    }

    /**
     * Vérifie si c'est le tour du joueur spécifié de jouer
     */
    @Override
    public boolean estTourDuJoueur(Joueur joueur, Joueur adversaire) {
        String combatId = trouverCombatId(joueur, adversaire);
        if (combatId == null) {
            System.err.println("DEBUG: Combat non trouvé lors de la vérification du tour");
            return false;
        }

        Map<String, Object> infoCombat = combatsEnCours.get(combatId);
        int joueurActif = (int) infoCombat.get("joueurActif");
        int tourActuel = (int) infoCombat.get("tourActuel");

        boolean estSonTour = joueur.getId() == joueurActif;
        System.out.println("DEBUG: Vérification tour - Joueur: " + joueur.getPseudo() +
                ", Tour actuel: " + tourActuel + ", Est son tour: " + estSonTour);
        return estSonTour;
    }

    @Override
    public int getTourActuel(Joueur joueur, Joueur adversaire) {
        String combatId = trouverCombatId(joueur, adversaire);
        if (combatId == null) {
            return 1; // Valeur par défaut
        }

        Map<String, Object> infoCombat = combatsEnCours.get(combatId);
        return (int) infoCombat.get("tourActuel");
    }

    private String trouverCombatId(Joueur joueur1, Joueur joueur2) {
        for (String combatId : combatsEnCours.keySet()) {
            Map<String, Object> infoCombat = combatsEnCours.get(combatId);
            Joueur j1 = (Joueur) infoCombat.get("joueur1");
            Joueur j2 = (Joueur) infoCombat.get("joueur2");

            if ((j1.getId() == joueur1.getId() && j2.getId() == joueur2.getId()) ||
                    (j1.getId() == joueur2.getId() && j2.getId() == joueur1.getId())) {
                return combatId;
            }
        }
        return null;
    }

    @Override
    public void enregistrerVictoire(Joueur joueur) {
        try {
            // Incrémenter le nombre de victoires
            joueur.setVictoires(joueur.getVictoires() + 1);

            // Mise à jour dans la base de données
            joueurDAO.mettreAJourJoueur(joueur);
            System.out.println("DEBUG: Victoire enregistrée pour " + joueur.getPseudo());
        } catch (Exception e) {
            System.err.println("Erreur lors de l'enregistrement de la victoire: " + e.getMessage());
        }
    }

    @Override
    public void terminerCombat(Joueur joueur1, Joueur joueur2, Joueur vainqueur) {
        if (vainqueur != null) {
            enregistrerVictoire(vainqueur);
        }

        // Supprimer le combat des combats en cours
        String combatId = trouverCombatId(joueur1, joueur2);
        if (combatId != null) {
            combatsEnCours.remove(combatId);

            // Nettoyer les actions et résultats stockés
            for (int tour = 1; tour <= 5; tour++) {
                actionsJoueurs.remove(combatId + "-" + tour);
                resultatsActions.remove(combatId + "-" + tour);
            }

            System.out.println("DEBUG: Combat terminé et nettoyé");
        }
    }

    @Override
    public boolean estCombatTermine(Joueur joueur1, Joueur joueur2, int tourActuel) {
        boolean termine = tourActuel >= 5 ||
                joueur1.getPersonnage().getPointsDeVie() <= 0 ||
                joueur2.getPersonnage().getPointsDeVie() <= 0;

        if (termine) {
            System.out.println("DEBUG: Combat terminé - Tour: " + tourActuel +
                    ", PV J1: " + joueur1.getPersonnage().getPointsDeVie() +
                    ", PV J2: " + joueur2.getPersonnage().getPointsDeVie());
        }

        return termine;
    }

    @Override
    public int calculerDegats(Joueur attaquant, Joueur defenseur, String typeAttaque) {
        // Calcul de base des dégâts selon les caractéristiques du personnage
        int baseAttaque = (int)(attaquant.getPersonnage().getDegats() * 2);

        // Ajouter un élément aléatoire
        int variation = random.nextInt(20) - 10; // Entre -10 et +10

        // Si c'est une attaque spéciale, augmenter les dégâts
        if ("special".equals(typeAttaque)) {
            baseAttaque = (int)(baseAttaque * 1.5);
        }

        // Calculer les dégâts finaux (minimum 5)
        int degats = Math.max(5, baseAttaque + variation);
        System.out.println("DEBUG: Calcul dégâts - Base: " + baseAttaque + ", Variation: " + variation + ", Final: " + degats);
        return degats;
    }

    @Override
    public int calculerDefense(Joueur defenseur, int degats) {
        // Réduction des dégâts en fonction de la résistance du personnage
        int reduction = (int)(defenseur.getPersonnage().getResistance() / 2);

        // Ajouter un élément aléatoire à la réduction
        int variationReduction = random.nextInt(10);

        // Calculer les dégâts réduits (minimum 1)
        int degatsReduits = Math.max(1, degats - reduction - variationReduction);
        System.out.println("DEBUG: Calcul défense - Dégâts initiaux: " + degats +
                ", Réduction: " + reduction + ", Variation: " + variationReduction +
                ", Dégâts finaux: " + degatsReduits);
        return degatsReduits;
    }

    /**
     * Méthode publique pour débloquer un combat en forçant le changement de tour
     * Uniquement pour usage en mode développement
     *
     * @param joueur Le joueur actuel
     * @param adversaire L'adversaire
     * @return true si le déblocage a réussi, false sinon
     */
    @Override
    public boolean forcerChangementTour(Joueur joueur, Joueur adversaire) {
        try {
            System.out.println("DEBUG: DÉBUT forçage du changement de tour pour " + joueur.getPseudo());
            // Trouver l'ID du combat
            String combatId = trouverCombatId(joueur, adversaire);
            if (combatId == null) {
                System.err.println("DEBUG: Impossible de trouver le combat pour forcer le changement de tour");
                return false;
            }

            // Récupérer les informations du combat
            Map<String, Object> infoCombat = combatsEnCours.get(combatId);

            // Débogage pour comprendre l'état du combat
            Joueur joueur1 = (Joueur) infoCombat.get("joueur1");
            Joueur joueur2 = (Joueur) infoCombat.get("joueur2");
            int tourActuel = (int) infoCombat.get("tourActuel");
            int joueurActifId = (int) infoCombat.get("joueurActif");

            System.out.println("DEBUG: État du combat:");
            System.out.println("  - Combat ID: " + combatId);
            System.out.println("  - Tour: " + tourActuel);
            System.out.println("  - Joueur actif ID: " + joueurActifId);
            System.out.println("  - Joueur1: " + joueur1.getPseudo() + " (ID: " + joueur1.getId() + ", PV: " + joueur1.getPersonnage().getPointsDeVie() + ")");
            System.out.println("  - Joueur2: " + joueur2.getPseudo() + " (ID: " + joueur2.getId() + ", PV: " + joueur2.getPersonnage().getPointsDeVie() + ")");

            // Vérifier si le combat est bloqué (absence d'activité pendant une période)
            long derniereMiseAJour = (long) infoCombat.getOrDefault("derniereMiseAJour", 0L);
            long tempsActuel = System.currentTimeMillis();
            boolean combatInactif = (tempsActuel - derniereMiseAJour) > 30000; // 30 secondes

            if (combatInactif) {
                System.out.println("DEBUG: Combat inactif détecté - Déblocage automatique");
            }

            // MÉTHODE PLUS EFFICACE DE DÉBLOCAGE:
            // 1. Force directement le tour pour le joueur qui le demande
            // 2. Simule une action défensive pour l'adversaire si nécessaire

            // Si l'adversaire est actif mais n'a pas joué, simuler son action
            if (joueurActifId != joueur.getId()) {
                // Déterminer qui est l'adversaire actif actuellement
                Joueur joueurActif = (joueurActifId == joueur1.getId()) ? joueur1 : joueur2;

                // Simuler une action défensive pour l'adversaire
                String actionsTourKey = combatId + "-" + tourActuel;
                Map<String, String> actionsTour = actionsJoueurs.getOrDefault(actionsTourKey, new HashMap<>());

                if (!actionsTour.containsKey(String.valueOf(joueurActifId))) {
                    System.out.println("DEBUG: Simulation d'une action défensive pour " + joueurActif.getPseudo());

                    // Exécuter directement une action défensive pour l'adversaire
                    String resultat = executerAction(joueurActif, joueur, "defense", tourActuel);
                    System.out.println("DEBUG: Action simulée: " + resultat);
                }
            }

            // Maintenant forcer le tour pour le joueur qui demande le déblocage
            infoCombat.put("joueurActif", joueur.getId());
            infoCombat.put("derniereMiseAJour", System.currentTimeMillis());

            // S'assurer que les points de vie actuels sont correctement stockés
            try {
                // Récupérer les valeurs les plus récentes
                Joueur j1FromDB = joueurDAO.obtenirJoueurParId(joueur1.getId());
                Joueur j2FromDB = joueurDAO.obtenirJoueurParId(joueur2.getId());

                if (j1FromDB != null && j1FromDB.getPersonnage() != null) {
                    joueur1.getPersonnage().setPointsDeVie(j1FromDB.getPersonnage().getPointsDeVie());
                }

                if (j2FromDB != null && j2FromDB.getPersonnage() != null) {
                    joueur2.getPersonnage().setPointsDeVie(j2FromDB.getPersonnage().getPointsDeVie());
                }

                // Mettre à jour les objets dans infoCombat
                infoCombat.put("joueur1", joueur1);
                infoCombat.put("joueur2", joueur2);
            } catch (Exception e) {
                System.err.println("DEBUG: Erreur lors de la mise à jour des PV: " + e.getMessage());
            }

            System.out.println("DEBUG: Tour forcé pour " + joueur.getPseudo() + " (ID: " + joueur.getId() + ")");

            return true;
        } catch (Exception e) {
            System.err.println("DEBUG: Erreur lors du forçage du changement de tour: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public CombatDAO getCombatDAO() {
        return combatDAO;
    }
}


