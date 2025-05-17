package be.helha.projects.GuerreDesRoyaumes.TUI.Ecran;

import be.helha.projects.GuerreDesRoyaumes.DAO.CombatDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceCombat;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.screen.Screen;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class EcranSelectionAdversaire {
    private final JoueurDAO joueurDAO;
    private final CombatDAO combatDAO;
    private final WindowBasedTextGUI textGUI;
    private final Screen screen;
    private final String pseudoJoueur;
    private final ServiceCombat serviceCombat;
    private final AtomicBoolean rechercheContinue = new AtomicBoolean(true);
    private final AtomicBoolean dialogueDemandeAffiche = new AtomicBoolean(false);
    private int derniereDemandeCombatId = 0;
    private ScheduledExecutorService scheduler;
    private Window fenetre;
    private ComboBox<String> comboJoueurs;
    private Button btnSelectionner;
    private Panel mainPanel;

    public EcranSelectionAdversaire(JoueurDAO joueurDAO, WindowBasedTextGUI textGUI, Screen screen, String pseudoJoueur, ServiceCombat serviceCombat) {
        this.joueurDAO = joueurDAO;
        this.textGUI = textGUI;
        this.screen = screen;
        this.pseudoJoueur = pseudoJoueur;
        this.serviceCombat = serviceCombat;
        // Récupérer le CombatDAO depuis le ServiceCombat
        this.combatDAO = serviceCombat.getCombatDAO();
    }

    public void afficher() {
        fenetre = new BasicWindow("Sélection d'adversaire");
        fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        mainPanel = new Panel(new GridLayout(1));

        // Titre
        mainPanel.addComponent(new Label("Sélectionnez un adversaire connecté:"));
        mainPanel.addComponent(new Label("Recherche en cours..."));

        // Créer une liste déroulante mais ne pas l'ajouter au panel immédiatement
        List<String> listeVide = new ArrayList<>();
        comboJoueurs = new ComboBox<>(listeVide);
        // On n'ajoute pas la combobox tout de suite, elle sera ajoutée dynamiquement 
        // quand des joueurs seront trouvés

        // Bouton de sélection (initialement non visible)
        btnSelectionner = new Button("Sélectionner", this::selectionnerAdversaire);
        // Le bouton est créé mais n'est pas ajouté immédiatement

        // Bouton retour (toujours visible)
        mainPanel.addComponent(new Button("Retour", () -> {
            arreterRecherche();
            fenetre.close();
            try {
                Joueur joueur = joueurDAO.obtenirJoueurParPseudo(pseudoJoueur);
                // Désactiver le statut du joueur (le rendre indisponible pour le combat quand il clique sur retour)
                try {
                    // Définir le statut comme inactif
                    joueurDAO.definirStatutConnexion(joueur.getId(), false);
                    System.out.println("Joueur " + joueur.getPseudo() + " désactivé pour le combat");
                } catch (Exception e) {
                    System.err.println("Erreur lors de la désactivation du statut de combat: " + e.getMessage());
                }
                
                new EcranPrincipal(null, joueurDAO, pseudoJoueur, screen).afficher();
            } catch (Exception e) {
                afficherMessageErreur("Erreur lors du retour au menu principal: " + e.getMessage());
            }
        }));

        // Démarrer la recherche automatique des joueurs actifs
        demarrerRechercheAutomatique();

        fenetre.setComponent(mainPanel);
        // Quand la fenêtre se ferme, arrêter la recherche
        fenetre.addWindowListener(new WindowListenerAdapter() {
            @Override
            public void onUnhandledInput(Window window, com.googlecode.lanterna.input.KeyStroke keyStroke, AtomicBoolean hasBeenHandled) {
                if (keyStroke.getKeyType() == com.googlecode.lanterna.input.KeyType.Escape) {
                    arreterRecherche();
                }
            }
        });
        textGUI.addWindowAndWait(fenetre);
    }

    private void demarrerRechercheAutomatique() {
        // Réinitialiser le flag
        rechercheContinue.set(true);
        
        // Créer un thread séparé pour les requêtes à la base de données
        scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            if (rechercheContinue.get()) {
                // Exécuter la recherche dans un thread séparé
                actualiserListeJoueursActifs();
            }
        }, 0, 2, TimeUnit.SECONDS);  // Actualiser tous les 2 secondes
    }

    private void arreterRecherche() {
        rechercheContinue.set(false);
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(500, TimeUnit.MILLISECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
            }
        }
    }

    private void actualiserListeJoueursActifs() {
        try {
            // Obtenir la liste des joueurs actifs
            List<Joueur> joueursActifs = joueurDAO.obtenirJoueursActifs();
            List<String> pseudosActifs = new ArrayList<>();

            if (joueursActifs != null) {
                // Filtrer pour ne pas inclure le joueur actuel
                for (Joueur j : joueursActifs) {
                    if (j != null && j.getPseudo() != null && !j.getPseudo().equals(pseudoJoueur)) {
                        pseudosActifs.add(j.getPseudo());
                    }
                }
            }
            
            // Vérifier si le joueur actuel a des demandes de combat en attente
            // Ne pas vérifier si un dialogue est déjà affiché
            if (!dialogueDemandeAffiche.get()) {
                Joueur joueurActuel = joueurDAO.obtenirJoueurParPseudo(pseudoJoueur);
                
                if (joueurActuel != null) {
                    // Utiliser combatDAO au lieu de joueurDAO
                    int idDemandeur = combatDAO.verifierDemandesCombat(joueurActuel.getId());
                    // Vérifier si c'est une nouvelle demande
                    if (idDemandeur > 0 && idDemandeur != derniereDemandeCombatId) {
                        // Enregistrer cette demande comme traitée
                        derniereDemandeCombatId = idDemandeur;
                        
                        // Une demande de combat existe pour ce joueur
                        Joueur demandeur = joueurDAO.obtenirJoueurParId(idDemandeur);
                        if (demandeur != null) {
                            // S'assurer que le demandeur est toujours actif
                            boolean demandeurEstActif = false;
                            for (Joueur j : joueursActifs) {
                                if (j.getId() == idDemandeur) {
                                    demandeurEstActif = true;
                                    break;
                                }
                            }
                            
                            if (demandeurEstActif) {
                                // Marquer qu'un dialogue est en cours d'affichage
                                dialogueDemandeAffiche.set(true);
                                
                                // Afficher une boîte de dialogue pour accepter ou refuser le combat
                                textGUI.getGUIThread().invokeLater(() -> {
                                    MessageDialogButton choix = new MessageDialogBuilder()
                                            .setTitle("Demande de combat")
                                            .setText(demandeur.getPseudo() + " vous a défié en combat. Acceptez-vous ?")
                                            .addButton(MessageDialogButton.Yes)
                                            .addButton(MessageDialogButton.No)
                                            .build()
                                            .showDialog(textGUI);
                                    
                                    // Marquer que le dialogue n'est plus affiché
                                    dialogueDemandeAffiche.set(false);
                                    
                                    if (choix == MessageDialogButton.Yes) {
                                        // Accepter la demande de combat en utilisant combatDAO
                                        boolean demandeAcceptee = combatDAO.accepterDemandeCombat(idDemandeur, joueurActuel.getId());
                                        
                                        if (demandeAcceptee) {
                                            // Arrêter la recherche et passer à l'écran de préparation de combat
                                            arreterRecherche();
                                            fenetre.close();
                                            new EcranPreparationCombat(joueurDAO, textGUI, screen, pseudoJoueur, demandeur.getPseudo(), serviceCombat).afficher();
                                        } else {
                                            afficherMessageErreur("Erreur lors de l'acceptation de la demande de combat");
                                        }
                                    } else {
                                        // Refuser la demande de combat en utilisant combatDAO
                                        combatDAO.supprimerDemandeCombat(idDemandeur, joueurActuel.getId());
                                        // Réinitialiser pour pouvoir traiter les prochaines demandes
                                        derniereDemandeCombatId = 0;
                                    }
                                });
                            } else {
                                // Le demandeur n'est plus actif, supprimer la demande avec combatDAO
                                combatDAO.supprimerDemandeCombat(idDemandeur, joueurActuel.getId());
                                derniereDemandeCombatId = 0;
                            }
                        }
                    }
                }
            }

            // Mettre à jour l'interface utilisateur dans le thread UI
            // Seulement si aucun dialogue n'est affiché, pour éviter les perturbations d'interface
            if (!dialogueDemandeAffiche.get()) {
                textGUI.getGUIThread().invokeLater(() -> {
                    // Mise à jour du message en fonction des joueurs disponibles
                    if (mainPanel.getChildCount() > 1) {
                        Component messageComponent = mainPanel.getChildrenList().get(1);
                        if (messageComponent instanceof Label) {
                            Label messageLabel = (Label) messageComponent;
                            if (pseudosActifs.isEmpty()) {
                                messageLabel.setText("Aucun adversaire disponible, recherche en cours...");
                            } else {
                                messageLabel.setText(pseudosActifs.size() + " adversaire(s) trouvé(s)");
                            }
                        }
                    }
                    
                    // Gestion de l'affichage de la ComboBox
                    boolean comboBoxPresente = isComponentPresent(comboJoueurs);
                    
                    // Gestion de l'affichage du bouton Sélectionner
                    boolean btnSelectionnerPresent = isComponentPresent(btnSelectionner);
                    
                    if (pseudosActifs.isEmpty()) {
                        // Aucun joueur actif : retirer la combobox et le bouton s'ils sont présents
                        if (comboBoxPresente) {
                            mainPanel.removeComponent(comboJoueurs);
                        }
                        if (btnSelectionnerPresent) {
                            mainPanel.removeComponent(btnSelectionner);
                        }
                    } else {
                        // Des joueurs actifs : ajouter la combobox et le bouton s'ils ne sont pas présents
                        if (!comboBoxPresente) {
                            // Ajouter après le message (index 1)
                            mainPanel.addComponent(2, comboJoueurs);
                        }
                        if (!btnSelectionnerPresent) {
                            // Ajouter après la combobox ou après le message s'il n'y a pas de combobox
                            int indexBouton = comboBoxPresente ? 3 : 2;
                            mainPanel.addComponent(indexBouton, btnSelectionner);
                        }
                        
                        // Mettre à jour les éléments de la liste
                        // Sauvegarder la sélection actuelle si elle existe
                        String selectionActuelle = comboJoueurs.getSelectedItem();
                        
                        // Mettre à jour la combobox
                        comboJoueurs.clearItems();
                        for (String pseudo : pseudosActifs) {
                            comboJoueurs.addItem(pseudo);
                        }
                        
                        // Essayer de restaurer la sélection précédente
                        if (selectionActuelle != null && pseudosActifs.contains(selectionActuelle)) {
                            comboJoueurs.setSelectedItem(selectionActuelle);
                        } else if (!pseudosActifs.isEmpty()) {
                            comboJoueurs.setSelectedIndex(0);
                        }
                    }
                });
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'actualisation des joueurs actifs: " + e.getMessage());
        }
    }

    /**
     * Méthode utilitaire qui vérifie si un composant est présent dans le panel principal
     * @param component Le composant à rechercher
     * @return true si le composant est présent, false sinon
     */
    private boolean isComponentPresent(Component component) {
        for (int i = 0; i < mainPanel.getChildCount(); i++) {
            if (mainPanel.getChildrenList().get(i) == component) {
                return true;
            }
        }
        return false;
    }

    private void selectionnerAdversaire() {
        String pseudoAdversaire = comboJoueurs.getSelectedItem();

        if (pseudoAdversaire == null || pseudoAdversaire.isEmpty()) {
            afficherMessageErreur("Veuillez sélectionner un adversaire");
            return;
        }

        try {
            // Récupérer le joueur actuel
            Joueur joueur = joueurDAO.obtenirJoueurParPseudo(pseudoJoueur);
            
            // Récupérer l'adversaire sélectionné
            Joueur adversaire = joueurDAO.obtenirJoueurParPseudo(pseudoAdversaire);

            if (joueur == null || adversaire == null) {
                afficherMessageErreur("Joueur ou adversaire introuvable");
                return;
            }

            // Envoyer une demande de combat en utilisant combatDAO
            boolean demandeCombatEnvoyee = combatDAO.envoyerDemandeCombat(joueur.getId(), adversaire.getId());
            
            if (demandeCombatEnvoyee) {
                // Afficher un message de confirmation
                afficherMessageInfo("Demande de combat envoyée à " + pseudoAdversaire + ". En attente de réponse...");
                
                // Continuer à rechercher pour voir si l'adversaire accepte
                // La mise à jour se fait automatiquement via le thread de recherche existant
            } else {
                afficherMessageErreur("Erreur lors de l'envoi de la demande de combat");
            }

        } catch (Exception e) {
            afficherMessageErreur("Erreur lors de la sélection: " + e.getMessage());
        }
    }

    private void afficherMessageErreur(String message) {
        new MessageDialogBuilder()
                .setTitle("Erreur")
                .setText(message)
                .addButton(MessageDialogButton.OK)
                .build()
                .showDialog(textGUI);
    }

    private void afficherMessageInfo(String message) {
        new MessageDialogBuilder()
                .setTitle("Information")
                .setText(message)
                .addButton(MessageDialogButton.OK)
                .build()
                .showDialog(textGUI);
    }
}