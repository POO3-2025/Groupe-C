package be.helha.projects.GuerreDesRoyaumes.TUI.Ecran;

import be.helha.projects.GuerreDesRoyaumes.DAO.CombatSessionMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.FileAttenteCombatDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.CombatSessionMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DTO.CombatResolver;
import be.helha.projects.GuerreDesRoyaumes.DTO.SkillManager;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.MongoDBConnectionException;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceCombat;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.screen.Screen;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Écran pour gérer la file d'attente de combat et le matchmaking.
 */
public class EcranFileAttenteCombat {
    private final JoueurDAO joueurDAO;
    private final WindowBasedTextGUI textGUI;
    private final Screen screen;
    private final Joueur joueur;
    private final ServiceCombat serviceCombat;
    private final FileAttenteCombatDAO fileAttenteCombatDAO;
    private Timer timerVerification;
    private Window fenetreAttente;

    public EcranFileAttenteCombat(JoueurDAO joueurDAO, WindowBasedTextGUI textGUI, Screen screen,
                           Joueur joueur, ServiceCombat serviceCombat, FileAttenteCombatDAO fileAttenteCombatDAO) {
        this.joueurDAO = joueurDAO;
        this.textGUI = textGUI;
        this.screen = screen;
        this.joueur = joueur;
        this.serviceCombat = serviceCombat;
        this.fileAttenteCombatDAO = fileAttenteCombatDAO;
    }

    /**
     * Affiche l'écran de file d'attente de combat.
     */
    public void afficher() {
        fenetreAttente = new BasicWindow("File d'attente de combat");
        fenetreAttente.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(1));
        panel.addComponent(new Label("Recherche d'un adversaire en cours..."));
        panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));
        
        // Ajouter le joueur à la file d'attente
        boolean ajoutReussi = fileAttenteCombatDAO.ajouterJoueurEnAttente(joueur);
        
        if (!ajoutReussi) {
            panel.addComponent(new Label("Erreur lors de l'ajout à la file d'attente!"));
            panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));
        } else {
            panel.addComponent(new Label("Vous êtes dans la file d'attente. Veuillez patienter..."));
            panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));
            
            // Afficher un indicateur d'activité
            panel.addComponent(new Label("Temps d'attente estimé: 30 secondes"));
            panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));
        }
        
        panel.addComponent(new Button("Annuler", () -> {
            arreterRechercheAdversaire();
            fenetreAttente.close();
            retourMenuPrincipal();
        }));

        fenetreAttente.setComponent(panel);
        textGUI.addWindow(fenetreAttente);

        // Démarrer la vérification périodique pour le matchmaking
        demarrerRechercheAdversaire();
    }

    /**
     * Démarre la recherche périodique d'un adversaire.
     */
    private void demarrerRechercheAdversaire() {
        // Arrêter tout timer précédent
        arreterRechercheAdversaire();
        
        timerVerification = new Timer();
        timerVerification.scheduleAtFixedRate(new TimerTask() {
            private int compteur = 0;
            
            @Override
            public void run() {
                compteur++;
                
                // Vérifier si un match a été trouvé
                int adversaireId = fileAttenteCombatDAO.verifierMatchTrouve(joueur.getId());
                if (adversaireId > 0) {
                    // Match trouvé
                    Joueur adversaire = joueurDAO.obtenirJoueurParId(adversaireId);
                    
                    // Annuler le timer
                    cancel();
                    timerVerification.cancel();
                    
                    // Démarrer le combat sur l'UI thread
                    textGUI.getGUIThread().invokeLater(() -> demarrerCombat(adversaire));
                    return;
                }
                
                // Après 3 vérifications (15 secondes), essayer de trouver un adversaire activement
                if (compteur % 3 == 0) {
                    Joueur adversaire = fileAttenteCombatDAO.trouverAdversaire(joueur);
                    if (adversaire != null) {
                        // Un adversaire a été trouvé
                        cancel();
                        timerVerification.cancel();
                        
                        // Démarrer le combat sur l'UI thread
                        textGUI.getGUIThread().invokeLater(() -> demarrerCombat(adversaire));
                    }
                }
                
                // Si aucun adversaire n'est trouvé après 60 secondes (12 vérifications), abandonner
                if (compteur >= 12) {
                    cancel();
                    timerVerification.cancel();
                    
                    // Retirer le joueur de la file d'attente
                    fileAttenteCombatDAO.retirerJoueurEnAttente(joueur.getId());
                    
                    // Informer l'utilisateur sur l'UI thread
                    textGUI.getGUIThread().invokeLater(() -> {
                        afficherMessageErreur("Aucun adversaire trouvé après 60 secondes. Veuillez réessayer plus tard.");
                        fenetreAttente.close();
                        retourMenuPrincipal();
                    });
                }
            }
        }, 5000, 5000); // Vérifier toutes les 5 secondes
    }

    /**
     * Arrête la recherche périodique d'un adversaire.
     */
    private void arreterRechercheAdversaire() {
        if (timerVerification != null) {
            timerVerification.cancel();
            timerVerification = null;
        }
        
        // Retirer le joueur de la file d'attente
        fileAttenteCombatDAO.retirerJoueurEnAttente(joueur.getId());
    }

    /**
     * Démarre un combat avec l'adversaire trouvé.
     * 
     * @param adversaire L'adversaire trouvé
     */
    private void demarrerCombat(Joueur adversaire) {
        try {
            // Fermer la fenêtre d'attente
            fenetreAttente.close();
            
            // Afficher un message indiquant qu'un adversaire a été trouvé
            Window fenetreMatch = new BasicWindow("Adversaire trouvé!");
            fenetreMatch.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));
            
            Panel panel = new Panel(new GridLayout(1));
            panel.addComponent(new Label("Adversaire trouvé: " + adversaire.getPseudo()));
            panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));
            panel.addComponent(new Label("Initialisation du combat..."));
            
            fenetreMatch.setComponent(panel);
            textGUI.addWindow(fenetreMatch);
            
            // Initialiser le combat
            serviceCombat.initialiserCombat(joueur, adversaire, new ArrayList<>());
            
            // Attendre 2 secondes pour montrer le message d'initialisation
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    textGUI.getGUIThread().invokeLater(() -> {
                        fenetreMatch.close();
                        
                        try {
                            // Obtenir les instances des dépendances nécessaires
                            CombatSessionMongoDAO sessionDAO = CombatSessionMongoDAOImpl.getInstance();
                            SkillManager skillManager = new SkillManager();
                            
                            // Lancer l'écran de combat
                            new EcranCombat(joueurDAO, textGUI, screen, joueur, adversaire, serviceCombat, sessionDAO, skillManager).afficher();
                        } catch (MongoDBConnectionException e) {
                            afficherMessageErreur("Erreur de connexion à MongoDB: " + e.getMessage());
                            retourMenuPrincipal();
                        }
                    });
                }
            }, 2000);
            
        } catch (Exception e) {
            afficherMessageErreur("Erreur lors du démarrage du combat: " + e.getMessage());
            retourMenuPrincipal();
        }
    }

    /**
     * Affiche un message d'erreur.
     * 
     * @param message Le message d'erreur à afficher
     */
    private void afficherMessageErreur(String message) {
        new MessageDialogBuilder()
                .setTitle("Erreur")
                .setText(message)
                .addButton(MessageDialogButton.OK)
                .build()
                .showDialog(textGUI);
    }

    /**
     * Retourne au menu principal.
     */
    private void retourMenuPrincipal() {
        new EcranPrincipal(null, joueurDAO, joueur.getPseudo(), screen).afficher();
    }
} 