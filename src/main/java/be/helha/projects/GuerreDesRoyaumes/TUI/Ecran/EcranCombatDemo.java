package be.helha.projects.GuerreDesRoyaumes.TUI.Ecran;

import be.helha.projects.GuerreDesRoyaumes.DAO.CombatSessionMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.CombatSessionMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DTO.CombatResolver;
import be.helha.projects.GuerreDesRoyaumes.DTO.SkillManager;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.MongoDBConnectionException;
import be.helha.projects.GuerreDesRoyaumes.Model.Combat.CombatSession;
import be.helha.projects.GuerreDesRoyaumes.Model.Combat.CombatStatus;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Slot;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceCombat;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.screen.Screen;
import be.helha.projects.GuerreDesRoyaumes.Config.DAOProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Écran de combat amélioré qui utilise MongoDB pour persister les actions et résultats.
 * Suit le diagramme de séquence:
 * 
 * Joueur -> EcranCombat : Choisit action
 * EcranCombat -> ServiceCombat : Sauvegarde action
 * ServiceCombat -> MongoDB : Persiste l'action
 * MongoDB -> ServiceCombat : Confirme persistance
 * ServiceCombat -> CombatResolver : Déclenche résolution
 * CombatResolver -> MongoDB : Récupère les deux actions
 * CombatResolver : Calcule résultat
 * CombatResolver -> MongoDB : Sauvegarde résultat
 * EcranCombat <- ServiceCombat : Reçoit résultat
 * EcranCombat : Met à jour l'UI
 */
public class EcranCombatDemo {
    private final JoueurDAO joueurDAO;
    private final WindowBasedTextGUI textGUI;
    private final Screen screen;
    private final Joueur joueur;
    private final Joueur adversaire;
    private final ServiceCombat serviceCombat;
    private final CombatSessionMongoDAO sessionDAO;
    private final CombatResolver combatResolver;
    private final SkillManager skillManager;
    
    private Item itemSelectionne;
    private int tourActuel = 1;
    private final int MAX_TOURS = 5;
    
