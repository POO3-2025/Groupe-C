package be.helha.projects.GuerreDesRoyaumes.TUI.Ecran;

import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.ActionCombatDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.CombatDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.PersonnageMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.RoyaumeMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat.Competence;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceCombat;
import be.helha.projects.GuerreDesRoyaumes.ServiceImpl.CompetenceServiceImpl;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TextColor;


import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class EcranCombat {
    private final JoueurDAO joueurDAO;
    private final WindowBasedTextGUI textGUI;
    private final Screen screen;
    private final Joueur joueur;
    private final Joueur adversaire;
    private final ServiceCombat serviceCombat;
    private int tourActuel = 1;
    private final int MAX_TOURS = 5;
    private boolean actionAdversaireRecue = false;
    private String actionAdversaire = null;
    private int compteurVerifications = 0;
    
    // Pour suivre les compétences déjà utilisées
    private List<Competence> competencesUtilisees = Collections.emptyList();
    // Pour suivre l'état de la fenêtre
    private boolean fenetreAttenteActive = false;

    public EcranCombat(JoueurDAO joueurDAO, WindowBasedTextGUI textGUI, Screen screen,
                       Joueur joueur, Joueur adversaire, ServiceCombat serviceCombat) {
        this.joueurDAO = joueurDAO;
        this.textGUI = textGUI;
        this.screen = screen;
        this.joueur = joueur;
        this.adversaire = adversaire;
        this.serviceCombat = serviceCombat;
        
        // À l'initialisation, charger les compétences du joueur
        try {
            CompetenceServiceImpl competenceService = CompetenceServiceImpl.getInstance();
            competencesUtilisees = competenceService.obtenirCompetencesJoueur(joueur);
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement des compétences utilisées: " + e.getMessage());
        }
    }

    public void afficher() {
        // Récupérer l'ID du combat en cours
        String idCombat = null;
        try {
            idCombat = serviceCombat.obtenirIdCombatEnCours(joueur.getId());
            if (idCombat == null) {
                afficherMessageErreur("Impossible de trouver l'ID du combat en cours.");
                return;
            }
        } catch (Exception e) {
            afficherMessageErreur("Erreur lors de la récupération de l'ID du combat: " + e.getMessage());
            return;
        }

        // Actualiser les points de vie avant d'afficher l'écran
        rafraichirPointsDeVie();

        // Vérifier si le combat est terminé
        if (estCombatTermine()) {
            terminerCombat();
            return;
        }

        // Vérifier si le maximum de tours est atteint
        if (tourActuel > MAX_TOURS) {
            terminerCombat();
            return;
        }
        
        // Vérifier l'état des deux joueurs pour ce tour
        boolean joueurLocalPret = false;
        boolean adversairePret = false;
        try {
            ActionCombatDAOImpl actionCombatDAO = ActionCombatDAOImpl.getInstance();
            joueurLocalPret = actionCombatDAO.joueurAEffectueAction(idCombat, tourActuel, joueur.getId());
            adversairePret = actionCombatDAO.joueurAEffectueAction(idCombat, tourActuel, adversaire.getId());
            
            System.out.println("== ÉTAT DU COMBAT (Tour " + tourActuel + ") ==");
            System.out.println("- " + joueur.getPseudo() + " a joué: " + joueurLocalPret);
            System.out.println("- " + adversaire.getPseudo() + " a joué: " + adversairePret);
            
            // Si les deux joueurs ont déjà joué pour ce tour, afficher l'écran de résolution
            if (joueurLocalPret && adversairePret) {
                afficherEcranResolutionTour();
                return;
            }
            
            // Si ce joueur a déjà joué mais pas l'adversaire, afficher l'écran d'attente
            if (joueurLocalPret && !adversairePret) {
                afficherEcranAttente();
                return;
            }
            
            // Si l'adversaire a joué mais pas ce joueur, on continue normalement pour permettre de jouer
            if (!joueurLocalPret && adversairePret) {
                System.out.println("C'est à votre tour de jouer !");
            }
            
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification de l'état des joueurs: " + e.getMessage());
            // On continue quand même pour éviter de bloquer le jeu
        }

        // === ÉCRAN PRINCIPAL DU COMBAT ===
        Window fenetre = new BasicWindow("Combat - Tour " + tourActuel + "/" + MAX_TOURS);
        fenetre.setHints(Collections.singletonList(Window.Hint.CENTERED));

        Panel panelPrincipal = new Panel(new GridLayout(1));
        
        // Barre de titre avec statut du tour
        Panel titrePanel = new Panel(new GridLayout(1));
        titrePanel.addComponent(new Label("╔═════════════════════════════════════════╗"));
        titrePanel.addComponent(new Label("║            GUERRE DES ROYAUMES          ║"));
        titrePanel.addComponent(new Label("║              TOUR " + tourActuel + " SUR " + MAX_TOURS + "              ║"));
        titrePanel.addComponent(new Label("╚═════════════════════════════════════════╝"));
        panelPrincipal.addComponent(titrePanel);
        panelPrincipal.addComponent(new EmptySpace());
        
        // Panneau de statut des joueurs
        Panel statutPanel = new Panel(new GridLayout(3));
        
        // En-têtes
        statutPanel.addComponent(new Label("STATUT"));
        statutPanel.addComponent(new Label(joueur.getPseudo()));
        statutPanel.addComponent(new Label(adversaire.getPseudo()));
        
        // Statut du tour
        statutPanel.addComponent(new Label("Action:"));
        
        if (joueurLocalPret) {
            statutPanel.addComponent(new Label("✓ Jouée"));
        } else {
            statutPanel.addComponent(new Label("○ En attente"));
        }
        
        if (adversairePret) {
            statutPanel.addComponent(new Label("✓ Jouée"));
        } else {
            statutPanel.addComponent(new Label("○ En attente"));
        }
        
        panelPrincipal.addComponent(statutPanel);
        panelPrincipal.addComponent(new EmptySpace());
        
        // Statistiques des personnages
        Panel statsPanel = creerPanneauStatistiquesComplet();
        
        panelPrincipal.addComponent(statsPanel);
        panelPrincipal.addComponent(new EmptySpace());
        
        // Bouton pour actualiser l'état du combat
        // Créer une référence finale pour utilisation dans le lambda
        final Window fenetreActualisation = fenetre;
        panelPrincipal.addComponent(new Button("↻ Actualiser l'état du combat", () -> {
            rafraichirPointsDeVie();
            fenetreActualisation.close();
            afficher();
        }));
        panelPrincipal.addComponent(new EmptySpace());
        
        // Section des actions
        if (!joueurLocalPret) {
            // Si c'est au tour du joueur, afficher les options d'action
            Panel actionsPanel = new Panel(new GridLayout(2));
            actionsPanel.addComponent(new Label("═══ ACTIONS DISPONIBLES ═══"));
            actionsPanel.addComponent(new EmptySpace());
            
            // Créer une référence finale pour les actions
            final Window fenetreAction = fenetre;
            
            // Bouton d'attaque
            String finalIdCombat = idCombat;
            actionsPanel.addComponent(new Button("⚔️ Attaquer", () -> {
                executerActionAttaque(finalIdCombat, fenetreAction);
            }));
            
            // Bouton de défense
            String finalIdCombat1 = idCombat;
            actionsPanel.addComponent(new Button("🛡️ Se défendre", () -> {
                executerActionDefense(finalIdCombat1, fenetreAction);
            }));
            
            // Bouton pour utiliser une compétence
            actionsPanel.addComponent(new Button("✨ Utiliser Compétence", () -> {
                fenetreAction.close();
                afficherChoixCompetence();
            }));

            // Bouton pour utiliser une potion
            actionsPanel.addComponent(new Button("🧪 Utiliser Potion", () -> {
                fenetreAction.close();
                afficherChoixPotion();
            }));
            
            panelPrincipal.addComponent(actionsPanel);
        } else {
            // Si le joueur a déjà joué, afficher un message
            Panel messagePanel = new Panel(new GridLayout(1));
            messagePanel.addComponent(new Label("═══ TOUR TERMINÉ ═══"));
            messagePanel.addComponent(new Label("Vous avez déjà effectué votre action pour ce tour."));
            messagePanel.addComponent(new Label("Attendez que votre adversaire termine son action."));
            
            panelPrincipal.addComponent(messagePanel);
            
            // Bouton pour passer en mode attente
            // Utiliser la référence finale préalablement créée
            panelPrincipal.addComponent(new Button("Passer en mode attente", () -> {
                fenetreActualisation.close();
                afficherEcranAttente();
            }));
        }
        
        panelPrincipal.addComponent(new EmptySpace());
        
        // Bouton pour abandonner le combat
        Panel boutonPanel = new Panel(new GridLayout(1));
        boutonPanel.addComponent(new Button("❌ Abandonner le combat", () -> {
            MessageDialogButton reponse = new MessageDialogBuilder()
                .setTitle("Abandonner")
                .setText("Êtes-vous sûr de vouloir abandonner ce combat ? Vous perdrez automatiquement.")
                .addButton(MessageDialogButton.Yes)
                .addButton(MessageDialogButton.No)
                .build()
                .showDialog(textGUI);

            if (reponse == MessageDialogButton.Yes) {
                fenetreActualisation.close(); // Utiliser la référence finale préalablement créée
                try {
                    serviceCombat.terminerCombat(joueur, adversaire, adversaire); // Adversaire gagne par abandon
                    afficherMessageSucces("Vous avez abandonné le combat. " + adversaire.getPseudo() + " remporte la victoire.");
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'abandon du combat: " + e.getMessage());
                }
                retourMenuPrincipal();
            }
        }));
        panelPrincipal.addComponent(boutonPanel);

        fenetre.setComponent(panelPrincipal);
        textGUI.addWindowAndWait(fenetre);
    }
    
    private void executerActionAttaque(String idCombat, Window fenetrePrecedente) {
        try {
            // Calculer les dégâts totaux (personnage + armes)
            double degatsPersonnage = joueur.getPersonnage().getDegats();
            double degatsArmes = calculerBonusAttaqueItems();
            double degatsTotal = degatsPersonnage + degatsArmes;
            
            double resistanceAdversaire = adversaire.getPersonnage().getResistance();
            
            double pvApresAttaque = 0;
            double pvActuelsAdversaire = 0;
            double degatsRestants = 0;
            double resistanceRestante = 0;
            
            if (degatsTotal > resistanceAdversaire) {
                degatsRestants = degatsTotal - resistanceAdversaire;
                
                pvActuelsAdversaire = adversaire.getPersonnage().getPointsDeVie();
                pvApresAttaque = Math.max(0, pvActuelsAdversaire - degatsRestants);
                adversaire.getPersonnage().setPointsDeVie(pvApresAttaque);
                
                // Après l'attaque, la résistance tombe à 0
                adversaire.getPersonnage().setResistance(0);
            } else {
                // Tous les dégâts sont absorbés par la résistance
                resistanceRestante = resistanceAdversaire - degatsTotal;
                adversaire.getPersonnage().setResistance(resistanceRestante);
                // Les PV ne changent pas
                pvApresAttaque = adversaire.getPersonnage().getPointsDeVie();
            }
            
            // Récupérer les points de vie actuels du joueur
            double pvJoueur = joueur.getPersonnage().getPointsDeVie();
            
            // Enregistrer l'action dans la table actions_combat
            ActionCombatDAOImpl actionCombatDAO = ActionCombatDAOImpl.getInstance();
            String parametres = "{\"degats\":" + degatsTotal + "}";
            boolean actionEnregistree = actionCombatDAO.enregistrerAction(
                idCombat, 
                tourActuel, 
                joueur.getId(), 
                "attaque", 
                parametres,
                pvJoueur,
                pvApresAttaque
            );
            
            if (!actionEnregistree) {
                afficherMessageErreur("Erreur lors de l'enregistrement de l'action d'attaque.");
                return;
            }
            
            // Enregistrer les PV actualisés
            serviceCombat.mettreAJourPointsDeVie(adversaire.getId(), pvApresAttaque);
            
            // Insérer les deux joueurs dans la table action_etats_personnage avec les nouvelles vie restante et points_defense
            // Récupérer les points de défense actuels
            double defenseJoueur = joueur.getPersonnage().getResistance();
            double defenseAdversaire = adversaire.getPersonnage().getResistance();
            
            // Enregistrer l'état du joueur attaquant
            actionCombatDAO.enregistrerEtatPersonnage(
                idCombat,
                joueur.getId(),
                pvJoueur,
                defenseJoueur,
                null // Aucun buff pour l'instant
            );
            
            // Enregistrer l'état de l'adversaire
            actionCombatDAO.enregistrerEtatPersonnage(
                idCombat,
                adversaire.getId(),
                pvApresAttaque,
                defenseAdversaire,
                null // Aucun buff pour l'instant
            );
            
            // Passer au joueur suivant
            serviceCombat.passerAuJoueurSuivant(idCombat, adversaire.getId());
            
            // Afficher le résultat de l'attaque sans fermer la fenêtre principale
            String message = "Attaque réussie ! Vous avez infligé " + degatsTotal + 
                            " points de dégâts à " + adversaire.getPseudo() + 
                            ".\nPV adversaire: " + pvApresAttaque;
            
            // Vérifier si le combat se termine par mort subite
            if (pvApresAttaque <= 0) {
                message += "\n\nVictoire ! Vous avez vaincu " + adversaire.getPseudo() + " !";
                // Terminer le combat avec le joueur actuel comme vainqueur
                serviceCombat.terminerCombat(joueur, adversaire, joueur);
                
                // Afficher le message et terminer le combat
                new MessageDialogBuilder()
                    .setTitle("Victoire !")
                    .setText(message)
                    .addButton(MessageDialogButton.OK)
                    .build()
                    .showDialog(textGUI);
                
                fenetrePrecedente.close();
                terminerCombat();
                return;
            }
            
            // Afficher le message et passer à l'écran d'attente
            new MessageDialogBuilder()
                .setTitle("Attaque réussie")
                .setText(message)
                .addButton(MessageDialogButton.OK)
                .build()
                .showDialog(textGUI);
            
            // Fermer la fenêtre principale et passer à la phase d'attente
            fenetrePrecedente.close();
            afficherEcranAttente();
            
        } catch (Exception e) {
            e.printStackTrace();
            afficherMessageErreur("Erreur lors de l'exécution de l'attaque: " + e.getMessage());
        }
    }
    
    private void executerActionDefense(String idCombat, Window fenetrePrecedente) {
        try {
            // Calculer le bonus de défense
            double bonusDefense = calculerBonusDefenseItems();
            double defenseTotal = joueur.getPersonnage().getResistance() + bonusDefense;
            
            // Mettre à jour la résistance du personnage
            joueur.getPersonnage().setResistance(defenseTotal);
            
            // Récupérer les points de vie actuels
            double pvJoueur = joueur.getPersonnage().getPointsDeVie();
            double pvAdversaire = adversaire.getPersonnage().getPointsDeVie();
            
            // Enregistrer l'action dans la table actions_combat
            ActionCombatDAOImpl actionCombatDAO = ActionCombatDAOImpl.getInstance();
            String parametres = "{\"bonusDefense\":" + bonusDefense + "}";
            boolean actionEnregistree = actionCombatDAO.enregistrerAction(
                idCombat, 
                tourActuel, 
                joueur.getId(), 
                "defense", 
                parametres,
                pvJoueur,
                pvAdversaire
            );
            
            if (!actionEnregistree) {
                afficherMessageErreur("Erreur lors de l'enregistrement de l'action de défense.");
                return;
            }
            
            // Insérer les deux joueurs dans la table action_etats_personnage
            // Enregistrer l'état du joueur qui se défend avec son bonus de défense
            actionCombatDAO.enregistrerEtatPersonnage(
                idCombat,
                joueur.getId(),
                (int) pvJoueur,
                (int) defenseTotal, // On ajoute le bonus de défense
                null // Aucun buff pour l'instant
            );
            
            // Enregistrer l'état de l'adversaire
            actionCombatDAO.enregistrerEtatPersonnage(
                idCombat,
                adversaire.getId(),
                (int) pvAdversaire,
                (int) adversaire.getPersonnage().getResistance(),
                null // Aucun buff pour l'instant
            );
            
            // Passer au joueur suivant
            serviceCombat.passerAuJoueurSuivant(idCombat, adversaire.getId());
            
            // Afficher le message de confirmation
            String message = "Vous avez choisi la défense. Bonus de " + 
                defenseTotal + " points de défense." +
                "\n\nVous êtes maintenant en position défensive contre la prochaine attaque.";
                
            new MessageDialogBuilder()
                .setTitle("Défense activée")
                .setText(message)
                .addButton(MessageDialogButton.OK)
                .build()
                .showDialog(textGUI);
            
            // Fermer la fenêtre et passer à l'écran d'attente
            fenetrePrecedente.close();
            afficherEcranAttente();
            
        } catch (Exception e) {
            e.printStackTrace();
            afficherMessageErreur("Erreur lors de l'exécution de la défense: " + e.getMessage());
        }
    }

    private void afficherFenetreAttenteEtPlanifierVerification() {
        Window fenetreAttente = new BasicWindow("Attente");
        fenetreAttente.setHints(Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));
        panel.addComponent(new Label("En attente de l'adversaire..."));
        panel.addComponent(new Label("Tour " + tourActuel + "/" + MAX_TOURS));

        // Bouton de vérification manuelle
        panel.addComponent(new Button("Actualiser", () -> {
            fenetreAttente.close();
            textGUI.getGUIThread().invokeLater(this::afficher);
        }));

        fenetreAttente.setComponent(panel);
        textGUI.addWindowAndWait(fenetreAttente);

        // Planifier la vérification automatique après fermeture de la fenêtre
        textGUI.getGUIThread().invokeLater(() -> {
            if (!serviceCombat.sontJoueursPrets(joueur, adversaire)) {
                afficherFenetreAttenteEtPlanifierVerification();
            } else {
                afficher(); // Relancer l'affichage principal si prêt
            }
        });
    }

    private void afficherChoixCompetence() {
        Window fenetre = new BasicWindow("Choix de Compétence");
        fenetre.setHints(Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(1));
        panel.addComponent(new Label("Choisissez une compétence à utiliser:"));
        panel.addComponent(new EmptySpace());

        try {
            // Récupérer les compétences achetées par le joueur
            CompetenceServiceImpl competenceService = CompetenceServiceImpl.getInstance();
            List<Competence> competencesJoueur = competenceService.obtenirCompetencesJoueur(joueur);
            
            if (competencesJoueur.isEmpty()) {
                panel.addComponent(new Label("Vous n'avez pas encore acheté de compétences."));
            } else {
                Panel competencesPanel = new Panel(new GridLayout(3));
                competencesPanel.addComponent(new Label("Nom"));
                competencesPanel.addComponent(new Label("Description"));
                competencesPanel.addComponent(new Label("Action"));
                
                for (Competence competence : competencesJoueur) {
                    competencesPanel.addComponent(new Label(competence.getNom()));
                    competencesPanel.addComponent(new Label(competence.getDescription()));
                    
                    // Vérifier si la compétence a déjà été utilisée
                    boolean dejaUtilisee = competencesUtilisees.stream()
                            .anyMatch(c -> c.getId().equals(competence.getId()));
                    
                    Button btnUtiliser = new Button("Utiliser", () -> {
                        // TODO: Implémenter l'utilisation de la compétence
                        // serviceCombat.utiliserCompetence(joueur, adversaire, competence.getId(), tourActuel);
                        afficherMessageSucces("Vous avez choisi d'utiliser la compétence: " + competence.getNom());
                        // competencesUtilisees.add(competence); // Ajouter à la liste des compétences utilisées
                        fenetre.close();
                        afficher();
                    });
                    
                    // Désactiver le bouton si la compétence a déjà été utilisée
                    if (dejaUtilisee) {
                        btnUtiliser.setEnabled(false);
                        competencesPanel.addComponent(new Label("Déjà utilisée"));
                    } else {
                        competencesPanel.addComponent(btnUtiliser);
                    }
                }
                
                panel.addComponent(competencesPanel);
            }
        } catch (Exception e) {
            panel.addComponent(new Label("Erreur lors du chargement des compétences: " + e.getMessage()));
        }
        
        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Retour", () -> {
            fenetre.close();
            afficher();
        }));

        fenetre.setComponent(panel);
        textGUI.addWindowAndWait(fenetre);
    }

    private void afficherChoixPotion() {
        Window fenetre = new BasicWindow("Utiliser une Potion");
        fenetre.setHints(Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(1));
        panel.addComponent(new Label("Quel type de potion souhaitez-vous utiliser?"));
        panel.addComponent(new EmptySpace());

        // Vérifier si le joueur a des potions dans son inventaire
        boolean aPotionSoin = joueurHasPotionType("Soin");
        boolean aPotionDegats = joueurHasPotionType("Dégâts");
        
        Panel optionsPanel = new Panel(new GridLayout(2));
        
        Button btnPotionSoin = new Button("Potion de Soin", () -> {
            // TODO: Implémenter l'utilisation de la potion de soin
            // serviceCombat.utiliserPotion(joueur, "soin", tourActuel);
            afficherMessageSucces("Vous avez utilisé une potion de soin. +30 points de vie.");
            fenetre.close();
            afficher();
        });
        btnPotionSoin.setEnabled(aPotionSoin);
        optionsPanel.addComponent(btnPotionSoin);
        
        Button btnPotionDegats = new Button("Potion de Dégâts", () -> {
            // TODO: Implémenter l'utilisation de la potion de dégâts
            // serviceCombat.utiliserPotion(joueur, "degats", tourActuel);
            afficherMessageSucces("Vous avez utilisé une potion de dégâts. +20 points de dégâts à l'adversaire.");
            fenetre.close();
            afficher();
        });
        btnPotionDegats.setEnabled(aPotionDegats);
        optionsPanel.addComponent(btnPotionDegats);
        
        panel.addComponent(optionsPanel);
        
        if (!aPotionSoin && !aPotionDegats) {
            panel.addComponent(new Label("Vous n'avez aucune potion dans votre inventaire."));
        }
        
        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Retour", () -> {
            fenetre.close();
            afficher();
        }));

        fenetre.setComponent(panel);
        textGUI.addWindowAndWait(fenetre);
    }

    private boolean joueurHasPotionType(String type) {
        // TODO: Implémenter la vérification de potions dans l'inventaire
        // Cette fonction devrait vérifier si le joueur a une potion du type spécifié
        // Pour l'instant, on retourne true pour démonstration
        return true;
    }

    private double calculerBonusAttaqueItems() {
        // Utiliser le service de combat pour calculer les dégâts totaux du joueur
        double degatsTotal = serviceCombat.calculerDegatsJoueur(joueur);
        double degatsPersonnage = joueur.getPersonnage().getDegats();
        
        // Retourner uniquement le bonus des items (dégâts totaux - dégâts de base du personnage)
        return degatsTotal - degatsPersonnage;
    }

    private int calculerBonusDefenseItems() {
        // TODO: Implémenter le calcul du bonus de défense des items
        // Cette fonction devrait parcourir l'inventaire du joueur et calculer les bonus de défense
        // Pour l'instant, on retourne une valeur de test
        return 8;
    }

    private void afficherConfirmationAction(String typeAction) {
        Window fenetre = new BasicWindow("Confirmation");
        fenetre.setHints(Collections.singletonList(Window.Hint.CENTERED));

        // Créer une référence finale pour la fenêtre à utiliser dans les lambdas
        final Window fenetreFinale = fenetre;

        Panel panel = new Panel(new GridLayout(1));
        panel.addComponent(new Label("Vous avez choisi: " + typeAction));
        panel.addComponent(new Label("Voulez-vous confirmer cette action?"));
        
        Panel boutonsPanel = new Panel(new GridLayout(2));
        boutonsPanel.addComponent(new Button("Confirmer", () -> {
            // TODO: Implémenter la confirmation de l'action
            fenetreFinale.close();
            // attendreProchainTour();
        }));
        
        boutonsPanel.addComponent(new Button("Annuler", () -> {
            fenetreFinale.close();
            afficher(); // Retour à l'écran de sélection d'action
        }));
        
        panel.addComponent(boutonsPanel);
        
        fenetre.setComponent(panel);
        textGUI.addWindowAndWait(fenetre);
    }

    private void rafraichirPointsDeVie() {
        try {
            // Récupérer l'ID du combat en cours
            String idCombat = serviceCombat.obtenirIdCombatEnCours(joueur.getId());
            if (idCombat == null) {
                System.err.println("Impossible de trouver l'ID du combat en cours.");
                return;
            }
            
            // Récupérer les stats actuels depuis la table action_etats_personnage
            ActionCombatDAOImpl actionCombatDAO = ActionCombatDAOImpl.getInstance();
            
            // Option 1: Récupérer les stats individuellement pour chaque joueur
            int[] statsJoueur = actionCombatDAO.recupererDerniersStats(idCombat, joueur.getId());
            int[] statsAdversaire = actionCombatDAO.recupererDerniersStats(idCombat, adversaire.getId());
            
            if (statsJoueur != null && statsAdversaire != null) {
                // Mettre à jour les points de vie locaux
                int pvJoueur = statsJoueur[0];
                int defenseJoueur = statsJoueur[1];
                int pvAdversaire = statsAdversaire[0];
                int defenseAdversaire = statsAdversaire[1];
                
                joueur.getPersonnage().setPointsDeVie(pvJoueur);
                adversaire.getPersonnage().setPointsDeVie(pvAdversaire);
                
                System.out.println("Stats actualisés via action_etats_personnage:");
                System.out.println("   - Joueur: PV=" + pvJoueur + ", Défense=" + defenseJoueur);
                System.out.println("   - Adversaire: PV=" + pvAdversaire + ", Défense=" + defenseAdversaire);
            } else {
                // Si aucun état n'est trouvé dans action_etats_personnage, on utilise la méthode alternative
                System.out.println("Aucun état trouvé dans action_etats_personnage, récupération directe des personnages...");
                
                // Méthode alternative: récupérer les personnages actualisés depuis MongoDB
                PersonnageMongoDAOImpl personnageDAO = PersonnageMongoDAOImpl.getInstance();
                
                Personnage personnageJoueur = personnageDAO.obtenirPersonnageParJoueurId(joueur.getId());
                Personnage personnageAdversaire = personnageDAO.obtenirPersonnageParJoueurId(adversaire.getId());
                
                if (personnageJoueur != null && personnageAdversaire != null) {
                    // Mettre à jour les points de vie locaux
                    joueur.getPersonnage().setPointsDeVie(personnageJoueur.getPointsDeVie());
                    adversaire.getPersonnage().setPointsDeVie(personnageAdversaire.getPointsDeVie());
                    
                    System.out.println("Points de vie actualisés via MongoDB - Joueur: " + 
                                      joueur.getPersonnage().getPointsDeVie() + 
                                      ", Adversaire: " + adversaire.getPersonnage().getPointsDeVie());
                } else {
                    System.err.println("Impossible de récupérer les personnages depuis la base de données.");
                }
            }
            
            // Récupérer la dernière action du combat pour informer le joueur
            String derniereAction = actionCombatDAO.obtenirDerniereAction(idCombat);
            if (derniereAction != null && !derniereAction.isEmpty()) {
                System.out.println("Dernière action du combat: " + derniereAction);
            }
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'actualisation des points de vie: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void attendreProchainTour() {
        // Simulation de l'attente pour le prochain tour
        Window fenetre = new BasicWindow("Tour " + tourActuel + " terminé");
        fenetre.setHints(Collections.singletonList(Window.Hint.CENTERED));
        
        // Définir que la fenêtre est active
        fenetreAttenteActive = true;

        Panel panel = new Panel(new GridLayout(1));
        panel.addComponent(new Label("Votre action pour le tour " + tourActuel + " est enregistrée."));
        panel.addComponent(new Label("En attente de l'action de l'adversaire..."));

        // Afficher les points de vie actuels
        panel.addComponent(new EmptySpace());
        Panel pvsPanel = new Panel(new GridLayout(2));
        Label pvJoueurLabel = new Label("PV " + joueur.getPseudo() + ": " + joueur.getPersonnage().getPointsDeVie());
        Label pvAdversaireLabel = new Label("PV " + adversaire.getPseudo() + ": " + adversaire.getPersonnage().getPointsDeVie());
        pvsPanel.addComponent(pvJoueurLabel);
        pvsPanel.addComponent(pvAdversaireLabel);
        panel.addComponent(pvsPanel);
        panel.addComponent(new EmptySpace());
        
        // Bouton pour actualiser manuellement les points de vie
        panel.addComponent(new Button("Actualiser l'état du combat", () -> {
            rafraichirPointsDeVie();
            pvJoueurLabel.setText("PV " + joueur.getPseudo() + ": " + joueur.getPersonnage().getPointsDeVie());
            pvAdversaireLabel.setText("PV " + adversaire.getPseudo() + ": " + adversaire.getPersonnage().getPointsDeVie());
            
            // Récupérer également les points de défense pour l'affichage
            try {
                String idCombat = serviceCombat.obtenirIdCombatEnCours(joueur.getId());
                if (idCombat != null) {
                    ActionCombatDAOImpl actionCombatDAO = ActionCombatDAOImpl.getInstance();
                    int[] statsJoueur = actionCombatDAO.recupererDerniersStats(idCombat, joueur.getId());
                    int[] statsAdversaire = actionCombatDAO.recupererDerniersStats(idCombat, adversaire.getId());
                    
                    if (statsJoueur != null && statsAdversaire != null) {
                        String detailsJoueur = "Détails " + joueur.getPseudo() + " - Défense: " + statsJoueur[1];
                        String detailsAdversaire = "Détails " + adversaire.getPseudo() + " - Défense: " + statsAdversaire[1];
                        
                        afficherMessageSucces("État du combat actualisé avec succès!\n\n" +
                                            detailsJoueur + "\n" + detailsAdversaire);
                        return;
                    }
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la récupération des détails de défense: " + e.getMessage());
            }
            
            // Vérifier si le combat est terminé après la mise à jour
            if (estCombatTermine()) {
                fenetre.close();
                fenetreAttenteActive = false;
                terminerCombat();
                return;
            }
            
            afficherMessageSucces("État du combat actualisé avec succès!");
        }));
        
        // Bouton pour actualiser et vérifier l'action de l'adversaire
        panel.addComponent(new Button("Vérifier action adversaire", () -> {
            // Actualiser d'abord les points de vie
            rafraichirPointsDeVie();
            pvJoueurLabel.setText("PV " + joueur.getPseudo() + ": " + joueur.getPersonnage().getPointsDeVie());
            pvAdversaireLabel.setText("PV " + adversaire.getPseudo() + ": " + adversaire.getPersonnage().getPointsDeVie());
            
            // Vérifier si le combat est terminé après la mise à jour
            if (estCombatTermine()) {
                fenetre.close();
                fenetreAttenteActive = false;
                terminerCombat();
                return;
            }
            
            // Récupérer l'action de l'adversaire
            try {
                String resultatAction = serviceCombat.obtenirResultatActionAdverse(joueur, adversaire, tourActuel);
                if (resultatAction != null && !resultatAction.equals("Aucun résultat disponible")) {
                    fenetre.close();
                    fenetreAttenteActive = false;
                    afficherMessageSucces("Action adversaire: " + resultatAction);
                    
                    // Incrémentation du tour si les deux joueurs ont joué
                    final int nouveauTour = tourActuel + 1;
                    tourActuel = nouveauTour;
                    
                    // Vérifier si on a atteint le nombre max de tours
                    if (nouveauTour > MAX_TOURS) {
                        afficherMessageSucces("Tous les tours sont terminés! Détermination du vainqueur...");
                        terminerCombat(); // Terminer le combat si on a atteint le nombre max de tours
                    } else {
                        // Sinon, passer au tour suivant
                        afficher(); // Afficher le prochain tour
                    }
                } else {
                    afficherMessageErreur("L'adversaire n'a pas encore joué. Veuillez patienter.");
                }
            } catch (Exception e) {
                afficherMessageErreur("Erreur lors de la récupération de l'action adverse: " + e.getMessage());
            }
        }));

        // Ajouter un minuteur pour vérifier périodiquement l'action de l'adversaire
        panel.addComponent(new EmptySpace());
        panel.addComponent(new Label("Vérification automatique toutes les secondes..."));
        
        fenetre.setComponent(panel);
        textGUI.addWindow(fenetre);
        
        // Ajouter un listener sur la fermeture de la fenêtre
        fenetre.addWindowListener(new WindowListenerAdapter() {
            @Override
            public void onUnhandledInput(Window window, com.googlecode.lanterna.input.KeyStroke keyStroke, java.util.concurrent.atomic.AtomicBoolean hasBeenHandled) {
                if (keyStroke.getKeyType() == com.googlecode.lanterna.input.KeyType.Escape) {
                    fenetreAttenteActive = false;
                }
            }
        });
        
        // Mettre en place un timer pour vérifier périodiquement les points de vie
        final Timer timerPV = new Timer();
        timerPV.schedule(new TimerTask() {
            @Override
            public void run() {
                textGUI.getGUIThread().invokeLater(() -> {
                    rafraichirPointsDeVie();
                    pvJoueurLabel.setText("PV " + joueur.getPseudo() + ": " + joueur.getPersonnage().getPointsDeVie());
                    pvAdversaireLabel.setText("PV " + adversaire.getPseudo() + ": " + adversaire.getPersonnage().getPointsDeVie());
                    
                    // Vérifier si le combat est terminé suite à cette mise à jour
                    if (estCombatTermine() && fenetreAttenteActive) {
                        fenetre.close();
                        fenetreAttenteActive = false;
                        timerPV.cancel(); // Arrêter les timers
                        terminerCombat();
                    }
                });
            }
        }, 500, 500); // Vérifier chaque demi-seconde
        
        // Mettre en place un timer pour vérifier périodiquement l'action de l'adversaire
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                textGUI.getGUIThread().invokeLater(() -> {
                    try {
                        String resultatAction = serviceCombat.obtenirResultatActionAdverse(joueur, adversaire, tourActuel);
                        if (resultatAction != null && !resultatAction.equals("Aucun résultat disponible")) {
                            if (fenetreAttenteActive) {
                                fenetre.close();
                                fenetreAttenteActive = false;
                                timer.cancel(); // Arrêter le timer de vérification d'action
                                timerPV.cancel(); // Arrêter le timer de vérification de PV
                                
                                afficherMessageSucces("Action adversaire: " + resultatAction);
                                
                                // Incrémentation du tour si les deux joueurs ont joué
                                // Créer une variable finale pour le nouveau tour
                                final int nouveauTour = tourActuel + 1;
                                tourActuel = nouveauTour;
                                
                                // Vérifier si on a atteint le nombre max de tours
                                if (nouveauTour > MAX_TOURS) {
                                    afficherMessageSucces("Tous les tours sont terminés! Détermination du vainqueur...");
                                    terminerCombat(); // Terminer le combat si on a atteint le nombre max de tours
                                } else {
                                    // Sinon, passer au tour suivant
                                    afficher(); // Afficher le prochain tour
                                }
                            }
                        }
                    } catch (Exception e) {
                        // Ignorer les erreurs silencieusement pour ne pas perturber l'utilisateur
                        System.err.println("Erreur lors de la vérification automatique: " + e.getMessage());
                    }
                });
            }
        }, 1000, 1000); // Vérifier toutes les secondes
    }

    private boolean estCombatTermine() {
        return joueur.getPersonnage().getPointsDeVie() <= 0 ||
               adversaire.getPersonnage().getPointsDeVie() <= 0 ||
               tourActuel > MAX_TOURS;
    }

    private void terminerCombat() {
        Joueur vainqueur = determinerVainqueur();
        
        // Obtenir l'ID du combat en cours
        String idCombat = null;
        try {
            idCombat = serviceCombat.obtenirIdCombatEnCours(joueur.getId());
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération de l'ID du combat: " + e.getMessage());
        }
        
        try {
            // Terminer officiellement le combat dans le backend
            if (idCombat != null) {
                serviceCombat.terminerCombat(joueur, adversaire, vainqueur);
                
                // Distribution des récompenses et mise à jour des statistiques
                if (vainqueur != null) {
                    // Mise à jour des statistiques du vainqueur
                    vainqueur.ajouterArgent(500); // Ajouter 500 TerraCoins
                    vainqueur.setVictoires(vainqueur.getVictoires() + 1); // Incrémenter les victoires
                    joueurDAO.mettreAJourJoueur(vainqueur);
                    
                    // Mise à jour des statistiques du perdant
                    Joueur perdant = (vainqueur.getId() == joueur.getId()) ? adversaire : joueur;
                    perdant.setDefaites(perdant.getDefaites() + 1); // Incrémenter les défaites
                    joueurDAO.mettreAJourJoueur(perdant);
                    
                    // Augmenter le niveau du royaume du vainqueur dans MongoDB
                    try {
                        RoyaumeMongoDAOImpl royaumeDAO = RoyaumeMongoDAOImpl.getInstance();
                        royaumeDAO.augmenterNiveauRoyaume(vainqueur.getId());
                        System.out.println("Niveau du royaume de " + vainqueur.getPseudo() + " augmenté");
                    } catch (Exception e) {
                        System.err.println("Erreur lors de l'augmentation du niveau du royaume: " + e.getMessage());
                    }
                } else {
                    // En cas de match nul, pas de changement aux statistiques
                }
                
                // Mettre les deux joueurs inactifs
                try {
                    joueurDAO.definirStatutConnexion(joueur.getId(), false);
                    joueurDAO.definirStatutConnexion(adversaire.getId(), false);
                    System.out.println("Statut des joueurs mis à inactif");
                } catch (Exception e) {
                    System.err.println("Erreur lors de la mise à jour du statut des joueurs: " + e.getMessage());
                }
                
                // Nettoyer les tables
                try {
                    ActionCombatDAOImpl actionCombatDAO = ActionCombatDAOImpl.getInstance();
                    actionCombatDAO.supprimerActionsCombat(idCombat);
                    actionCombatDAO.supprimerEtatsPersonnage(idCombat);
                    CombatDAOImpl combatDAO = new CombatDAOImpl();
                    combatDAO.supprimerCombatEnCours(idCombat);
                    System.out.println("Tables de combat nettoyées avec succès");
                } catch (Exception e) {
                    System.err.println("Erreur lors du nettoyage des tables: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la terminaison du combat: " + e.getMessage());
        }

        // Créer une fenêtre plus élaborée pour la fin du combat
        Window fenetre = new BasicWindow("♛ FIN DU COMBAT ♛");
        fenetre.setHints(Collections.singletonList(Window.Hint.CENTERED));

        Panel panelPrincipal = new Panel(new GridLayout(1));
        
        // Titre stylisé
        panelPrincipal.addComponent(new Label("═════════════════════════════════════").addStyle(SGR.BOLD));
        if (vainqueur != null) {
            boolean victoire = vainqueur.getId() == joueur.getId();
            panelPrincipal.addComponent(
                new Label(victoire ? "VICTOIRE !" : "DÉFAITE !")
                .addStyle(SGR.BOLD)
                .setForegroundColor(victoire ? TextColor.ANSI.GREEN : TextColor.ANSI.RED)
            );
        } else {
            panelPrincipal.addComponent(new Label("MATCH NUL !").addStyle(SGR.BOLD).setForegroundColor(TextColor.ANSI.YELLOW));
        }
        panelPrincipal.addComponent(new Label("═════════════════════════════════════").addStyle(SGR.BOLD));
        panelPrincipal.addComponent(new EmptySpace());

        // Afficher le vainqueur
        if (vainqueur != null) {
            String message = "Le vainqueur est: " + vainqueur.getPseudo();
            panelPrincipal.addComponent(new Label(message).addStyle(SGR.BOLD));
            
            // Message personnalisé selon le résultat
            if (vainqueur.getId() == joueur.getId()) {
                panelPrincipal.addComponent(new Label("Félicitations pour votre victoire!").setForegroundColor(TextColor.ANSI.GREEN));
                
                // Déterminer la raison de la victoire
                if (adversaire.getPersonnage().getPointsDeVie() <= 0) {
                    panelPrincipal.addComponent(new Label("➤ Vous avez vaincu votre adversaire en le réduisant à 0 PV!"));
                } else if (tourActuel > MAX_TOURS) {
                    panelPrincipal.addComponent(new Label("➤ Vous avez plus de points de vie que votre adversaire à la fin des " + MAX_TOURS + " tours!"));
                }
            } else {
                panelPrincipal.addComponent(new Label("Vous avez perdu. Meilleure chance la prochaine fois!").setForegroundColor(TextColor.ANSI.RED));
                
                // Déterminer la raison de la défaite
                if (joueur.getPersonnage().getPointsDeVie() <= 0) {
                    panelPrincipal.addComponent(new Label("➤ Votre adversaire vous a vaincu en vous réduisant à 0 PV!"));
                } else if (tourActuel > MAX_TOURS) {
                    panelPrincipal.addComponent(new Label("➤ Votre adversaire a plus de points de vie que vous à la fin des " + MAX_TOURS + " tours!"));
                }
            }
        } else {
            panelPrincipal.addComponent(new Label("Le combat s'est terminé par un match nul!").setForegroundColor(TextColor.ANSI.YELLOW));
            panelPrincipal.addComponent(new Label("➤ Les deux joueurs ont exactement le même nombre de points de vie!"));
        }
        
        panelPrincipal.addComponent(new EmptySpace());
        
        // Afficher les statistiques finales du combat
        panelPrincipal.addComponent(new Label("━━━━━━ RÉSUMÉ DU COMBAT ━━━━━━").addStyle(SGR.BOLD));
        panelPrincipal.addComponent(new EmptySpace());
        
        // Statistiques des joueurs
        Panel statsPanel = new Panel(new GridLayout(3));
        
        // En-têtes
        statsPanel.addComponent(new Label(""));
        statsPanel.addComponent(new Label(joueur.getPseudo()).addStyle(SGR.UNDERLINE));
        statsPanel.addComponent(new Label(adversaire.getPseudo()).addStyle(SGR.UNDERLINE));
        
        // Points de vie restants
        int pvJoueur = (int) joueur.getPersonnage().getPointsDeVie();
        int pvAdversaire = (int) adversaire.getPersonnage().getPointsDeVie();
        int pvMaxJoueur = (int) joueur.getPersonnage().getPointsDeVieMAX();
        int pvMaxAdversaire = (int) adversaire.getPersonnage().getPointsDeVieMAX();
        
        statsPanel.addComponent(new Label("PV restants:"));
        statsPanel.addComponent(new Label(pvJoueur + "/" + pvMaxJoueur));
        statsPanel.addComponent(new Label(pvAdversaire + "/" + pvMaxAdversaire));
        
        // Pourcentage des PV restants
        statsPanel.addComponent(new Label("% PV restants:"));
        int pctJoueur = (int) (100.0 * pvJoueur / pvMaxJoueur);
        int pctAdversaire = (int) (100.0 * pvAdversaire / pvMaxAdversaire);
        
        statsPanel.addComponent(new Label(pctJoueur + "%").setForegroundColor(getColorForPercentage(pctJoueur)));
        statsPanel.addComponent(new Label(pctAdversaire + "%").setForegroundColor(getColorForPercentage(pctAdversaire)));
        
        // Nombre de tours joués
        statsPanel.addComponent(new Label("Nombre de tours:"));
        statsPanel.addComponent(new Label(String.valueOf(tourActuel)));
        statsPanel.addComponent(new Label(String.valueOf(tourActuel)));
        
        panelPrincipal.addComponent(statsPanel);
        panelPrincipal.addComponent(new EmptySpace());
        
        // Résultats et récompenses
        if (vainqueur != null && vainqueur.getId() == joueur.getId()) {
            Panel recompensesPanel = new Panel(new GridLayout(1));
            recompensesPanel.addComponent(new Label("━━━━━━ RÉCOMPENSES ━━━━━━").addStyle(SGR.BOLD));
            recompensesPanel.addComponent(new EmptySpace());
            
            // Afficher les récompenses obtenues
            recompensesPanel.addComponent(new Label("✦ +500 TerraCoins").setForegroundColor(TextColor.ANSI.YELLOW));
            recompensesPanel.addComponent(new Label("✦ +1 victoire ajoutée à votre palmarès").setForegroundColor(TextColor.ANSI.GREEN));
            recompensesPanel.addComponent(new Label("✦ Niveau de royaume augmenté!").setForegroundColor(TextColor.ANSI.CYAN));
            
            panelPrincipal.addComponent(recompensesPanel);
            panelPrincipal.addComponent(new EmptySpace());
        }
        
        // Bouton pour retourner au menu principal
        panelPrincipal.addComponent(new Button("Retour au menu principal", () -> {
            fenetre.close();
            retourMenuPrincipal();
        }));

        fenetre.setComponent(panelPrincipal);
        textGUI.addWindowAndWait(fenetre);
    }
    
    /**
     * Retourne une couleur appropriée selon le pourcentage de points de vie
     * @param percentage Le pourcentage de points de vie
     * @return La couleur correspondante
     */
    private TextColor getColorForPercentage(int percentage) {
        if (percentage <= 25) {
            return TextColor.ANSI.RED;
        } else if (percentage <= 50) {
            return TextColor.ANSI.YELLOW;
        } else {
            return TextColor.ANSI.GREEN;
        }
    }

    private Joueur determinerVainqueur() {
        // Si l'un des joueurs est mort, l'autre est le vainqueur
        if (joueur.getPersonnage().getPointsDeVie() <= 0) {
            return adversaire;
        } else if (adversaire.getPersonnage().getPointsDeVie() <= 0) {
            return joueur;
        } 
        
        // Si on a atteint le nombre maximum de tours, le vainqueur est celui avec le plus de PV
        if (tourActuel > MAX_TOURS) {
            if (joueur.getPersonnage().getPointsDeVie() > adversaire.getPersonnage().getPointsDeVie()) {
                return joueur;
            } else if (adversaire.getPersonnage().getPointsDeVie() > joueur.getPersonnage().getPointsDeVie()) {
                return adversaire;
            } else {
                return null; // Match nul si même nombre de PV
            }
        }
        
        // Si le combat n'est pas terminé, on n'a pas encore de vainqueur
        return null;
    }

    private void retourMenuPrincipal() {
        // TODO: Implémentation pour retourner au menu principal
        new EcranPrincipal(null, joueurDAO, joueur.getPseudo(), screen).afficher();
    }

    private void afficherMessageErreur(String message) {
        new MessageDialogBuilder()
                .setTitle("Erreur")
                .setText(message)
                .addButton(MessageDialogButton.OK)
                .build()
                .showDialog(textGUI);
    }
    
    private void afficherMessageSucces(String message) {
        new MessageDialogBuilder()
                .setTitle("Information")
                .setText(message)
                .addButton(MessageDialogButton.OK)
                .build()
                .showDialog(textGUI);
    }

    /**
     * Crée un panneau détaillé affichant les statistiques et l'état des deux combattants
     * @return Un Panel contenant les informations détaillées des combattants
     */
    private Panel creerPanneauStatistiquesComplet() {
        Panel statsPanel = new Panel(new GridLayout(2).setLeftMarginSize(1).setRightMarginSize(1));
        
        // Récupérer l'ID du combat en cours
        String idCombat = null;
        int[] statsJoueur = null;
        int[] statsAdversaire = null;
        
        try {
            idCombat = serviceCombat.obtenirIdCombatEnCours(joueur.getId());
            if (idCombat != null) {
                ActionCombatDAOImpl actionCombatDAO = ActionCombatDAOImpl.getInstance();
                statsJoueur = actionCombatDAO.recupererDerniersStats(idCombat, joueur.getId());
                statsAdversaire = actionCombatDAO.recupererDerniersStats(idCombat, adversaire.getId());
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des statistiques: " + e.getMessage());
        }
        
        // Panneau pour le joueur
        Panel joueurPanel = new Panel(new GridLayout(1));
        joueurPanel.addComponent(new Label("━━━━━━ VOTRE PERSONNAGE ━━━━━━").addStyle(SGR.BOLD));
        joueurPanel.addComponent(new Label(joueur.getPseudo() + " (" + joueur.getPersonnage().getNom() + ")").addStyle(SGR.UNDERLINE));
        
        // Barres de progression pour les points de vie
        double pvMax = joueur.getPersonnage().getPointsDeVieMAX();
        double pvActuel = joueur.getPersonnage().getPointsDeVie();
        double pourcentagePV = Math.min(100, Math.max(0, (pvActuel / pvMax) * 100));
        
        String barrePV = genererBarreProgression(pourcentagePV);
        joueurPanel.addComponent(new Label("PV: " + (int)pvActuel + "/" + (int)pvMax));
        joueurPanel.addComponent(new Label(barrePV));
        
        // Afficher les statistiques additionnelles du joueur
        joueurPanel.addComponent(new EmptySpace(new TerminalSize(0, 1)));
        joueurPanel.addComponent(new Label("Attaque: " + joueur.getPersonnage().getDegats()));
        joueurPanel.addComponent(new Label("Défense: " + joueur.getPersonnage().getResistance()));
        
        // Afficher les informations de défense supplémentaires si disponibles
        if (statsJoueur != null && statsJoueur.length >= 2) {
            int defenseSupplementaire = statsJoueur[1];
            if (defenseSupplementaire > 0) {
                joueurPanel.addComponent(new Label("Bonus défense: +" + defenseSupplementaire).addStyle(SGR.BOLD).setForegroundColor(TextColor.ANSI.GREEN));
            }
        }
        
        // Panneau pour l'adversaire
        Panel adversairePanel = new Panel(new GridLayout(1));
        adversairePanel.addComponent(new Label("━━━━━━ ADVERSAIRE ━━━━━━").addStyle(SGR.BOLD));
        adversairePanel.addComponent(new Label(adversaire.getPseudo() + " (" + adversaire.getPersonnage().getNom() + ")").addStyle(SGR.UNDERLINE));
        
        // Barres de progression pour les points de vie de l'adversaire
        double pvMaxAdv = adversaire.getPersonnage().getPointsDeVieMAX();
        double pvActuelAdv = adversaire.getPersonnage().getPointsDeVie();
        double pourcentagePVAdv = Math.min(100, Math.max(0, (pvActuelAdv / pvMaxAdv) * 100));
        
        String barrePVAdv = genererBarreProgression(pourcentagePVAdv);
        adversairePanel.addComponent(new Label("PV: " + (int)pvActuelAdv + "/" + (int)pvMaxAdv));
        adversairePanel.addComponent(new Label(barrePVAdv));
        
        // Afficher les statistiques additionnelles de l'adversaire
        adversairePanel.addComponent(new EmptySpace(new TerminalSize(0, 1)));
        adversairePanel.addComponent(new Label("Attaque: " + adversaire.getPersonnage().getDegats()));
        adversairePanel.addComponent(new Label("Défense: " + adversaire.getPersonnage().getResistance()));
        
        // Afficher les informations de défense supplémentaires si disponibles
        if (statsAdversaire != null && statsAdversaire.length >= 2) {
            int defenseSupplementaire = statsAdversaire[1];
            if (defenseSupplementaire > 0) {
                adversairePanel.addComponent(new Label("Bonus défense: +" + defenseSupplementaire).addStyle(SGR.BOLD).setForegroundColor(TextColor.ANSI.GREEN));
            }
        }
        
        // Ajouter les deux panneaux au panneau principal
        statsPanel.addComponent(joueurPanel);
        statsPanel.addComponent(adversairePanel);
        
        return statsPanel;
    }
    
    /**
     * Génère une barre de progression visuelle basée sur un pourcentage
     * @param pourcentage Le pourcentage de remplissage de la barre (0-100)
     * @return Une chaîne représentant la barre de progression
     */
    private String genererBarreProgression(double pourcentage) {
        int longueurTotale = 20;
        int remplissage = (int) Math.round(pourcentage * longueurTotale / 100);
        
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < longueurTotale; i++) {
            if (i < remplissage) {
                sb.append("█");
            } else {
                sb.append(" ");
            }
        }
        sb.append("] ").append((int)pourcentage).append("%");
        
        return sb.toString();
    }

    /**
     * Affiche l'écran de résolution d'un tour avec les résultats des actions des deux joueurs
     */
    private void afficherEcranResolutionTour() {
        try {
            String idCombat = serviceCombat.obtenirIdCombatEnCours(joueur.getId());
            if (idCombat == null) {
                afficherMessageErreur("Impossible de trouver l'ID du combat en cours.");
                return;
            }
            
            // Récupérer les actions exécutées par les deux joueurs pour ce tour
            ActionCombatDAOImpl actionCombatDAO = ActionCombatDAOImpl.getInstance();
            ResultSet resultats = actionCombatDAO.obtenirActionsTour(idCombat, tourActuel);
            
            if (resultats == null) {
                afficherMessageErreur("Erreur lors de la récupération des actions du tour.");
                return;
            }
            
            // Créer une fenêtre avec un titre plus élaboré
            Window fenetre = new BasicWindow("★ RÉSOLUTION DU TOUR " + tourActuel + " ★");
            fenetre.setHints(Collections.singletonList(Window.Hint.CENTERED));
            
            Panel panelPrincipal = new Panel(new GridLayout(1));
            
            // Ajouter un en-tête stylisé
            panelPrincipal.addComponent(new Label("═════════════════════════════════════").addStyle(SGR.BOLD));
            panelPrincipal.addComponent(new Label("RÉSULTATS DES ACTIONS").addStyle(SGR.BOLD));
            panelPrincipal.addComponent(new Label("═════════════════════════════════════").addStyle(SGR.BOLD));
            panelPrincipal.addComponent(new EmptySpace());
            
            // Initialiser les chaînes pour stocker les descriptions des actions
            String actionJoueurLocal = null;
            String actionAdversaire = null;
            double vieJoueurApres = joueur.getPersonnage().getPointsDeVie();
            double vieAdversaireApres = adversaire.getPersonnage().getPointsDeVie();
            
            // Parcourir les résultats et extraire les actions
            while (resultats.next()) {
                int joueurId = resultats.getInt("joueur_id");
                String typeAction = resultats.getString("type_action");
                
                // Récupérer aussi les paramètres et les points de vie après l'action
                String parametres = resultats.getString("parametres");
                double vieRestanteJoueur = resultats.getDouble("vie_restante_joueur");
                double vieRestanteAdversaire = resultats.getDouble("vie_restante_adversaire");
                
                if (joueurId == joueur.getId()) {
                    actionJoueurLocal = formaterDescriptionAction(typeAction, parametres, true);
                    vieJoueurApres = vieRestanteJoueur;
                    vieAdversaireApres = vieRestanteAdversaire;
                } else if (joueurId == adversaire.getId()) {
                    actionAdversaire = formaterDescriptionAction(typeAction, parametres, false);
                }
            }
            
            // Afficher les actions dans des panneaux séparés avec des bordures
            if (actionJoueurLocal != null) {
                Panel actionJoueurPanel = new Panel(new GridLayout(1));
                actionJoueurPanel.addComponent(new Label("Votre action:").addStyle(SGR.BOLD).setForegroundColor(TextColor.ANSI.BLUE));
                actionJoueurPanel.addComponent(new Label(actionJoueurLocal));
                panelPrincipal.addComponent(actionJoueurPanel);
                panelPrincipal.addComponent(new EmptySpace());
            }
            
            if (actionAdversaire != null) {
                Panel actionAdversairePanel = new Panel(new GridLayout(1));
                actionAdversairePanel.addComponent(new Label("Action de " + adversaire.getPseudo() + ":").addStyle(SGR.BOLD).setForegroundColor(TextColor.ANSI.RED));
                actionAdversairePanel.addComponent(new Label(actionAdversaire));
                panelPrincipal.addComponent(actionAdversairePanel);
                panelPrincipal.addComponent(new EmptySpace());
            }
            
            // Afficher le résumé des points de vie après ce tour
            panelPrincipal.addComponent(new Label("⚔ RÉSULTAT DU TOUR ⚔").addStyle(SGR.BOLD));
            panelPrincipal.addComponent(new EmptySpace());
            
            // Créer un résumé visuel des changements de points de vie
            Panel resultatPanel = new Panel(new GridLayout(2));
            resultatPanel.addComponent(new Label(joueur.getPseudo() + ":"));
            resultatPanel.addComponent(new Label(String.format("%.0f PV", vieJoueurApres)));
            resultatPanel.addComponent(new Label(adversaire.getPseudo() + ":"));
            resultatPanel.addComponent(new Label(String.format("%.0f PV", vieAdversaireApres)));
            panelPrincipal.addComponent(resultatPanel);
            
            panelPrincipal.addComponent(new EmptySpace());
            
            // Déterminer qui est en avantage actuellement
            if (vieJoueurApres > vieAdversaireApres) {
                panelPrincipal.addComponent(new Label("Vous avez l'avantage!").setForegroundColor(TextColor.ANSI.GREEN));
            } else if (vieAdversaireApres > vieJoueurApres) {
                panelPrincipal.addComponent(new Label(adversaire.getPseudo() + " a l'avantage!").setForegroundColor(TextColor.ANSI.RED));
            } else {
                panelPrincipal.addComponent(new Label("Le combat est très serré!").setForegroundColor(TextColor.ANSI.YELLOW));
            }
            
            panelPrincipal.addComponent(new EmptySpace());
            
            // Vérifier si le combat est terminé après ce tour
            if (vieJoueurApres <= 0 || vieAdversaireApres <= 0) {
                String message = vieJoueurApres <= 0 ? 
                        adversaire.getPseudo() + " vous a vaincu!" : 
                        "Vous avez vaincu " + adversaire.getPseudo() + "!";
                panelPrincipal.addComponent(new Label(message).addStyle(SGR.BOLD));
            } 
            // Vérifier si c'était le dernier tour
            else if (tourActuel >= MAX_TOURS) {
                panelPrincipal.addComponent(new Label("C'était le dernier tour!").addStyle(SGR.BOLD));
                String vainqueur = vieJoueurApres > vieAdversaireApres ? 
                        "Vous êtes le vainqueur!" : 
                        (vieAdversaireApres > vieJoueurApres ? adversaire.getPseudo() + " est le vainqueur!" : "Match nul!");
                panelPrincipal.addComponent(new Label(vainqueur).addStyle(SGR.BOLD));
            }
            
            // Bouton pour continuer
            // Créer des copies finales des variables pour utilisation dans le lambda
            final double vieJoueurFinal = vieJoueurApres;
            final double vieAdversaireFinal = vieAdversaireApres;
            
            panelPrincipal.addComponent(new Button("Continuer", () -> {
                fenetre.close();
                
                // Si le combat est terminé après ce tour, afficher l'écran de fin
                if (vieJoueurFinal <= 0 || vieAdversaireFinal <= 0 || tourActuel >= MAX_TOURS) {
                    terminerCombat();
                } else {
                    // Sinon, passer au tour suivant - incrémenter avant le lambda
                    int nouveauTour = tourActuel + 1;
                    tourActuel = nouveauTour;
                    afficher();
                }
            }));
            
            fenetre.setComponent(panelPrincipal);
            textGUI.addWindowAndWait(fenetre);
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'affichage de l'écran de résolution: " + e.getMessage());
            e.printStackTrace();
            afficherMessageErreur("Erreur lors de l'affichage de l'écran de résolution: " + e.getMessage());
        }
    }
    
    /**
     * Formate la description d'une action pour l'affichage
     * @param typeAction Le type d'action (Attaque, Defense, etc.)
     * @param parametres Les paramètres additionnels de l'action
     * @param estJoueurLocal Indique si l'action est celle du joueur local
     * @return Une chaîne formatée décrivant l'action
     */
    private String formaterDescriptionAction(String typeAction, String parametres, boolean estJoueurLocal) {
        String nomPersonnage = estJoueurLocal ? joueur.getPersonnage().getNom() : adversaire.getPersonnage().getNom();
        
        switch (typeAction.toLowerCase()) {
            case "attaque":
                return "► " + nomPersonnage + " attaque avec son arme principale!";
            case "defense":
                return "► " + nomPersonnage + " renforce sa défense!";
            case "competence":
                if (parametres != null && !parametres.isEmpty()) {
                    return "► " + nomPersonnage + " utilise la compétence: " + parametres + "!";
                } else {
                    return "► " + nomPersonnage + " utilise une compétence spéciale!";
                }
            default:
                return "► " + nomPersonnage + " effectue une action: " + typeAction;
        }
    }

    /**
     * Affiche l'écran d'attente pendant que l'adversaire joue son tour
     */
    private void afficherEcranAttente() {
        // Marquer la fenêtre d'attente comme active
        fenetreAttenteActive = true;
        
        // Créer une fenêtre d'attente améliorée
        Window fenetre = new BasicWindow("⌛ En attente de " + adversaire.getPseudo() + " ⌛");
        fenetre.setHints(Collections.singletonList(Window.Hint.CENTERED));
        
        Panel panelPrincipal = new Panel(new GridLayout(1));
        
        // Titre et informations sur le tour
        panelPrincipal.addComponent(new Label("TOUR " + tourActuel + "/" + MAX_TOURS).addStyle(SGR.BOLD));
        panelPrincipal.addComponent(new EmptySpace());
        
        // Affichage dynamique des informations de combat actuelles
        panelPrincipal.addComponent(new Label("━━━━━━ ÉTAT DU COMBAT ━━━━━━").addStyle(SGR.BOLD));
        
        // Panel pour les statistiques actuelles
        Panel statsPanel = creerPanneauStatistiquesComplet();
        panelPrincipal.addComponent(statsPanel);
        panelPrincipal.addComponent(new EmptySpace());
        
        // Afficher un message d'attente animé
        Label labelAttente = new Label("En attente de l'action de " + adversaire.getPseudo() + "...");
        panelPrincipal.addComponent(labelAttente);
        panelPrincipal.addComponent(new EmptySpace());
        
        // Ajouter un compteur de vérifications
        Label compteurLabel = new Label("Vérifications effectuées: 0");
        panelPrincipal.addComponent(compteurLabel);
        
        // Ajouter des conseils stratégiques pour le joueur
        Panel conseilsPanel = new Panel(new GridLayout(1));
        conseilsPanel.addComponent(new EmptySpace());
        conseilsPanel.addComponent(new Label("━━━━━━ CONSEILS STRATÉGIQUES ━━━━━━").addStyle(SGR.BOLD));
        conseilsPanel.addComponent(new EmptySpace());
        
        // Afficher des conseils aléatoires
        String[] conseils = {
            "Les attaques sont efficaces, mais ne négligez pas la défense!",
            "Essayez de garder au moins 30% de vos points de vie pour les derniers tours.",
            "La défense peut être cruciale si vous êtes en avantage au niveau des PV.",
            "Utilisez vos compétences au moment opportun pour renverser la situation.",
            "Observez le comportement de votre adversaire pour anticiper sa stratégie.",
            "Si vous avez peu de PV, une bonne défense peut vous donner une chance de récupérer."
        };
        
        // Sélectionner 2 conseils aléatoires
        Random random = new Random();
        for (int i = 0; i < 2; i++) {
            int index = random.nextInt(conseils.length);
            conseilsPanel.addComponent(new Label("• " + conseils[index]).setForegroundColor(TextColor.ANSI.YELLOW));
        }
        
        panelPrincipal.addComponent(conseilsPanel);
        panelPrincipal.addComponent(new EmptySpace());
        
                    // Bouton pour actualiser manuellement
        panelPrincipal.addComponent(new Button("Actualiser maintenant", () -> {
            // Incrémenter le compteur de vérifications manuelles
            compteurVerifications++;
            compteurLabel.setText("Vérifications effectuées: " + compteurVerifications);
            
            // Vérifier si l'adversaire a joué
            try {
                final String idCombatLocal = serviceCombat.obtenirIdCombatEnCours(joueur.getId());
                if (idCombatLocal != null) {
                    ActionCombatDAOImpl actionCombatDAO = ActionCombatDAOImpl.getInstance();
                    boolean adversairePret = actionCombatDAO.joueurAEffectueAction(idCombatLocal, tourActuel, adversaire.getId());
                    
                    if (adversairePret) {
                        // L'adversaire a joué son tour
                        fenetre.close();
                        fenetreAttenteActive = false;
                        
                        String resultatAction = serviceCombat.obtenirResultatActionAdverse(joueur, adversaire, tourActuel);
                        afficherMessageSucces("Action adversaire: " + resultatAction);
                        
                        // Passer au tour suivant ou à la résolution
                        boolean joueurLocalPret = actionCombatDAO.joueurAEffectueAction(idCombatLocal, tourActuel, joueur.getId());
                        if (joueurLocalPret && adversairePret) {
                            // Les deux joueurs ont joué, afficher la résolution du tour
                            afficherEcranResolutionTour();
                        } else {
                            // Si pour une raison quelconque, le joueur local n'a pas encore joué
                            // (ce qui ne devrait pas arriver normalement), retourner à l'écran principal
                            afficher();
                        }
                    } else {
                        // L'adversaire n'a pas encore joué
                        afficherMessageErreur(adversaire.getPseudo() + " n'a pas encore joué son tour.");
                    }
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la vérification manuelle: " + e.getMessage());
                e.printStackTrace();
            }
        }));
        
        // Créer un timer pour animer le texte d'attente (points de suspension)
        Timer animationTimer = new Timer();
        final String[] animations = {"En attente de l'action de " + adversaire.getPseudo() + ".", 
                                   "En attente de l'action de " + adversaire.getPseudo() + "..", 
                                   "En attente de l'action de " + adversaire.getPseudo() + "..."};
        final int[] animIndex = {0};
        
        animationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (fenetreAttenteActive) {
                    try {
                        textGUI.getGUIThread().invokeLater(() -> {
                            labelAttente.setText(animations[animIndex[0]]);
                            animIndex[0] = (animIndex[0] + 1) % animations.length;
                        });
                    } catch (Exception e) {
                        // Ignorer les erreurs d'animation
                    }
                } else {
                    animationTimer.cancel();
                }
            }
        }, 500, 500);
        
        fenetre.setComponent(panelPrincipal);
        textGUI.addWindowAndWait(fenetre);
        
        // Nettoyage
        fenetreAttenteActive = false;
        animationTimer.cancel();
    }
}
