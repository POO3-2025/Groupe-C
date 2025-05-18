package be.helha.projects.GuerreDesRoyaumes.TUI.Ecran;

import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Slot;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceCombat;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.screen.Screen;

import java.util.ArrayList;
import java.util.List;

public class EcranPreparationCombat {
    private final JoueurDAO joueurDAO;
    private final WindowBasedTextGUI textGUI;
    private final Screen screen;
    private final String pseudoJoueur;
    private final String pseudoAdversaire;
    private final ServiceCombat serviceCombat;
    private boolean estHote;
    private List<Item> itemsSelectionnes;
    private Joueur joueur;
    private Joueur adversaire;

    public EcranPreparationCombat(JoueurDAO joueurDAO, WindowBasedTextGUI textGUI, Screen screen,
                                  String pseudoJoueur, String pseudoAdversaire, ServiceCombat serviceCombat) {
        this.joueurDAO = joueurDAO;
        this.textGUI = textGUI;
        this.screen = screen;
        this.pseudoJoueur = pseudoJoueur;
        this.pseudoAdversaire = pseudoAdversaire;
        this.serviceCombat = serviceCombat;

        // Déterminer si ce joueur est l'hôte (par exemple, celui qui a initié le combat)
        // Cette logique peut être ajustée selon votre système
        this.estHote = pseudoJoueur.compareTo(pseudoAdversaire) < 0;
        this.itemsSelectionnes = new ArrayList<>();
    }

    public void afficher() {
        Window fenetre = new BasicWindow("Préparation au combat");
        fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(1));

        panel.addComponent(new Label("Préparation au combat contre: " + pseudoAdversaire));
        panel.addComponent(new EmptySpace());