    /**
     * Constructeur de l'écran de combat
     */
    public EcranCombatDemo(JoueurDAO joueurDAO, WindowBasedTextGUI textGUI, Screen screen,
                       Joueur joueur, Joueur adversaire, ServiceCombat serviceCombat) {
        this.joueurDAO = joueurDAO;
        this.textGUI = textGUI;
        this.screen = screen;
        this.joueur = joueur;
        this.adversaire = adversaire;
        this.serviceCombat = serviceCombat;
        
        try {
            this.sessionDAO = DAOProvider.getCombatSessionMongoDAO();
        } catch (MongoDBConnectionException e) {
            throw new RuntimeException("Erreur de connexion à MongoDB", e);
        }
        
        // Initialiser le SkillManager
        this.skillManager = new SkillManager();
        
        // Initialiser le CombatResolver avec les dépendances
        this.combatResolver = new CombatResolver(joueurDAO, sessionDAO);
        
        // Initialiser un nouveau combat avec tour 1
        try {
            // Vérifier si un combat existe déjà
            String combatId = sessionDAO.trouverSessionId(joueur.getId(), adversaire.getId());
            
            if (combatId == null) {
                // Initialiser un nouveau combat
                System.out.println("DEBUG: Initialisation d'un nouveau combat");
                serviceCombat.initialiserCombat(joueur, adversaire, new ArrayList<>());
            } else {
                // Un combat existe déjà, charger l'état actuel
                CombatSession session = sessionDAO.chargerSession(combatId);
                if (session != null) {
                    tourActuel = session.getTourActuel();
                    System.out.println("DEBUG: Combat existant chargé, tour actuel: " + tourActuel);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation du combat: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Affiche l'écran de combat
     */
    public void afficher() {
        // Vérifier si les joueurs sont valides
        if (joueur == null || adversaire == null || 
            joueur.getPersonnage() == null || adversaire.getPersonnage() == null) {
            afficherMessageErreur("Erreur: Joueurs ou personnages non valides.");
            return;
        }
        
        // Vérifier si c'est le tour du joueur
        boolean estTourDuJoueur = serviceCombat.estTourDuJoueur(joueur, adversaire);
        
        Window fenetre = new BasicWindow("Combat - Tour " + tourActuel + "/" + MAX_TOURS);
        fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));
        
        Panel panel = new Panel(new GridLayout(1));
        
        // Afficher les informations des deux joueurs
        panel.addComponent(new Label("Votre personnage: " + joueur.getPersonnage().getNom() +
                " (PV: " + joueur.getPersonnage().getPointsDeVie() + ")"));
        panel.addComponent(new Label("Adversaire: " + adversaire.getPersonnage().getNom() +
                " (PV: " + adversaire.getPersonnage().getPointsDeVie() + ")"));
        
        panel.addComponent(new EmptySpace());
        
        // Si ce n'est pas le tour du joueur, afficher un écran d'attente
        if (!estTourDuJoueur) {
            panel.addComponent(new Label("C'est le tour de l'adversaire..."));
            panel.addComponent(new EmptySpace());
            
            panel.addComponent(new Button("Rafraîchir", () -> {
                fenetre.close();
                verifierEtatDuCombat();
            }));
        } else {
            // C'est le tour du joueur, afficher les actions possibles
            panel.addComponent(new Label("C'est votre tour - Choisissez votre action:"));
            
            // Boutons d'action de base
            Panel actionsPanel = new Panel(new GridLayout(3));
            
            actionsPanel.addComponent(new Button("Attaque", () -> {
                executerAction("attaque");
                fenetre.close();
            }));
            
            actionsPanel.addComponent(new Button("Défense", () -> {
                executerAction("defense");
                fenetre.close();
            }));
            
            actionsPanel.addComponent(new Button("Compétence spéciale", () -> {
                executerAction("special");
                fenetre.close();
            }));
            
            panel.addComponent(actionsPanel);
            
            // Section pour utiliser des items
            panel.addComponent(new EmptySpace());
            panel.addComponent(new Label("Ou utilisez un item:"));
            
            // Liste des items disponibles
            Table<String> itemsTable = new Table<>("ID", "Nom", "Type", "Description");
            List<Item> itemsDisponibles = obtenirItemsInventaireCombat();
            
            for (Item item : itemsDisponibles) {
                itemsTable.getTableModel().addRow(
                    String.valueOf(item.getId()),
                    item.getNom(),
                    item.getType(),
                    obtenirDescriptionItem(item)
                );
            }
            
            itemsTable.setSelectAction(() -> {
                int selectedIndex = itemsTable.getSelectedRow();
                if (selectedIndex >= 0 && selectedIndex < itemsDisponibles.size()) {
                    itemSelectionne = itemsDisponibles.get(selectedIndex);
                }
            });
            
            panel.addComponent(itemsTable);
            
            panel.addComponent(new Button("Utiliser item sélectionné", () -> {
                if (itemSelectionne != null) {
                    executerActionAvecItem("utiliser_item");
                    fenetre.close();
                } else {
                    afficherMessageErreur("Veuillez sélectionner un item d'abord");
                }
            }));
        }
        
        fenetre.setComponent(panel);
        textGUI.addWindowAndWait(fenetre);
    }
    
    /**
     * Vérifie l'état actuel du combat et met à jour l'interface
     */
    private void verifierEtatDuCombat() {
        try {
            // Récupérer l'identifiant de la session de combat
            String sessionId = sessionDAO.trouverSessionId(joueur.getId(), adversaire.getId());
            
            if (sessionId == null) {
                afficherMessageErreur("Erreur: Aucun combat en cours trouvé");
                return;
            }
            
            // Charger la session actuelle
            CombatSession session = sessionDAO.chargerSession(sessionId);
            
            if (session == null) {
                afficherMessageErreur("Erreur: Impossible de charger la session de combat");
                return;
            }
            
            // Mettre à jour le tour actuel
            tourActuel = session.getTourActuel();
            
            // Vérifier si le combat est terminé
            if (session.estTermine()) {
                terminerCombat(session);
                return;
            }
            
            // Vérifier si c'est le tour du joueur
            boolean estTourDuJoueur = sessionDAO.estTourDuJoueur(sessionId, joueur.getId());
            
            if (estTourDuJoueur) {
                // C'est notre tour, afficher l'écran de choix d'action
                afficher();
            } else {
                // Vérifier si des résultats sont disponibles pour le tour précédent
                Map<String, Object> resultats = sessionDAO.obtenirResultatTour(sessionId, tourActuel - 1);
                
                if (!resultats.isEmpty()) {
                    // Afficher les résultats du tour précédent
                    afficherResultatTour(sessionId, tourActuel - 1);
                } else {
                    // Pas de résultats disponibles, attendre l'action de l'adversaire
                    Window fenetreAttente = new BasicWindow("En attente...");
                    fenetreAttente.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));
                    
                    Panel panel = new Panel(new GridLayout(1));
                    panel.addComponent(new Label("En attente de l'action de l'adversaire..."));
                    panel.addComponent(new EmptySpace());
                    panel.addComponent(new Button("Rafraîchir", () -> {
                        fenetreAttente.close();
                        verifierEtatDuCombat();
                    }));
                    
                    fenetreAttente.setComponent(panel);
                    textGUI.addWindowAndWait(fenetreAttente);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification de l'état du combat: " + e.getMessage());
            e.printStackTrace();
            afficherMessageErreur("Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Exécute une action de combat basique
     */
    private void executerAction(String typeAction) {
        try {
            String resultat = serviceCombat.executerAction(joueur, adversaire, typeAction, tourActuel);
            
            // Afficher le résultat immédiat de l'action
            Window fenetreResultat = new BasicWindow("Action exécutée");
            fenetreResultat.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));
            
            Panel panel = new Panel(new GridLayout(1));
            panel.addComponent(new Label("Vous avez choisi: " + typeAction));
            panel.addComponent(new Label(resultat));
            panel.addComponent(new EmptySpace());
            
            panel.addComponent(new Button("Continuer", () -> {
                fenetreResultat.close();
                verifierEtatDuCombat();
            }));
            
            fenetreResultat.setComponent(panel);
            textGUI.addWindowAndWait(fenetreResultat);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution de l'action: " + e.getMessage());
            e.printStackTrace();
            afficherMessageErreur("Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Exécute une action avec un item sélectionné
     */
    private void executerActionAvecItem(String typeAction) {
        try {
            if (itemSelectionne == null) {
                afficherMessageErreur("Aucun item sélectionné");
                return;
            }
            
            String resultat = serviceCombat.executerActionAvecItem(joueur, adversaire, typeAction, itemSelectionne, tourActuel);
            
            // Afficher le résultat immédiat de l'action
            Window fenetreResultat = new BasicWindow("Item utilisé");
            fenetreResultat.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));
            
            Panel panel = new Panel(new GridLayout(1));
            panel.addComponent(new Label("Vous avez utilisé: " + itemSelectionne.getNom()));
            panel.addComponent(new Label(resultat));
            panel.addComponent(new EmptySpace());
            
            panel.addComponent(new Button("Continuer", () -> {
                fenetreResultat.close();
                // Réinitialiser l'item sélectionné
                itemSelectionne = null;
                verifierEtatDuCombat();
            }));
            
            fenetreResultat.setComponent(panel);
            textGUI.addWindowAndWait(fenetreResultat);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'utilisation de l'item: " + e.getMessage());
            e.printStackTrace();
            afficherMessageErreur("Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Affiche les résultats d'un tour de combat
     */
    private void afficherResultatTour(String sessionId, int tour) {
        try {
            // Obtenir les résultats du tour
            Map<String, Object> resultats = sessionDAO.obtenirResultatTour(sessionId, tour);
            
            if (resultats.isEmpty()) {
                return;
            }
            
            // Construire un message descriptif
            StringBuilder message = new StringBuilder();
            
            // Identifier qui est joueur1 et joueur2 dans la session
            CombatSession session = sessionDAO.chargerSession(sessionId);
            boolean joueurEstJ1 = session.getJoueur1Id() == joueur.getId();
            
            String actionJoueur = (String) resultats.get(joueurEstJ1 ? "actionJoueur1" : "actionJoueur2");
            String actionAdversaire = (String) resultats.get(joueurEstJ1 ? "actionJoueur2" : "actionJoueur1");
            
            double degatsJoueur = ((Number) resultats.getOrDefault(joueurEstJ1 ? "degatsJoueur1" : "degatsJoueur2", 0)).doubleValue();
            double degatsAdversaire = ((Number) resultats.getOrDefault(joueurEstJ1 ? "degatsJoueur2" : "degatsJoueur1", 0)).doubleValue();
            
            double healingJoueur = ((Number) resultats.getOrDefault(joueurEstJ1 ? "healingJoueur1" : "healingJoueur2", 0)).doubleValue();
            double healingAdversaire = ((Number) resultats.getOrDefault(joueurEstJ1 ? "healingJoueur2" : "healingJoueur1", 0)).doubleValue();
            
            // Construire le message des résultats
            message.append("Résultats du tour ").append(tour).append(":\n\n");
            message.append("Vous avez choisi: ").append(actionJoueur).append("\n");
            message.append("Votre adversaire a choisi: ").append(actionAdversaire).append("\n\n");
            
            if ("special".equals(actionJoueur)) {
                String nomCompetence = (String) resultats.get(joueurEstJ1 ? "competenceJoueur1" : "competenceJoueur2");
                message.append("Vous avez utilisé la compétence: ").append(nomCompetence).append("\n");
            }
            
            if ("special".equals(actionAdversaire)) {
                String nomCompetence = (String) resultats.get(joueurEstJ1 ? "competenceJoueur2" : "competenceJoueur1");
                message.append("L'adversaire a utilisé la compétence: ").append(nomCompetence).append("\n");
            }
            
            if ("utiliser_item".equals(actionJoueur)) {
                String nomItem = (String) resultats.get(joueurEstJ1 ? "itemJoueur1" : "itemJoueur2");
                message.append("Vous avez utilisé l'item: ").append(nomItem).append("\n");
            }
            
            if ("utiliser_item".equals(actionAdversaire)) {
                String nomItem = (String) resultats.get(joueurEstJ1 ? "itemJoueur2" : "itemJoueur1");
                message.append("L'adversaire a utilisé l'item: ").append(nomItem).append("\n");
            }
            
            message.append("\n");
            
            if (degatsJoueur > 0) {
                message.append("Vous avez infligé ").append(String.format("%.1f", degatsJoueur)).append(" points de dégâts.\n");
            }
            
            if (degatsAdversaire > 0) {
                message.append("L'adversaire vous a infligé ").append(String.format("%.1f", degatsAdversaire)).append(" points de dégâts.\n");
            }
            
            if (healingJoueur > 0) {
                message.append("Vous vous êtes soigné de ").append(String.format("%.1f", healingJoueur)).append(" points de vie.\n");
            }
            
            if (healingAdversaire > 0) {
                message.append("L'adversaire s'est soigné de ").append(String.format("%.1f", healingAdversaire)).append(" points de vie.\n");
            }
            
            // Points de vie après le tour
            double pvJoueur = ((Number) resultats.get(joueurEstJ1 ? "pvJoueur1" : "pvJoueur2")).doubleValue();
            double pvAdversaire = ((Number) resultats.get(joueurEstJ1 ? "pvJoueur2" : "pvJoueur1")).doubleValue();
            
            message.append("\nPoints de vie après ce tour:\n");
            message.append("- Vous: ").append(String.format("%.1f", pvJoueur)).append("\n");
            message.append("- Adversaire: ").append(String.format("%.1f", pvAdversaire));
            
            // Afficher les résultats
            Window fenetreResultat = new BasicWindow("Résultats du tour " + tour);
            fenetreResultat.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));
            
            Panel panel = new Panel(new GridLayout(1));
            panel.addComponent(new MultiLineLabel(message.toString()));
            
            panel.addComponent(new Button("Continuer", () -> {
                fenetreResultat.close();
                
                // Mise à jour des points de vie des personnages
                joueur.getPersonnage().setPointsDeVie(pvJoueur);
                adversaire.getPersonnage().setPointsDeVie(pvAdversaire);
                
                // Si le combat est terminé
                boolean combatTermine = (boolean) resultats.getOrDefault("combatTermine", false);
                
                if (combatTermine) {
                    terminerCombat(session);
                } else {
                    verifierEtatDuCombat();
                }
            }));
            
            fenetreResultat.setComponent(panel);
            textGUI.addWindowAndWait(fenetreResultat);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'affichage des résultats: " + e.getMessage());
            e.printStackTrace();
            afficherMessageErreur("Erreur: " + e.getMessage());
        }
    }
    
    /**
     * Termine le combat et affiche les résultats finaux
     */
    private void terminerCombat(CombatSession session) {
        try {
            // Déterminer le vainqueur
            int vainqueurId = session.getVainqueurId();
            boolean victoire = vainqueurId == joueur.getId();
            boolean matchNul = vainqueurId == 0;
            
            // Afficher les résultats finaux
            Window fenetre = new BasicWindow("Fin du combat");
            fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));
            
            Panel panel = new Panel(new GridLayout(1));
            
            if (matchNul) {
                panel.addComponent(new Label("Match nul!"));
            } else if (victoire) {
                panel.addComponent(new Label("Victoire! Vous avez gagné ce combat."));
            } else {
                panel.addComponent(new Label("Défaite! Votre adversaire a remporté ce combat."));
            }
            
            panel.addComponent(new EmptySpace());
            
            // Afficher les statistiques finales
            panel.addComponent(new Label("Points de vie restants:"));
            panel.addComponent(new Label("- Vous: " + String.format("%.1f", session.getPvJoueur1())));
            panel.addComponent(new Label("- Adversaire: " + String.format("%.1f", session.getPvJoueur2())));
            
            panel.addComponent(new EmptySpace());
            panel.addComponent(new Label("Retour au menu principal dans 5 secondes..."));
            
            fenetre.setComponent(panel);
            textGUI.addWindow(fenetre);
            
            // Enregistrer le résultat dans la base de données
            Joueur vainqueur = null;
            if (vainqueurId == joueur.getId()) {
                vainqueur = joueur;
            } else if (vainqueurId == adversaire.getId()) {
                vainqueur = adversaire;
            }
            
            serviceCombat.terminerCombat(joueur, adversaire, vainqueur);
            
            // Compte à rebours de 5 secondes
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    fenetre.close();
                    retourMenuPrincipal();
                }
            }, 5000); // 5 secondes
        } catch (Exception e) {
            System.err.println("Erreur lors de la terminaison du combat: " + e.getMessage());
            e.printStackTrace();
            afficherMessageErreur("Erreur: " + e.getMessage());
            retourMenuPrincipal();
        }
    }
    
