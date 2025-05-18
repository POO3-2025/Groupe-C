package be.helha.projects.GuerreDesRoyaumes.TUI.Ecran;

import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.ActionCombatDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.PersonnageMongoDAOImpl;
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

import java.util.Collections;
import java.util.List;
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
        // Vérifier si les deux joueurs sont prêts pour passer au tour suivant
        if (!serviceCombat.sontJoueursPrets(joueur, adversaire)) {
            afficherFenetreAttenteEtPlanifierVerification();
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

        Window fenetre = new BasicWindow("Combat - Tour " + tourActuel + "/" + MAX_TOURS);
        fenetre.setHints(Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(1));

        // Statut du combat
        panel.addComponent(new Label("=== Informations du combat ==="));
        panel.addComponent(new Label("Tour actuel: " + tourActuel + "/" + MAX_TOURS));
        panel.addComponent(new EmptySpace());
        
        // Informations sur les joueurs
        panel.addComponent(new Label("=== Votre personnage ==="));
        Label nomJoueurLabel = new Label("Nom: " + joueur.getPersonnage().getNom());
        Label pvJoueurLabel = new Label("Points de vie: " + joueur.getPersonnage().getPointsDeVie());
        panel.addComponent(nomJoueurLabel);
        panel.addComponent(pvJoueurLabel);
        panel.addComponent(new Label("Attaque: " + joueur.getPersonnage().getDegats()));
        panel.addComponent(new Label("Défense: " + joueur.getPersonnage().getResistance()));
        panel.addComponent(new EmptySpace());
        
        panel.addComponent(new Label("=== Adversaire ==="));
        Label nomAdversaireLabel = new Label("Nom: " + adversaire.getPersonnage().getNom());
        Label pvAdversaireLabel = new Label("Points de vie: " + adversaire.getPersonnage().getPointsDeVie());
        panel.addComponent(nomAdversaireLabel);
        panel.addComponent(pvAdversaireLabel);
        panel.addComponent(new EmptySpace());
        
        // Bouton d'actualisation
        panel.addComponent(new Button("Actualiser l'état du combat", () -> {
            rafraichirPointsDeVie();
            // Mettre à jour les labels avec les informations fraîches
            pvJoueurLabel.setText("Points de vie: " + joueur.getPersonnage().getPointsDeVie());
            pvAdversaireLabel.setText("Points de vie: " + adversaire.getPersonnage().getPointsDeVie());
            
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
            
            // Vérifier si le combat est terminé après l'actualisation
            if (estCombatTermine()) {
                fenetre.close();
                terminerCombat();
                return;
            }
            
            afficherMessageSucces("État du combat actualisé avec succès!");
        }));
        panel.addComponent(new EmptySpace());
        
        panel.addComponent(new Label("Choisissez votre action pour ce tour:"));
        
        // Panel pour les actions principales
        Panel actionsPanel = new Panel(new GridLayout(2));
        
        // Bouton d'attaque
        actionsPanel.addComponent(new Button("Attaquer", () -> {
            try {
                // Récupérer l'ID du combat en cours
                String idCombat = serviceCombat.obtenirIdCombatEnCours(joueur.getId());
                if (idCombat == null) {
                    afficherMessageErreur("Impossible de trouver l'ID du combat en cours.");
                    return;
                }
                
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
                double defenseJoueur =  joueur.getPersonnage().getResistance();
                double defenseAdversaire =  adversaire.getPersonnage().getResistance();
                
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
                    
                    fenetre.close();
                    terminerCombat();
                    return;
                }
                
                // Afficher le message et rester sur l'écran de combat
                new MessageDialogBuilder()
                    .setTitle("Attaque réussie")
                    .setText(message + "\n\nVous devez maintenant attendre le tour de votre adversaire.")
                    .addButton(MessageDialogButton.OK)
                    .build()
                    .showDialog(textGUI);
                
                // Fermer la fenêtre principale et passer à la phase d'attente
                fenetre.close();
                attendreProchainTour();
                
            } catch (Exception e) {
                e.printStackTrace();
                afficherMessageErreur("Erreur lors de l'exécution de l'attaque: " + e.getMessage());
            }
        }));
        
        // Bouton de défense
        actionsPanel.addComponent(new Button("Se défendre", () -> {
            try {
                // Récupérer l'ID du combat en cours
                String idCombat = serviceCombat.obtenirIdCombatEnCours(joueur.getId());
                if (idCombat == null) {
                    afficherMessageErreur("Impossible de trouver l'ID du combat en cours.");
                    return;
                }
                    
                // Calculer le bonus de défense
                double bonusDefense = calculerBonusDefenseItems();
                double defenseTotal = joueur.getPersonnage().getResistance() + bonusDefense;
                
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
                
                // Afficher le message de confirmation sans fermer la fenêtre principale
                String message = "Vous avez choisi la défense. Bonus de " + 
                                defenseTotal + " points de défense." +
                                "\n\nVous devez maintenant attendre le tour de votre adversaire.";
                
                new MessageDialogBuilder()
                    .setTitle("Défense activée")
                    .setText(message)
                    .addButton(MessageDialogButton.OK)
                    .build()
                    .showDialog(textGUI);
                
                // Fermer la fenêtre et attendre le tour de l'adversaire
                fenetre.close();
                attendreProchainTour();
                
            } catch (Exception e) {
                e.printStackTrace();
                afficherMessageErreur("Erreur lors de l'exécution de la défense: " + e.getMessage());
            }
        }));
        
        // Bouton pour utiliser une compétence
        actionsPanel.addComponent(new Button("Utiliser Compétence", () -> {
            fenetre.close();
            afficherChoixCompetence();
        }));
        
        // Bouton pour utiliser une potion
        actionsPanel.addComponent(new Button("Utiliser Potion", () -> {
            fenetre.close();
            afficherChoixPotion();
        }));
        
        panel.addComponent(actionsPanel);
        panel.addComponent(new EmptySpace());
        
        // Bouton pour confirmer les choix
        panel.addComponent(new Button("Confirmer mes choix", () -> {
            // TODO: Implémenter la confirmation des choix
            // serviceCombat.confirmerAction(joueur, adversaire, tourActuel);
            afficherMessageSucces("Choix confirmés pour le tour " + tourActuel);
            // fenetre.close();
            // attendreProchainTour();
        }));
        
        panel.addComponent(new EmptySpace());

        // Bouton pour abandonner le combat
        panel.addComponent(new Button("Abandonner le Combat", () -> {
            MessageDialogButton reponse = new MessageDialogBuilder()
                .setTitle("Abandonner")
                .setText("Êtes-vous sûr de vouloir abandonner ce combat ? Vous perdrez automatiquement.")
                .addButton(MessageDialogButton.Yes)
                .addButton(MessageDialogButton.No)
                .build()
                .showDialog(textGUI);

            if (reponse == MessageDialogButton.Yes) {
                fenetre.close();
                // TODO: Implémentation de l'abandon
                // serviceCombat.abandonnerCombat(joueur, adversaire);
                // retourMenuPrincipal();
            }
        }));

        fenetre.setComponent(panel);
        textGUI.addWindowAndWait(fenetre);
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

        Panel panel = new Panel(new GridLayout(1));
        panel.addComponent(new Label("Vous avez choisi: " + typeAction));
        panel.addComponent(new Label("Voulez-vous confirmer cette action?"));
        
        Panel boutonsPanel = new Panel(new GridLayout(2));
        boutonsPanel.addComponent(new Button("Confirmer", () -> {
            // TODO: Implémenter la confirmation de l'action
            fenetre.close();
            // attendreProchainTour();
        }));
        
        boutonsPanel.addComponent(new Button("Annuler", () -> {
            fenetre.close();
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
                    tourActuel++;
                    
                    // Vérifier si on a atteint le nombre max de tours
                    if (tourActuel > MAX_TOURS) {
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
                                tourActuel++;
                                
                                // Vérifier si on a atteint le nombre max de tours
                                if (tourActuel > MAX_TOURS) {
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
        
        // Terminer officiellement le combat dans le backend
        try {
            if (idCombat != null) {
                serviceCombat.terminerCombat(joueur, adversaire, vainqueur);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la terminaison du combat: " + e.getMessage());
        }

        Window fenetre = new BasicWindow("Fin du combat");
        fenetre.setHints(Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(1));

        if (vainqueur != null) {
            panel.addComponent(new Label("Le vainqueur est: " + vainqueur.getPseudo()));

            if (vainqueur.getId() == joueur.getId()) {
                panel.addComponent(new Label("Félicitations, vous avez gagné!"));
                
                // Déterminer la raison de la victoire
                if (adversaire.getPersonnage().getPointsDeVie() <= 0) {
                    panel.addComponent(new Label("Vous avez vaincu votre adversaire en le réduisant à 0 PV!"));
                } else if (tourActuel > MAX_TOURS) {
                    panel.addComponent(new Label("Vous avez plus de points de vie que votre adversaire à la fin des " + MAX_TOURS + " tours!"));
                }
                
                // TODO: Attribuer récompenses, expérience, etc.
            } else {
                panel.addComponent(new Label("Vous avez perdu. Meilleure chance la prochaine fois!"));
                
                // Déterminer la raison de la défaite
                if (joueur.getPersonnage().getPointsDeVie() <= 0) {
                    panel.addComponent(new Label("Votre adversaire vous a vaincu en vous réduisant à 0 PV!"));
                } else if (tourActuel > MAX_TOURS) {
                    panel.addComponent(new Label("Votre adversaire a plus de points de vie que vous à la fin des " + MAX_TOURS + " tours!"));
                }
            }
        } else {
            panel.addComponent(new Label("Match nul! Les deux joueurs ont le même nombre de PV à la fin des " + MAX_TOURS + " tours!"));
        }

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Label("Points de vie finaux:"));
        panel.addComponent(new Label("- " + joueur.getPseudo() + ": " + joueur.getPersonnage().getPointsDeVie()));
        panel.addComponent(new Label("- " + adversaire.getPseudo() + ": " + adversaire.getPersonnage().getPointsDeVie()));
        panel.addComponent(new EmptySpace());
        panel.addComponent(new Label("Retour au menu principal dans 5 secondes..."));

        fenetre.setComponent(panel);
        textGUI.addWindowAndWait(fenetre);

        // Compte à rebours de 5 secondes
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                fenetre.close();
                retourMenuPrincipal();
            }
        }, 5000); // 5 secondes
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
}