        // Récupérer les informations des joueurs
        try {
            joueur = joueurDAO.obtenirJoueurParPseudo(pseudoJoueur);
            adversaire = joueurDAO.obtenirJoueurParPseudo(pseudoAdversaire);

            if (joueur == null || adversaire == null) {
                throw new RuntimeException("Impossible de récupérer les informations des joueurs");
            }

            // Initialiser le combat
            serviceCombat.initialiserCombat(joueur, adversaire, new ArrayList<>());

            // Ajout du bouton "Préparer mon combat"
            Button btnPreparerCombat = new Button("Préparer mon combat", () -> {
                //ici afficher l'ecran de gestiond de coffre en mode combat
                fenetre.close();
                new EcranPrincipal(null, joueurDAO, joueur.getPseudo(), screen).afficherEcranGestionCoffre(joueur, true);
            });
            panel.addComponent(btnPreparerCombat);
            panel.addComponent(new EmptySpace());

            // En mode développement, attendre la confirmation de l'utilisateur
            panel.addComponent(new Label("Préparation complète!"));
            panel.addComponent(new Label("Attendez que l'adversaire confirme..."));

            // Simuler une attente pour la confirmation
            // Dans une implémentation réelle, on attendrait un message réseau
            Panel panelTemporaire = new Panel(new GridLayout(1));
            Button btnPretSimule = new Button("Simuler adversaire prêt", () -> {
                fenetre.close();
                Window fenetreConfirmation = new BasicWindow("Confirmation");
                fenetreConfirmation.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

                Panel panelConfirmation = new Panel(new GridLayout(1));
                panelConfirmation.addComponent(new Label("L'adversaire est prêt!"));
                panelConfirmation.addComponent(new EmptySpace());
                panelConfirmation.addComponent(new Button("Commencer le combat", () -> {
                    fenetreConfirmation.close();
                    new EcranCombat(joueurDAO, textGUI, screen, joueur, adversaire, serviceCombat).afficher();
                }));

                fenetreConfirmation.setComponent(panelConfirmation);
                textGUI.addWindowAndWait(fenetreConfirmation);
            });

            panelTemporaire.addComponent(btnPretSimule);
            panel.addComponent(panelTemporaire);

            // Créer un thread de vérification pour voir si l'adversaire a annulé le combat
            demarrerVerificationAnnulationCombat(joueur, adversaire, fenetre);

            // Bouton d'annulation
            panel.addComponent(new Button("Annuler", () -> {
                // Afficher une boîte de dialogue de confirmation avec avertissement de sanction
                MessageDialogButton confirmation = new MessageDialogBuilder()
                        .setTitle("Confirmation d'annulation")
                        .setText("Voulez-vous vraiment annuler ce combat ?\nUne sanction de 500 TerraCoins sera appliquée à votre compte.")
                        .addButton(MessageDialogButton.Yes)
                        .addButton(MessageDialogButton.No)
                        .build()
                        .showDialog(textGUI);
                
                if (confirmation == MessageDialogButton.Yes) {
                    try {
                        if (joueur != null && adversaire != null) {
                            // Obtenir le CombatDAO depuis le ServiceCombat
                            be.helha.projects.GuerreDesRoyaumes.DAO.CombatDAO combatDAO = serviceCombat.getCombatDAO();
                            
                            // Supprimer le combat en cours
                            combatDAO.supprimerCombatEnCours(joueur.getId(), adversaire.getId());
                            
                            // Appliquer la sanction financière (500 TerraCoins) en utilisant la méthode standard
                            if (joueur.getArgent() >= 500) {
                                joueur.retirerArgent(500);
                                // Mettre à jour le joueur dans la base de données
                                joueurDAO.mettreAJourJoueur(joueur);
                                
                                // Informer l'utilisateur de la sanction appliquée
                                new MessageDialogBuilder()
                                        .setTitle("Sanction appliquée")
                                        .setText("Une sanction de 500 TerraCoins a été appliquée à votre compte.")
                                        .addButton(MessageDialogButton.OK)
                                        .build()
                                        .showDialog(textGUI);
                            } else {
                                new MessageDialogBuilder()
                                        .setTitle("Fonds insuffisants")
                                        .setText("Vous n'avez pas assez de TerraCoins. Votre solde a été mis à 0.")
                                        .addButton(MessageDialogButton.OK)
                                        .build()
                                        .showDialog(textGUI);
                                
                                // Mettre le solde à 0 si le joueur n'a pas assez d'argent
                                joueur.setArgent(0);
                                joueurDAO.mettreAJourJoueur(joueur);
                            }
                            
                            // Envoyer un message à l'adversaire pour l'informer de l'annulation
                            // Dans une implémentation réelle, on utiliserait le réseau
                            // Ici, on simule avec un message d'information pour le développement
                            System.out.println("Combat annulé par " + pseudoJoueur + ". Notification envoyée à " + pseudoAdversaire);
                        }
                    } catch (Exception e) {
                        System.err.println("Erreur lors de l'annulation du combat: " + e.getMessage());
                        e.printStackTrace();
                    }
                    
                    // Retour à l'écran de sélection d'adversaire
                    fenetre.close();
                    new EcranSelectionAdversaire(joueurDAO, textGUI, screen, pseudoJoueur, serviceCombat).afficher();
                }
                // Si le joueur choisit "Non", rien ne se passe et il reste sur l'écran actuel
            }));

        } catch (Exception e) {
            panel.addComponent(new Label("Erreur: " + e.getMessage()));
            panel.addComponent(new Button("Retour", () -> {
                fenetre.close();
                new EcranSelectionAdversaire(joueurDAO, textGUI, screen, pseudoJoueur, serviceCombat).afficher();
            }));
        }

