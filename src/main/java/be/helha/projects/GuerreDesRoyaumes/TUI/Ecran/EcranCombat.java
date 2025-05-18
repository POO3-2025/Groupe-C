package be.helha.projects.GuerreDesRoyaumes.TUI.Ecran;

import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceCombat;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.screen.Screen;

import java.util.Collections;
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

    public EcranCombat(JoueurDAO joueurDAO, WindowBasedTextGUI textGUI, Screen screen,
                       Joueur joueur, Joueur adversaire, ServiceCombat serviceCombat) {
        this.joueurDAO = joueurDAO;
        this.textGUI = textGUI;
        this.screen = screen;
        this.joueur = joueur;
        this.adversaire = adversaire;
        this.serviceCombat = serviceCombat;
    }

    public void afficher() {

        // verifier si les deux joueur sont pret pour passer au tour suivant
        if (!serviceCombat.sontJoueursPrets(joueur, adversaire)) {
            afficherFenetreAttenteEtPlanifierVerification();
            return;
        }


        // Vérifier si c'est le tour du joueur
        boolean estTourDuJoueur = serviceCombat.estTourDuJoueur(joueur, adversaire);

        Window fenetre = new BasicWindow("Combat - Tour " + tourActuel + "/" + MAX_TOURS);
        fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(1));

        // Statut du combat
        panel.addComponent(new Label("Votre personnage: " + joueur.getPersonnage().getNom() +
                " (PV: " + joueur.getPersonnage().getPointsDeVie() + ")"));
        panel.addComponent(new Label("Adversaire: " + adversaire.getPersonnage().getNom() +
                " (PV: " + adversaire.getPersonnage().getPointsDeVie() + ")"));

        panel.addComponent(new EmptySpace());

        // Si ce n'est pas le tour du joueur, afficher un message d'attente
        if (!estTourDuJoueur) {
            panel.addComponent(new Label("Attendez que l'adversaire joue son tour..."));

            // Panel pour les boutons d'attente
            Panel panelAttente = new Panel(new GridLayout(2));

            panelAttente.addComponent(new Button("Vérifier", () -> {
                fenetre.close();
                verifierTourAdversaire();
            }));

            // Ajouter un bouton "Débloquer" pour forcer manuellement le jeu à continuer
            panelAttente.addComponent(new Button("Débloquer", () -> {
                fenetre.close();
                debloquerCombat();
            }));

            panel.addComponent(panelAttente);
        } else {
            panel.addComponent(new Label("Tour " + tourActuel + " - Choisissez votre action:"));

            // Actions possibles
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
        }

        fenetre.setComponent(panel);
        textGUI.addWindowAndWait(fenetre);
    }

    private void afficherFenetreAttenteEtPlanifierVerification() {
        Window fenetreAttente = new BasicWindow("Attente");
        fenetreAttente.setHints(Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new LinearLayout(Direction.VERTICAL));
        panel.addComponent(new Label("En attente de l'adversaire..."));

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

    private void verifierTourAdversaire() {
        try {
            // Actualiser l'état du combat avant vérification
            // Méthode de simulation pour débloquer l'attente mutuelle
            // En mode développement, on simule une action de l'adversaire après quelques vérifications
            if (!serviceCombat.estTourDuJoueur(joueur, adversaire)) {
                // Détecter le blocage: si après plusieurs vérifications, toujours pas de tour
                if (++compteurVerifications > 2) {
                    // Force l'action de l'adversaire en mode développement
                    Window fenetreInfo = new BasicWindow("Déblocage...");
                    fenetreInfo.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

                    Panel panelInfo = new Panel(new GridLayout(1));
                    panelInfo.addComponent(new Label("Tentative de déblocage du combat..."));

                    fenetreInfo.setComponent(panelInfo);
                    textGUI.addWindow(fenetreInfo);

                    // Simuler un délai
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }

                    // Forcer le changement de tour
                    boolean changeOk = serviceCombat.forcerChangementTour(joueur, adversaire);

                    // Fermer la fenêtre d'information
                    fenetreInfo.close();

                    if (!changeOk) {
                        afficherMessageErreur("Échec du déblocage, veuillez réessayer.");
                        compteurVerifications = 0; // Réinitialiser pour pouvoir retenter
                        afficher();
                        return;
                    }

                    // Réinitialiser le compteur
                    compteurVerifications = 0;
                } else {
                    // Si pas encore assez de vérifications, informer le joueur
                    afficherMessageErreur("L'adversaire n'a pas encore joué. Veuillez patienter. ("
                            + compteurVerifications + "/3)");
                    afficher(); // Réafficher l'écran d'attente
                    return;
                }
            } else {
                // Réinitialiser le compteur car on a bien obtenu le tour
                compteurVerifications = 0;
            }

            // Si c'est maintenant notre tour, afficher le résultat de l'action adverse
            String resultatActionAdverse = serviceCombat.obtenirResultatActionAdverse(joueur, adversaire, tourActuel);

            // Afficher le résultat de l'action adverse
            Window fenetreResultat = new BasicWindow("Action de l'adversaire");
            fenetreResultat.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

            Panel panel = new Panel(new GridLayout(1));
            panel.addComponent(new Label(resultatActionAdverse));
            panel.addComponent(new Label("Points de vie restants:"));
            panel.addComponent(new Label("- Vous: " + joueur.getPersonnage().getPointsDeVie()));
            panel.addComponent(new Label("- Adversaire: " + adversaire.getPersonnage().getPointsDeVie()));

            panel.addComponent(new Button("Continuer", () -> {
                fenetreResultat.close();
                afficher(); // Réafficher l'écran principal pour jouer notre tour
            }));

            fenetreResultat.setComponent(panel);
            textGUI.addWindowAndWait(fenetreResultat);
        } catch (Exception e) {
            afficherMessageErreur("Erreur lors de la vérification du tour: " + e.getMessage());
            afficher(); // Réafficher l'écran d'attente
        }
    }

    private void executerAction(String typeAction) {
        try {
            // Exécuter l'action du joueur
            String resultatJoueur = serviceCombat.executerAction(joueur, adversaire, typeAction, tourActuel);

            // Afficher le résultat immédiat de notre action
            Window fenetreResultat = new BasicWindow("Votre action");
            fenetreResultat.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

            Panel panel = new Panel(new GridLayout(1));
            panel.addComponent(new Label("Vous avez choisi: " + typeAction));
            panel.addComponent(new Label(resultatJoueur));
            panel.addComponent(new EmptySpace());
            panel.addComponent(new Label("En attente de l'action de l'adversaire..."));

            panel.addComponent(new Button("Continuer", () -> {
                fenetreResultat.close();

                // Vérifier si le combat est terminé après notre action
                if (estCombatTermine()) {
                    terminerCombat();
                } else {
                    // Afficher l'écran d'attente pour l'action de l'adversaire
                    afficher();
                }
            }));

            fenetreResultat.setComponent(panel);
            textGUI.addWindowAndWait(fenetreResultat);

        } catch (Exception e) {
            afficherMessageErreur("Erreur lors de l'exécution de l'action: " + e.getMessage());
        }
    }

    private boolean estCombatTermine() {
        return tourActuel >= MAX_TOURS ||
                joueur.getPersonnage().getPointsDeVie() <= 0 ||
                adversaire.getPersonnage().getPointsDeVie() <= 0;
    }

    private Window afficherFenetreAttente(String message) {
        Window fenetre = new BasicWindow("Attente");
        fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(1));
        panel.addComponent(new Label(message));

        fenetre.setComponent(panel);
        textGUI.addWindow(fenetre);

        return fenetre;
    }

    private void terminerCombat() {
        Joueur vainqueur = determinerVainqueur();
        serviceCombat.terminerCombat(joueur, adversaire, vainqueur);

        Window fenetre = new BasicWindow("Fin du combat");
        fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(1));

        if (vainqueur != null) {
            panel.addComponent(new Label("Le vainqueur est: " + vainqueur.getPseudo()));

            if (vainqueur.getId() == joueur.getId()) {
                panel.addComponent(new Label("Félicitations, vous avez gagné!"));
            } else {
                panel.addComponent(new Label("Vous avez perdu. Meilleure chance la prochaine fois!"));
            }
        } else {
            panel.addComponent(new Label("Match nul! Aucun vainqueur."));
        }

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Label("Retour au menu principal dans 5 secondes..."));

        fenetre.setComponent(panel);
        textGUI.addWindow(fenetre);



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
        if (joueur.getPersonnage().getPointsDeVie() <= 0) {
            return adversaire;
        } else if (adversaire.getPersonnage().getPointsDeVie() <= 0) {
            return joueur;
        } else if (joueur.getPersonnage().getPointsDeVie() > adversaire.getPersonnage().getPointsDeVie()) {
            return joueur;
        } else if (adversaire.getPersonnage().getPointsDeVie() > joueur.getPersonnage().getPointsDeVie()) {
            return adversaire;
        } else {
            return null; // Match nul
        }
    }

    private void retourMenuPrincipal() {
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

    /**
     * Méthode pour débloquer manuellement le combat quand il est bloqué
     */
    private void debloquerCombat() {
        // Afficher une fenêtre de progression
        Window fenetre = new BasicWindow("Déblocage manuel");
        fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(1));
        panel.addComponent(new Label("Déblocage du combat en cours..."));
        panel.addComponent(new Label("Tentative de synchronisation des états..."));

        fenetre.setComponent(panel);
        textGUI.addWindow(fenetre);

        try {
            // Première étape: synchroniser les points de vie (recharger depuis la BDD)
            panel.addComponent(new Label("Synchronisation des points de vie..."));

            // Recharger les joueurs depuis la base de données pour s'assurer d'avoir les données les plus récentes
            Joueur joueurMisAJour = joueurDAO.obtenirJoueurParId(joueur.getId());
            Joueur adversaireMisAJour = joueurDAO.obtenirJoueurParId(adversaire.getId());

            if (joueurMisAJour != null && joueurMisAJour.getPersonnage() != null) {
                joueur.getPersonnage().setPointsDeVie(joueurMisAJour.getPersonnage().getPointsDeVie());
            }

            if (adversaireMisAJour != null && adversaireMisAJour.getPersonnage() != null) {
                adversaire.getPersonnage().setPointsDeVie(adversaireMisAJour.getPersonnage().getPointsDeVie());
            }

            panel.addComponent(new Label("Points de vie synchronisés."));

            // Deuxième étape: forcer le changement de tour
            panel.addComponent(new Label("Forçage du tour..."));
            boolean succes = serviceCombat.forcerChangementTour(joueur, adversaire);

            // Fermer la fenêtre de progression
            fenetre.close();

            if (succes) {
                // Mettre à jour le numéro de tour local
                tourActuel = serviceCombat.getTourActuel(joueur, adversaire);

                // Si le déblocage a réussi, passer au tour du joueur
                afficherMessageErreur("Combat débloqué avec succès. C'est maintenant votre tour.\nTour actuel: " + tourActuel);

                // Forcer l'affichage de l'écran de combat actualisé
                afficher();
            } else {
                // Si échec, proposer des solutions alternatives
                Window fenetreEchec = new BasicWindow("Échec du déblocage");
                fenetreEchec.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

                Panel panelEchec = new Panel(new GridLayout(1));
                panelEchec.addComponent(new Label("Le déblocage automatique a échoué."));
                panelEchec.addComponent(new EmptySpace());
                panelEchec.addComponent(new Label("Que voulez-vous faire ?"));

                // Offrir trois options
                Panel optionsPanel = new Panel(new GridLayout(3));

                optionsPanel.addComponent(new Button("Réessayer", () -> {
                    fenetreEchec.close();
                    debloquerCombat();
                }));

                optionsPanel.addComponent(new Button("Forcer tour suivant", () -> {
                    fenetreEchec.close();
                    // Solution plus radicale: passer directement au tour suivant
                    forcerTourSuivant();
                }));

                optionsPanel.addComponent(new Button("Abandonner combat", () -> {
                    fenetreEchec.close();

                    // Demander confirmation avant d'abandonner
                    MessageDialogButton result = new MessageDialogBuilder()
                            .setTitle("Confirmation")
                            .setText("Êtes-vous sûr de vouloir abandonner ce combat?")
                            .addButton(MessageDialogButton.Yes)
                            .addButton(MessageDialogButton.No)
                            .build()
                            .showDialog(textGUI);

                    if (result == MessageDialogButton.Yes) {
                        // Terminer le combat immédiatement avec défaite
                        serviceCombat.terminerCombat(joueur, adversaire, adversaire);
                        retourMenuPrincipal();
                    } else {
                        // Revenir à l'écran de combat
                        afficher();
                    }
                }));

                panelEchec.addComponent(optionsPanel);
                fenetreEchec.setComponent(panelEchec);
                textGUI.addWindowAndWait(fenetreEchec);
            }
        } catch (Exception e) {
            fenetre.close();
            afficherMessageErreur("Erreur lors du déblocage: " + e.getMessage());
            afficher();
        }
    }

    /**
     * Force le passage au tour suivant en cas de blocage persistant
     */
    private void forcerTourSuivant() {
        Window fenetre = new BasicWindow("Passage forcé");
        fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(1));
        panel.addComponent(new Label("Passage forcé au tour suivant..."));

        fenetre.setComponent(panel);
        textGUI.addWindow(fenetre);

        try {
            // 1. Réinitialiser l'état du tour actuel
            String[] actions = {"defense"}; // Utiliser defense car c'est l'action la plus sûre
            String actionSimulee = actions[0];

            // 2. Vérifier si c'est au tour du joueur actuel
            boolean estTourDuJoueurActuel = serviceCombat.estTourDuJoueur(joueur, adversaire);

            if (!estTourDuJoueurActuel) {
                // Si ce n'est pas notre tour, forcer une action pour l'adversaire
                serviceCombat.executerAction(adversaire, joueur, actionSimulee, tourActuel);
                panel.addComponent(new Label("Action simulée pour l'adversaire."));
            }

            // 3. Maintenant s'assurer que c'est notre tour
            boolean forcageTourReussi = serviceCombat.forcerChangementTour(joueur, adversaire);

            if (forcageTourReussi) {
                panel.addComponent(new Label("Tour de jeu récupéré avec succès."));

                // 4. Obtenir le numéro de tour officiel
                tourActuel = serviceCombat.getTourActuel(joueur, adversaire);
                panel.addComponent(new Label("Tour actuel: " + tourActuel));
            } else {
                panel.addComponent(new Label("Échec du changement de tour."));
            }

            // Attendre un peu pour que l'utilisateur puisse lire
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            // Fermer la fenêtre
            fenetre.close();

            // Afficher un message de confirmation
            if (forcageTourReussi) {
                afficherMessageErreur("Tour forcé avec succès. C'est maintenant votre tour.");
            } else {
                afficherMessageErreur("Échec du forçage de tour. Veuillez réessayer ou abandonner.");
            }

            // Afficher l'écran principal actualisé
            afficher();
        } catch (Exception e) {
            fenetre.close();
            afficherMessageErreur("Erreur lors du forçage du tour: " + e.getMessage());
            afficher();
        }
    }

    /**
     * Vérifie et incrémente le tour si nécessaire
     */
    private void verifierEtIncrementerTour() {
        if (serviceCombat.estTourDuJoueur(joueur, adversaire)) {
            // Possible passage au tour suivant, vérifier l'état du jeu
            if (tourActuel < MAX_TOURS &&
                    joueur.getPersonnage().getPointsDeVie() > 0 &&
                    adversaire.getPersonnage().getPointsDeVie() > 0) {
                // Nous sommes peut-être passés au tour suivant
                // Obtenir les résultats du tour précédent
                String resultatsPrecedents = serviceCombat.obtenirResultatActionAdverse(joueur, adversaire, tourActuel);

                // Si aucun résultat disponible, possiblement nouveau tour
                if (resultatsPrecedents.equals("Aucun résultat disponible")) {
                    tourActuel++;
                    System.out.println("DEBUG: Incrémentation du tour à " + tourActuel);
                }
            }
        }
    }
}