    /**
     * Retourne au menu principal
     */
    private void retourMenuPrincipal() {
        new EcranPrincipal(null, joueurDAO, joueur.getPseudo(), screen).afficher();
    }
    
    /**
     * Récupère la liste des items dans l'inventaire de combat
     */
    private List<Item> obtenirItemsInventaireCombat() {
        List<Item> items = new ArrayList<>();
        
        if (joueur.getPersonnage() != null && joueur.getPersonnage().getInventaire() != null) {
            for (Slot slot : joueur.getPersonnage().getInventaire().getSlots()) {
                if (slot != null && slot.getItem() != null) {
                    items.add(slot.getItem());
                }
            }
        }
        
        return items;
    }
    
    /**
     * Obtient une description pour un item
     */
    private String obtenirDescriptionItem(Item item) {
        if (item == null) {
            return "";
        }
        
        StringBuilder description = new StringBuilder();
        String className = item.getClass().getSimpleName();
        
        switch (className) {
            case "Arme":
                description.append("Dégâts: ").append(((be.helha.projects.GuerreDesRoyaumes.Model.Items.Arme) item).getDegats());
                break;
            case "Bouclier":
                description.append("Défense: ").append(((be.helha.projects.GuerreDesRoyaumes.Model.Items.Bouclier) item).getDefense());
                break;
            case "Potion":
                be.helha.projects.GuerreDesRoyaumes.Model.Items.Potion potion = 
                    (be.helha.projects.GuerreDesRoyaumes.Model.Items.Potion) item;
                if (potion.getSoin() > 0) {
                    description.append("Soin: ").append(potion.getSoin());
                }
                if (potion.getDegats() > 0) {
                    if (description.length() > 0) {
                        description.append(", ");
                    }
                    description.append("Dégâts: ").append(potion.getDegats());
                }
                if (description.length() == 0) {
                    description.append("Effet mystérieux");
                }
                break;
            default:
                description.append("Type inconnu");
        }
        
        return description.toString();
    }
    
    /**
     * Affiche un message d'erreur
     */
    private void afficherMessageErreur(String message) {
        new MessageDialogBuilder()
                .setTitle("Erreur")
                .setText(message)
                .addButton(MessageDialogButton.OK)
                .build()
                .showDialog(textGUI);
    }
} 