        fenetre.setComponent(panel);
        textGUI.addWindowAndWait(fenetre);
    }
    
    /**
     * Affiche l'écran de gestion d'inventaire de combat
     */
    private void afficherEcranGestionInventaireDeCombat() {
        Window fenetre = new BasicWindow("Gestion de l'inventaire de combat");
        fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        Panel mainPanel = new Panel(new GridLayout(1));
        mainPanel.addComponent(new Label("Préparez votre inventaire de combat"));
        mainPanel.addComponent(new EmptySpace());

        // Créer un panel avec deux colonnes pour le coffre et l'inventaire
        Panel inventairePanel = new Panel(new GridLayout(2));

        // Colonne du coffre
        Panel coffrePanel = new Panel(new GridLayout(1));
        coffrePanel.addComponent(new Label("Coffre:"));
        
        // Table pour afficher les items du coffre
        Table<String> tableCoffre = new Table<>("ID", "Nom", "Type", "Détails");
        tableCoffre.setVisibleRows(8);
        coffrePanel.addComponent(tableCoffre);

        // Colonne de l'inventaire de combat
        Panel inventaireColPanel = new Panel(new GridLayout(1));
        inventaireColPanel.addComponent(new Label("Inventaire de combat:"));
        
        // Table pour afficher les items de l'inventaire de combat
        Table<String> tableInventaire = new Table<>("ID", "Nom", "Type", "Détails");
        tableInventaire.setVisibleRows(8);
        inventaireColPanel.addComponent(tableInventaire);

        // Boutons pour transférer des items
        Panel transferPanel = new Panel(new GridLayout(2));
        
        // TextBox pour entrer l'ID de l'item à transférer
        TextBox textBoxIdItem = new TextBox(new TerminalSize(5, 1));
        transferPanel.addComponent(new Label("ID de l'item:"));
        transferPanel.addComponent(textBoxIdItem);
        
        // TextBox pour entrer la quantité à transférer
        TextBox textBoxQuantite = new TextBox(new TerminalSize(5, 1));
        transferPanel.addComponent(new Label("Quantité:"));
        transferPanel.addComponent(textBoxQuantite);
        
        mainPanel.addComponent(transferPanel);

        // Boutons d'action
        Panel boutonsPanel = new Panel(new GridLayout(2));
        
        Button btnAjouterInventaire = new Button("Ajouter à l'inventaire de combat", () -> {
            try {
                int itemId = Integer.parseInt(textBoxIdItem.getText());
                int quantite = Integer.parseInt(textBoxQuantite.getText());
                
                // Trouver l'item dans le coffre
                Item itemSelectionne = null;
                for (Slot slot : joueur.getCoffre().getSlots()) {
                    if (slot != null && slot.getItem() != null && slot.getItem().getId() == itemId) {
                        itemSelectionne = slot.getItem();
                        break;
                    }
                }
                
                if (itemSelectionne != null) {
                    // Transférer l'item vers l'inventaire de combat
                    serviceCombat.transfererItemsCoffreVersInventaire(joueur, itemSelectionne, quantite);
                    
                    // Mettre à jour les tableaux
                    rafraichirTables(tableCoffre, tableInventaire);
                    
                    afficherMessageInfo("Item ajouté à l'inventaire de combat");
                } else {
                    afficherMessageErreur("Item non trouvé dans le coffre");
                }
                
                // Réinitialiser les champs
                textBoxIdItem.setText("");
                textBoxQuantite.setText("");
            } catch (NumberFormatException e) {
                afficherMessageErreur("Veuillez entrer des valeurs numériques valides");
            } catch (Exception e) {
                afficherMessageErreur("Erreur: " + e.getMessage());
            }
        });
        
        Button btnRetour = new Button("Retour", fenetre::close);
        
        boutonsPanel.addComponent(btnAjouterInventaire);
        boutonsPanel.addComponent(btnRetour);
        
        // Ajouter les deux colonnes au panel d'inventaire
        inventairePanel.addComponent(coffrePanel);
        inventairePanel.addComponent(inventaireColPanel);
        
        // Ajouter tout au panel principal
        mainPanel.addComponent(inventairePanel);
        mainPanel.addComponent(boutonsPanel);
        
        // Charger les données initiales
        rafraichirTables(tableCoffre, tableInventaire);
        
        fenetre.setComponent(mainPanel);
        textGUI.addWindowAndWait(fenetre);
    }
    
    /**
     * Rafraîchit les tables d'affichage des items
     */
    private void rafraichirTables(Table<String> tableCoffre, Table<String> tableInventaire) {
        // Vider les tables
        tableCoffre.getTableModel().clear();
        tableInventaire.getTableModel().clear();
        
        // Remplir la table du coffre
        if (joueur.getCoffre() != null) {
            for (Slot slot : joueur.getCoffre().getSlots()) {
                if (slot != null && slot.getItem() != null) {
                    Item item = slot.getItem();
                    String details = obtenirDetailsItem(item);
                    tableCoffre.getTableModel().addRow(
                            String.valueOf(item.getId()),
                            item.getNom(),
                            item.getType(),
                            details + " (Quantité: " + slot.getQuantity() + ")"
                    );
                }
            }
        }
        
        // Remplir la table de l'inventaire de combat
        if (joueur.getPersonnage() != null && joueur.getPersonnage().getInventaire() != null) {
            for (Slot slot : joueur.getPersonnage().getInventaire().getSlots()) {
                if (slot != null && slot.getItem() != null) {
                    Item item = slot.getItem();
                    String details = obtenirDetailsItem(item);
                    tableInventaire.getTableModel().addRow(
                            String.valueOf(item.getId()),
                            item.getNom(),
                            item.getType(),
                            details + " (Quantité: " + slot.getQuantity() + ")"
                    );
                }
            }
        }
    }
    
    /**
     * Obtient les détails spécifiques d'un item selon son type
     */
    private String obtenirDetailsItem(Item item) {
        String details = "";
        String className = item.getClass().getSimpleName();

        switch (className) {
            case "Arme":
                details = "Dégâts: " + ((be.helha.projects.GuerreDesRoyaumes.Model.Items.Arme) item).getDegats();
                break;
            case "Bouclier":
                details = "Défense: " + ((be.helha.projects.GuerreDesRoyaumes.Model.Items.Bouclier) item).getDefense();
                break;
            case "Potion":
                be.helha.projects.GuerreDesRoyaumes.Model.Items.Potion potion = (be.helha.projects.GuerreDesRoyaumes.Model.Items.Potion) item;
                details = "Soin: " + potion.getSoin() + ", Dégâts: " + potion.getDegats();
                break;
            default:
                details = "Type inconnu";
        }

        return details;
    }
    
    /**
     * Démarre un thread qui vérifie périodiquement si l'adversaire a annulé le combat
     * Si l'adversaire annule, le joueur est redirigé vers l'écran de sélection d'adversaire
     */
    private void demarrerVerificationAnnulationCombat(Joueur joueur, Joueur adversaire, Window fenetre) {
        Thread thread = new Thread(() -> {
            try {
                // Obtenir le CombatDAO depuis le ServiceCombat
                be.helha.projects.GuerreDesRoyaumes.DAO.CombatDAO combatDAO = serviceCombat.getCombatDAO();
                boolean combatTermine = false;
                
                while (!combatTermine) {
                    // Vérifier si le combat existe toujours en BD
                    int idAdversaireCombat = combatDAO.verifierCombatEnCours(joueur.getId());
                    
                    if (idAdversaireCombat == 0) {
                        // Le combat n'existe plus, l'adversaire a probablement annulé
                        combatTermine = true;
                        
                        // Afficher un message et revenir à l'écran de sélection dans le thread UI
                        textGUI.getGUIThread().invokeLater(() -> {
                            new MessageDialogBuilder()
                                    .setTitle("Combat annulé")
                                    .setText("L'adversaire a annulé le combat. Vous êtes redirigé vers l'écran de sélection d'adversaire.")
                                    .addButton(MessageDialogButton.OK)
                                    .build()
                                    .showDialog(textGUI);
                            
                            fenetre.close();
                            new EcranSelectionAdversaire(joueurDAO, textGUI, screen, pseudoJoueur, serviceCombat).afficher();
                        });
                    }
                    
                    // Attendre 1 seconde avant la prochaine vérification
                    Thread.sleep(1000);
                }
            } catch (Exception e) {
                System.err.println("Erreur lors de la vérification d'annulation du combat: " + e.getMessage());
            }
        });
        
        // Marquer le thread comme daemon pour qu'il se termine quand l'application se termine
        thread.setDaemon(true);
        thread.start();
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
