package be.helha.projects.GuerreDesRoyaumes.TUI.Ecran;

import be.helha.projects.GuerreDesRoyaumes.Config.ConnexionConfig.ConnexionManager;
import be.helha.projects.GuerreDesRoyaumes.Controller.CombatController;
import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.CombatDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.InventairePersonnageMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Coffre;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Slot;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.ItemMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.*;
import be.helha.projects.GuerreDesRoyaumes.Model.Royaume;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Guerrier;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Voleur;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Golem;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceAuthentification;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceBoutique;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceCombat;
import be.helha.projects.GuerreDesRoyaumes.ServiceImpl.CoffreServiceMongoImpl;
import be.helha.projects.GuerreDesRoyaumes.ServiceImpl.InventaireServiceImpl;
import be.helha.projects.GuerreDesRoyaumes.ServiceImpl.ServiceBoutiqueImpl;
import be.helha.projects.GuerreDesRoyaumes.ServiceImpl.ServiceCombatImpl;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.screen.Screen;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

import be.helha.projects.GuerreDesRoyaumes.DAOImpl.CompetenceMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat.*;
import be.helha.projects.GuerreDesRoyaumes.ServiceImpl.CompetenceServiceImpl;

public class EcranPrincipal {

    private final ServiceAuthentification serviceAuthentification;
    private final JoueurDAO joueurDAO;
    private final String pseudo;
    private final Screen screen;
    private final WindowBasedTextGUI textGUI;

    public EcranPrincipal(ServiceAuthentification serviceAuthentification, JoueurDAO joueurDAO, String pseudo, Screen screen) {
        this.serviceAuthentification = serviceAuthentification;
        this.joueurDAO = joueurDAO;
        this.pseudo = pseudo;
        this.screen = screen;
        this.textGUI = new MultiWindowTextGUI(screen);
    }

    public void afficher() {
        Joueur joueur = joueurDAO.obtenirJoueurParPseudo(pseudo);
        if (joueur == null) {
            afficherMessageErreur("Joueur non trouvé");
            return;
        }

        // Synchroniser avec les données MongoDB avant d'afficher
        try {
            // Récupérer le personnage depuis MongoDB
            be.helha.projects.GuerreDesRoyaumes.DAOImpl.PersonnageMongoDAOImpl personnageMongoDAO =
                be.helha.projects.GuerreDesRoyaumes.DAOImpl.PersonnageMongoDAOImpl.getInstance();
            Personnage personnageMongo = personnageMongoDAO.obtenirPersonnageParJoueurId(joueur.getId());
            
            if (personnageMongo != null) {
                joueur.setPersonnage(personnageMongo);
                joueurDAO.mettreAJourJoueur(joueur);
            }
            
            // Récupérer le royaume depuis MongoDB
            be.helha.projects.GuerreDesRoyaumes.DAOImpl.RoyaumeMongoDAOImpl royaumeMongoDAO =
                be.helha.projects.GuerreDesRoyaumes.DAOImpl.RoyaumeMongoDAOImpl.getInstance();
            Royaume royaumeMongo = royaumeMongoDAO.obtenirRoyaumeParJoueurId(joueur.getId());
            
            if (royaumeMongo != null) {
                if (joueur.getRoyaume() == null) {
                    joueur.setRoyaume(royaumeMongo);
                } else {
                    joueur.getRoyaume().setNom(royaumeMongo.getNom());
                    joueur.getRoyaume().setNiveau(royaumeMongo.getNiveau());
                }
                joueurDAO.mettreAJourJoueur(joueur);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la synchronisation avec MongoDB: " + e.getMessage());
        }

        Window fenetre = new BasicWindow("Guerre des Royaumes - Menu Principal");
        fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(1));
        panel.addComponent(new Label("Bienvenue, " + joueur.getPseudo() + " !"));
        panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));

        panel.addComponent(new Button("Voir Profil", () -> {
            fenetre.close();
            afficherProfil(joueur);
        }));

        panel.addComponent(new Button("Gérer Profil", () -> {
            fenetre.close();
            afficherEcranGestionProfil(joueur);
        }));

        panel.addComponent(new Button("Choisir Personnage", () -> {
            fenetre.close();
            afficherEcranChoixPersonnage(joueur);
        }));

        panel.addComponent(new Button("Gérer Coffre", () -> {
            fenetre.close();
            afficherEcranGestionCoffre(joueur);
        }));

        panel.addComponent(new Button("Boutique", () -> {
            fenetre.close();
            afficherEcranBoutique(joueur);
        }));

        panel.addComponent(new Button("Gérer Royaume", () -> {
            fenetre.close();
            afficherEcranGestionRoyaume(joueur);
        }));

        panel.addComponent(new Button("Combattre", () -> {
            fenetre.close();
            afficherEcranCombat(joueur);
        }));

        panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));
        panel.addComponent(new Button("Déconnexion", () -> {
            // Déconnecter le joueur
            if (serviceAuthentification != null) {
                serviceAuthentification.deconnecterJoueur(joueur.getPseudo());
            }
            // Fermer la fenêtre actuelle
            fenetre.close();
            
            // Au lieu de fermer l'application, revenir à l'écran d'authentification
            new EcranAuthentification(serviceAuthentification, textGUI, screen, joueurDAO).afficher();
        }));

        fenetre.setComponent(panel);
        textGUI.addWindowAndWait(fenetre);
    }

    private void afficherEcranGestionProfil(Joueur joueur) {
        Window fenetre = new BasicWindow("Gestion du Profil - Guerre des Royaumes");
        fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(2));

        // Champs de modification
        panel.addComponent(new Label("Nom :"));
        TextBox nomBox = new TextBox(joueur.getNom());
        panel.addComponent(nomBox);

        panel.addComponent(new Label("Prénom :"));
        TextBox prenomBox = new TextBox(joueur.getPrenom());
        panel.addComponent(prenomBox);

        panel.addComponent(new Label("Pseudo :"));
        TextBox pseudoBox = new TextBox(joueur.getPseudo());
        panel.addComponent(pseudoBox);

        panel.addComponent(new Label("Nouveau mot de passe :"));
        TextBox mdpBox = new TextBox().setMask('*');
        panel.addComponent(mdpBox);

        panel.addComponent(new Label("Confirmer mot de passe :"));
        TextBox mdpConfirmBox = new TextBox().setMask('*');
        panel.addComponent(mdpConfirmBox);

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Sauvegarder", () -> {
            String nom = nomBox.getText();
            String prenom = prenomBox.getText();
            String pseudo = pseudoBox.getText();
            String motDePasse = mdpBox.getText();
            String motDePasseConfirm = mdpConfirmBox.getText();

            if (nom.isEmpty() || prenom.isEmpty() || pseudo.isEmpty()) {
                afficherMessageErreur("Le nom, prénom et pseudo sont obligatoires");
                return;
            }

            // Vérifier si le mot de passe a été modifié
            if (!motDePasse.isEmpty()) {
                if (!motDePasse.equals(motDePasseConfirm)) {
                    afficherMessageErreur("Les mots de passe ne correspondent pas");
                    return;
                }
            }

            try {
                // Stocker l'ancien pseudo pour vérification
                String ancienPseudo = joueur.getPseudo();
                
                // Mettre à jour les informations du joueur
                joueur.setNom(nom);
                joueur.setPrenom(prenom);
                joueur.setPseudo(pseudo);

                // Utiliser le service d'authentification pour mettre à jour le joueur
                if (!motDePasse.isEmpty()) {
                    // Si un nouveau mot de passe est fourni
                    serviceAuthentification.gererProfil(joueur.getId(), pseudo, motDePasse);
                } else {
                    // Si le mot de passe reste inchangé
                    serviceAuthentification.mettreAJourJoueur(joueur);
                }

                // Si le pseudo a été modifié, créer une nouvelle instance d'EcranPrincipal
                if (!ancienPseudo.equals(pseudo)) {
                    fenetre.close();
                    afficherMessageSucces("Profil mis à jour avec succès. Reconnexion avec le nouveau pseudo...");
                    
                    // Créer et afficher un nouvel écran principal avec le nouveau pseudo
                    new EcranPrincipal(serviceAuthentification, joueurDAO, pseudo, screen).afficher();
                    return;
                }

                fenetre.close();
                afficherMessageSucces("Profil mis à jour avec succès");
                afficher();
            } catch (Exception e) {
                afficherMessageErreur("Erreur lors de la mise à jour du profil: " + e.getMessage());
            }
        }));

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Retour", () -> {
            fenetre.close();
            afficher();
        }));

        fenetre.setComponent(panel);
        textGUI.addWindowAndWait(fenetre);
    }

    private void afficherProfil(Joueur joueur) {
        Window fenetre = new BasicWindow("Profil - Guerre des Royaumes");
        fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        // Tenter de récupérer le personnage depuis MongoDB
        Personnage personnageMongo = null;
        try {
            be.helha.projects.GuerreDesRoyaumes.DAOImpl.PersonnageMongoDAOImpl personnageMongoDAO =
                be.helha.projects.GuerreDesRoyaumes.DAOImpl.PersonnageMongoDAOImpl.getInstance();
            personnageMongo = personnageMongoDAO.obtenirPersonnageParJoueurId(joueur.getId());

            // Toujours mettre à jour le personnage depuis MongoDB s'il existe
            if (personnageMongo != null) {
                joueur.setPersonnage(personnageMongo);
                try {
                    joueurDAO.mettreAJourJoueur(joueur);
                } catch (Exception e) {
                    System.err.println("Erreur lors de la mise à jour du joueur avec le personnage: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du personnage dans MongoDB: " + e.getMessage());
        }

        // Tenter de récupérer le royaume depuis MongoDB
        Royaume royaumeMongo = null;
        try {
            be.helha.projects.GuerreDesRoyaumes.DAOImpl.RoyaumeMongoDAOImpl royaumeMongoDAO =
                be.helha.projects.GuerreDesRoyaumes.DAOImpl.RoyaumeMongoDAOImpl.getInstance();
            royaumeMongo = royaumeMongoDAO.obtenirRoyaumeParJoueurId(joueur.getId());

            // Si on trouve un royaume dans MongoDB mais pas dans le joueur ou si les noms sont différents, on le met à jour
            if (royaumeMongo != null &&
                (joueur.getRoyaume() == null ||
                 !joueur.getRoyaume().getNom().equals(royaumeMongo.getNom()))) {

                if (joueur.getRoyaume() == null) {
                    joueur.setRoyaume(royaumeMongo);
                } else {
                    joueur.getRoyaume().setNom(royaumeMongo.getNom());
                    joueur.getRoyaume().setNiveau(royaumeMongo.getNiveau());
                }

                try {
                    joueurDAO.mettreAJourJoueur(joueur);
                } catch (Exception e) {
                    System.err.println("Erreur lors de la mise à jour du joueur avec le royaume: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du royaume dans MongoDB: " + e.getMessage());
        }

        Panel panel = new Panel(new GridLayout(1));

        // Informations principales
        panel.addComponent(new Label("=== Informations du Joueur ==="));
        panel.addComponent(new Label("Pseudo: " + joueur.getPseudo()));

        // Informations du personnage
        if (joueur.getPersonnage() != null) {
            panel.addComponent(new Label("Votre personnage actuel: " + joueur.getPersonnage().getClass().getSimpleName()));
            // Afficher les statistiques du personnage
            panel.addComponent(new Label("Points de vie: " + joueur.getPersonnage().getVie()));
            panel.addComponent(new Label("Points d'attaque: " + joueur.getPersonnage().getDegats()));
            panel.addComponent(new Label("Points de défense: " + joueur.getPersonnage().getResistance()));
        } else if (personnageMongo != null) {
            panel.addComponent(new Label("Votre personnage actuel (MongoDB): " + personnageMongo.getClass().getSimpleName()));
            // Afficher les statistiques du personnage
            panel.addComponent(new Label("Points de vie: " + personnageMongo.getVie()));
            panel.addComponent(new Label("Points d'attaque: " + personnageMongo.getDegats()));
            panel.addComponent(new Label("Points de défense: " + personnageMongo.getResistance()));
        } else {
            panel.addComponent(new Label("Vous n'avez pas encore choisi de personnage"));
        }

        panel.addComponent(new Label("Argent: " + joueur.getArgent() + " TerraCoins"));
        panel.addComponent(new Label("Victoires: " + joueur.getVictoires()));
        panel.addComponent(new Label("Défaites: " + joueur.getDefaites()));

        // Sous-catégorie Royaume
        panel.addComponent(new Label("\n=== Son Royaume ==="));
        if (joueur.getRoyaume() != null) {
            panel.addComponent(new Label("Nom du Royaume: " + joueur.getRoyaume().getNom()));
            panel.addComponent(new Label("Niveau du Royaume: " + joueur.getRoyaume().getNiveau()));
        } else if (royaumeMongo != null) {
            panel.addComponent(new Label("Nom du Royaume (MongoDB): " + royaumeMongo.getNom()));
            panel.addComponent(new Label("Niveau du Royaume (MongoDB): " + royaumeMongo.getNiveau()));
        } else {
            panel.addComponent(new Label("Aucun royaume"));
        }

        // Boutons d'action
        Panel boutonsPanel = new Panel(new GridLayout(3));
        boutonsPanel.addComponent(new Button("Retour", () -> {
            fenetre.close();
            afficher();
        }));

        panel.addComponent(boutonsPanel);
        fenetre.setComponent(panel);
        textGUI.addWindowAndWait(fenetre);
    }

    private void afficherEcranChoixPersonnage(Joueur joueur) {
        Window fenetre = new BasicWindow("Choix du Personnage - Guerre des Royaumes");
        fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(1));

        panel.addComponent(new Label("=== Choisissez votre Personnage ==="));

        // Liste des personnages disponibles
        String[] personnages = {"Guerrier", "Voleur", "Golem", "Titan"};
        for (String nomPersonnage : personnages) {
            Button btnPersonnage = new Button(nomPersonnage, () -> {
                try {
                    // Création du personnage selon le type choisi
                    Personnage personnage = null;

                    switch (nomPersonnage) {
                        case "Guerrier":
                            personnage = new Guerrier();
                            break;
                        case "Voleur":
                            personnage = new Voleur();
                            break;
                        case "Golem":
                            personnage = new Golem();
                            break;
                        case "Titan":
                            personnage = new Titan();
                            break;
                        default:
                            throw new IllegalArgumentException("Type de personnage non reconnu");
                    }

                    // Vérifier si un personnage existe déjà dans MongoDB
                    be.helha.projects.GuerreDesRoyaumes.DAOImpl.PersonnageMongoDAOImpl personnageMongoDAO =
                        be.helha.projects.GuerreDesRoyaumes.DAOImpl.PersonnageMongoDAOImpl.getInstance();

                    Personnage personnageExistant = personnageMongoDAO.obtenirPersonnageParJoueurId(joueur.getId());

                    if (personnageExistant != null) {
                        // Si un personnage existe déjà, on le met à jour
                        personnageMongoDAO.mettreAJourPersonnage(personnage, joueur.getId());
                        System.out.println("Personnage mis à jour dans MongoDB pour le joueur: " + joueur.getId());
                    } else {
                        // Sinon, on crée un nouveau personnage
                        personnageMongoDAO.ajouterPersonnage(personnage, joueur.getId());
                        System.out.println("Personnage créé dans MongoDB pour le joueur: " + joueur.getId());
                    }

                    // Mettre à jour le joueur avec le personnage
                    joueur.setPersonnage(personnage);
                    joueurDAO.mettreAJourJoueur(joueur);

                    fenetre.close();
                    afficherMessageSucces("Personnage " + nomPersonnage + " sélectionné");
                    // Récupérer le joueur avec les données à jour avant d'afficher l'écran principal
                    Joueur joueurMisAJour = joueurDAO.obtenirJoueurParId(joueur.getId());
                    if (joueurMisAJour != null) {
                        afficher(); // Retour à l'écran principal avec des données à jour
                    } else {
                        afficherMessageErreur("Erreur lors de la récupération des données mises à jour du joueur");
                        afficher(); // Retour à l'écran principal avec les données existantes
                    }
                } catch (Exception e) {
                    afficherMessageErreur("Erreur lors de la sélection du personnage: " + e.getMessage());
                    e.printStackTrace();
                }
            });
            panel.addComponent(btnPersonnage);
        }

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Retour", () -> {
            fenetre.close();
            afficher();
        }));

        fenetre.setComponent(panel);
        textGUI.addWindowAndWait(fenetre);
    }

    public void afficherEcranGestionCoffre(Joueur joueur, boolean estModeCombat) {
        // Implémentation pour la gestion du coffre avec MongoDB
        CoffreServiceMongoImpl coffreService = CoffreServiceMongoImpl.getInstance();

        try {
            // Charger le contenu du coffre depuis MongoDB
            coffreService.chargerCoffre(joueur);

            // Vérifier si le coffre est initialisé après le chargement
            if (joueur.getCoffre() == null) {
                joueur.setCoffre(new Coffre());
            }

            // Créer et afficher l'écran de gestion du coffre
            Panel panel = new Panel(new GridLayout(1));
            Window fenetre = null;
            if (estModeCombat) {
                 fenetre = new BasicWindow("Préparation au combat - Sélection d'items - " + joueur.getPseudo());
            } else {
                 fenetre = new BasicWindow("Gestion du Coffre - " + joueur.getPseudo());
            }
            fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));
            
            // Utiliser une référence finale à la fenêtre pour les lambdas
            final Window fenetreFinale = fenetre;

            // En-tête
            panel.addComponent(new Label("═══ Coffre de " + joueur.getPseudo() + " ═══"));
            panel.addComponent(new Label("TerraCoins disponible: " + joueur.getArgent() + " TerraCoins"));
            panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));

            // Afficher le contenu du coffre
            List<Slot> slots = joueur.getCoffre().getSlots();
            boolean coffreVide = true;

            Panel contenuPanel = new Panel(new GridLayout(5));
            contenuPanel.addComponent(new Label("ID"));
            contenuPanel.addComponent(new Label("Nom"));
            contenuPanel.addComponent(new Label("Type"));
            contenuPanel.addComponent(new Label("Prix"));
            contenuPanel.addComponent(new Label("Quantité"));

            // Calculer la valeur totale du coffre et la valeur de vente ici
            int totalValeurCoffre = 0;

            for (Slot slot : slots) {
                if (slot != null && slot.getItem() != null && slot.getQuantity() > 0) {
                    coffreVide = false;
                    Item item = slot.getItem();

                    contenuPanel.addComponent(new Label(String.valueOf(item.getId())));
                    contenuPanel.addComponent(new Label(item.getNom()));
                    contenuPanel.addComponent(new Label(item.getType()));
                    contenuPanel.addComponent(new Label(String.valueOf(item.getPrix())));
                    contenuPanel.addComponent(new Label(String.valueOf(slot.getQuantity())));

                    // Ajouter le prix de l'item à la valeur totale
                    totalValeurCoffre += item.getPrix() * slot.getQuantity();
                }
            }

            // Calculer la valeur de vente une seule fois
            final int valeurTotale = totalValeurCoffre;
            final int valeurVente = totalValeurCoffre / 2;

            if (coffreVide) {
                panel.addComponent(new Label("Votre coffre est vide"));
            } else {
                panel.addComponent(contenuPanel);

                if(!estModeCombat) {
                    panel.addComponent(new Label("Valeur totale: " + valeurTotale + " TerraCoins" + " | Valeur de vente: " + valeurVente + " TerraCoins"));
                }

                // Afficher la valeur totale et la valeur de vente
                panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));
                panel.addComponent(new Label("Valeur totale du coffre: " + valeurTotale + " TerraCoins"));
                panel.addComponent(new Label("Valeur de vente (50%): " + valeurVente + " TerraCoins"));

                // Ajouter des options de gestion
                panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));
                panel.addComponent(new Label("Options:"));


                Panel optionsPanel = new Panel(new GridLayout(2));
                if(estModeCombat) {
                    panel.addComponent(new Label("Sélectionnez jusqu'à 5 items pour le combat:"));
                }

                optionsPanel.addComponent(new Label("ID Item:"));
                TextBox idBox = new TextBox();
                optionsPanel.addComponent(idBox);

                optionsPanel.addComponent(new Label("Quantité:"));
                TextBox quantiteBox = new TextBox().setText("1");
                optionsPanel.addComponent(quantiteBox);

                panel.addComponent(optionsPanel);

                Panel boutonsPanel = new Panel(new GridLayout(estModeCombat ? 2 : 3));

                if (estModeCombat) {
                    // Mode combat afficher bouton transferer item et confirmer
                    Button btnTransferer = new Button("Équiper", () -> {
                        try {
                            int itemId = Integer.parseInt(idBox.getText());
                            
                            // Initialiser le service d'inventaire
                            InventaireServiceImpl inventaireService = InventaireServiceImpl.getInstance();
                            
                            // Transfert vers l'inventaire de combat - Obtenir l'item
                            Item item = inventaireService.transfererDuCoffreVersInventaire(joueur, itemId);

                            // Le transfert a réussi puisqu'aucune exception n'a été levée
                            afficherMessageSucces(item.getNom() + " équipé!");
                            fenetreFinale.close();
                            afficherEcranGestionCoffre(joueur, true);
                            
                        } catch (NumberFormatException e) {
                            afficherMessageErreur("Veuillez entrer un ID d'item valide.");
                        } catch (Exception e) {
                            // Capturer toutes les erreurs de transfert et les afficher dans Lanterna
                            afficherMessageErreur(e.getMessage());
                        }
                    });

                    Button btnConfirmer = new Button("Confirmer", () -> {
                        if(joueur.getPersonnage().getInventaire().getSlots().isEmpty()) {
                            // Afficher une boîte de dialogue de confirmation
                            MessageDialogBuilder confirmDialog = new MessageDialogBuilder()
                                    .setTitle("Confirmation")
                                    .setText("Êtes-vous sûr de vouloir lancer le combat sans items?")
                                    .addButton(MessageDialogButton.Yes)
                                    .addButton(MessageDialogButton.No);

                            MessageDialogButton reponse = confirmDialog.build().showDialog(textGUI);
                            
                            if (reponse == MessageDialogButton.No) {
                                // Si l'utilisateur choisit "Non", réafficher l'écran de gestion du coffre en mode combat
                                fenetreFinale.close();
                                afficherEcranGestionCoffre(joueur, true);
                                return;
                            }
                            // Si l'utilisateur choisit "Oui", continuer le combat sans items
                        }

                        // Passer à l'écran de sélection des compétences avant de lancer le combat
                        fenetreFinale.close();
                        afficherEcranGestionCompetences(joueur);
                    });

                    // Ajouter les boutons dans l'ordre souhaité : Équiper, Confirmer, puis Annuler
                    boutonsPanel.addComponent(btnTransferer);
                    boutonsPanel.addComponent(btnConfirmer);
                    boutonsPanel.addComponent(new Button("Annuler", () -> {
                        // Afficher une boîte de dialogue de confirmation
                        MessageDialogBuilder confirmDialog = new MessageDialogBuilder()
                                .setTitle("Confirmation")
                                .setText("Êtes-vous sûr de vouloir annuler la préparation au combat?")
                                .addButton(MessageDialogButton.Yes)
                                .addButton(MessageDialogButton.No);

                        MessageDialogButton reponse = confirmDialog.build().showDialog(textGUI);
                        
                        if (reponse == MessageDialogButton.Yes) {
                            // Si l'utilisateur choisit "Oui", on vide l'inventaire de combat et les compétences
                            annulerPreparationCombat(joueur);
                            
                            // Revenir à l'écran principal
                            fenetreFinale.close();
                            afficher();
                        } else {
                            // Si l'utilisateur choisit "Non", rester sur l'écran de préparation au combat
                            // Ne rien faire, l'écran reste affiché
                        }
                    }));
                } else {
                // Bouton Retirer Item
                boutonsPanel.addComponent(new Button("Retirer", () -> {
                    try {
                        int itemId = Integer.parseInt(idBox.getText());
                        int quantite = Integer.parseInt(quantiteBox.getText());

                        // Retirer l'item avec la méthode du service
                        boolean success = coffreService.retirerItemDuCoffre(joueur, itemId, quantite);

                        if (success) {
                            afficherMessageSucces("Item retiré avec succès");
                            // Fermer et réafficher l'écran pour actualiser
                            fenetreFinale.close();
                            afficherEcranGestionCoffre(joueur, false);
                        } else {
                            afficherMessageErreur("Erreur lors du retrait de l'item");
                        }
                    } catch (NumberFormatException e) {
                        afficherMessageErreur("Veuillez entrer des valeurs numériques valides");
                    }
                }));

                // Bouton Vendre Item
                boutonsPanel.addComponent(new Button("Vendre Item", () -> {
                    try {
                        int itemId = Integer.parseInt(idBox.getText());
                        int quantite = Integer.parseInt(quantiteBox.getText());

                        // Trouver l'item dans le coffre
                        Item itemAVendre = null;
                        for (Slot slot : slots) {
                            if (slot != null && slot.getItem() != null && slot.getItem().getId() == itemId) {
                                itemAVendre = slot.getItem();
                                break;
                            }
                        }

                        if (itemAVendre == null) {
                            afficherMessageErreur("Item non trouvé dans le coffre");
                            return;
                        }

                        // Calculer le prix de vente (50% du prix original)
                        int prixVente = (itemAVendre.getPrix() * quantite) / 2;

                        // Demander confirmation
                        MessageDialogBuilder confirmDialog = new MessageDialogBuilder()
                                .setTitle("Confirmation de vente")
                                .setText("Voulez-vous vendre " + quantite + "x " + itemAVendre.getNom() + " pour " + prixVente + " TerraCoins? (50% du prix original)")
                                .addButton(MessageDialogButton.Yes)
                                .addButton(MessageDialogButton.No);

                        MessageDialogButton reponse = confirmDialog.build().showDialog(textGUI);

                        if (reponse == MessageDialogButton.Yes) {
                            // Retirer l'item du coffre
                            boolean success = coffreService.retirerItemDuCoffre(joueur, itemId, quantite);

                            if (success) {
                                // Ajouter l'argent au joueur
                                joueur.ajouterArgent(prixVente);
                                joueurDAO.mettreAJourJoueur(joueur);

                                afficherMessageSucces("Item vendu avec succès! Vous avez gagné " + prixVente + " TerraCoins.");

                                // Fermer et réafficher l'écran pour actualiser
                                fenetreFinale.close();
                                afficherEcranGestionCoffre(joueur, false);
                            } else {
                                afficherMessageErreur("Erreur lors de la vente de l'item");
                            }
                        }
                    } catch (NumberFormatException e) {
                        afficherMessageErreur("Veuillez entrer des valeurs numériques valides");
                    }
                }));

                // Bouton Vendre Tout - Utilisant les variables finales
                boutonsPanel.addComponent(new Button("Vendre Tout", () -> {
                    // Demander confirmation - Utiliser les variables finales
                    MessageDialogBuilder confirmDialog = new MessageDialogBuilder()
                            .setTitle("Confirmation de vente")
                            .setText("Voulez-vous vendre tout le contenu du coffre pour " + valeurVente +
                                    " TerraCoins? (50% du prix original total de " + valeurTotale + " TerraCoins)")
                            .addButton(MessageDialogButton.Yes)
                            .addButton(MessageDialogButton.No);

                    MessageDialogButton reponse = confirmDialog.build().showDialog(textGUI);

                    if (reponse == MessageDialogButton.Yes) {
                        // Vider le coffre
                        if (coffreService.viderCoffre(joueur)) {
                            // Ajouter l'argent au joueur en utilisant la variable finale
                            joueur.ajouterArgent(valeurVente);
                            joueurDAO.mettreAJourJoueur(joueur);

                            afficherMessageSucces("Tous les items ont été vendus avec succès! Vous avez gagné " +
                                                  valeurVente + " TerraCoins.");

                            // Fermer et réafficher l'écran pour actualiser
                            fenetreFinale.close();
                            afficherEcranGestionCoffre(joueur, false);
                        } else {
                            afficherMessageErreur("Erreur lors de la vente des items");
                        }
                    }
                }));}

                panel.addComponent(boutonsPanel);
            }

            panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));
            if (estModeCombat) {
                // En mode combat, on a déjà un bouton Annuler dans le panel des boutons
            } else {
                panel.addComponent(new Button("Retour", () -> {
                    fenetreFinale.close();
                    afficher(); // Retour à l'écran principal
                }));
            }

            fenetre.setComponent(panel);
            textGUI.addWindowAndWait(fenetre);
        } catch (Exception e) {
            afficherMessageErreur("Erreur lors du chargement du coffre : " + e.getMessage());
            afficher(); // Retour à l'écran principal en cas d'erreur
        }
    }

    // Surcharge de la méthode pour faciliter l'appel sans préciser le mode combat
    public void afficherEcranGestionCoffre(Joueur joueur) {
        afficherEcranGestionCoffre(joueur, false);
    }

    private void afficherEcranBoutique(Joueur joueur) {
        // Assurer que les données du coffre sont chargées avant d'afficher l'écran boutique
        CoffreServiceMongoImpl coffreService = CoffreServiceMongoImpl.getInstance();

        try {
            // Charger le contenu du coffre depuis MongoDB
            coffreService.chargerCoffre(joueur);

            // Initialiser et afficher l'écran boutique
            ServiceBoutique serviceBoutique = ServiceBoutiqueImpl.getInstance();
            EcranBoutique ecranBoutique = new EcranBoutique(serviceBoutique, joueur, screen);
            ecranBoutique.afficher();

        } catch (Exception e) {
            afficherMessageErreur("Erreur lors du chargement du coffre : " + e.getMessage());
        }
    }

    private void afficherEcranGestionRoyaume(Joueur joueur) {
        // Vérifier d'abord si le royaume est initialisé
        if (joueur.getRoyaume() == null) {
            joueur.setRoyaume(new Royaume(0, "Royaume de " + joueur.getPseudo(), 1));
            try {
                joueurDAO.mettreAJourJoueur(joueur);
            } catch (Exception e) {
                afficherMessageErreur("Erreur lors de l'initialisation du royaume: " + e.getMessage());
                return;
            }
        }

        // Récupérer également le royaume depuis MongoDB pour s'assurer qu'il est correctement synchronisé
        try {
            be.helha.projects.GuerreDesRoyaumes.DAOImpl.RoyaumeMongoDAOImpl royaumeMongoDAO =
                be.helha.projects.GuerreDesRoyaumes.DAOImpl.RoyaumeMongoDAOImpl.getInstance();
            Royaume royaumeMongo = royaumeMongoDAO.obtenirRoyaumeParJoueurId(joueur.getId());

            // Si le royaume existe dans MongoDB mais a un nom différent, synchroniser avec l'objet joueur
            if (royaumeMongo != null && !joueur.getRoyaume().getNom().equals(royaumeMongo.getNom())) {
                joueur.getRoyaume().setNom(royaumeMongo.getNom());
                joueur.getRoyaume().setNiveau(royaumeMongo.getNiveau());
                try {
                    joueurDAO.mettreAJourJoueur(joueur);
                } catch (Exception e) {
                    System.err.println("Erreur lors de la synchronisation du royaume: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du royaume depuis MongoDB: " + e.getMessage());
        }

        // Créer la fenêtre de gestion du royaume
        Window fenetre = new BasicWindow("Gestion du Royaume - Guerre des Royaumes");
        fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(2));

        // Informations actuelles du royaume
        panel.addComponent(new Label("Nom actuel du royaume:"));
        panel.addComponent(new Label(joueur.getRoyaume().getNom()));

        panel.addComponent(new Label("Niveau actuel:"));
        panel.addComponent(new Label(String.valueOf(joueur.getRoyaume().getNiveau())));

        // Champ pour modifier le nom du royaume
        panel.addComponent(new Label("Nouveau nom du royaume:"));
        TextBox nomRoyaumeBox = new TextBox(joueur.getRoyaume().getNom());
        panel.addComponent(nomRoyaumeBox);

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Sauvegarder", () -> {
            String nouveauNom = nomRoyaumeBox.getText();

            if (nouveauNom.isEmpty()) {
                afficherMessageErreur("Le nom du royaume ne peut pas être vide");
                return;
            }

            // Mettre à jour le nom du royaume
            joueur.getRoyaume().setNom(nouveauNom);

            // Mettre à jour dans SQL
            try {
                joueurDAO.mettreAJourJoueur(joueur);
            } catch (Exception e) {
                afficherMessageErreur("Erreur lors de la mise à jour du royaume dans SQL: " + e.getMessage());
                return;
            }

            // Mettre à jour dans MongoDB
            try {
                be.helha.projects.GuerreDesRoyaumes.DAOImpl.RoyaumeMongoDAOImpl royaumeMongoDAO =
                    be.helha.projects.GuerreDesRoyaumes.DAOImpl.RoyaumeMongoDAOImpl.getInstance();

                Royaume royaumeMongo = royaumeMongoDAO.obtenirRoyaumeParJoueurId(joueur.getId());
                if (royaumeMongo != null) {
                    royaumeMongoDAO.mettreAJourRoyaume(joueur.getRoyaume(), joueur.getId());
                } else {
                    royaumeMongoDAO.ajouterRoyaume(joueur.getRoyaume(), joueur.getId());
                }

                afficherMessageSucces("Royaume mis à jour avec succès");
                fenetre.close();
                afficher();

            } catch (Exception e) {
                afficherMessageErreur("Erreur lors de la mise à jour du royaume dans MongoDB: " + e.getMessage());
                e.printStackTrace();
            }
        }));

        // Autres options de gestion du royaume (à développer ultérieurement)
        panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));
        panel.addComponent(new EmptySpace());

        // Bouton retour
        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Retour", () -> {
            fenetre.close();
            afficher();
        }));

        fenetre.setComponent(panel);
        textGUI.addWindowAndWait(fenetre);
    }

    private void afficherEcranCombat(Joueur joueur) {
        // Vérifier si le joueur a un personnage
        if (joueur.getPersonnage() == null) {
            afficherMessageErreur("Vous devez d'abord choisir un personnage avant de combattre.");
            return;
        }

        try {
            // Activer le statut du joueur (le rendre disponible pour le combat)
            try {
                // Définir le statut comme actif
                joueurDAO.definirStatutConnexion(joueur.getId(), true);
                System.out.println("Joueur " + joueur.getPseudo() + " activé pour le combat");
            } catch (Exception e) {
                System.err.println("Erreur lors de l'activation du statut de combat: " + e.getMessage());
                e.printStackTrace();
            }

            // Initialisation du service de combat
            // Créer une instance de CombatDAOImpl
            be.helha.projects.GuerreDesRoyaumes.DAOImpl.CombatDAOImpl combatDAO = new be.helha.projects.GuerreDesRoyaumes.DAOImpl.CombatDAOImpl();
            ServiceCombat serviceCombat = new ServiceCombatImpl(joueurDAO, combatDAO);

            // Afficher l'écran de sélection d'adversaire
            new EcranSelectionAdversaire(joueurDAO, textGUI, screen, joueur.getPseudo(), serviceCombat).afficher();

        } catch (Exception e) {
            afficherMessageErreur("Erreur lors de l'initialisation du combat: " + e.getMessage());
        }
    }

    private void afficherMessageErreur(String message) {
        MessageDialogBuilder dialogBuilder = new MessageDialogBuilder()
                .setTitle("Erreur")
                .setText(message)
                .addButton(MessageDialogButton.OK);
        dialogBuilder.build().showDialog(textGUI);
    }

    private void afficherMessageSucces(String message) {
        MessageDialogBuilder dialogBuilder = new MessageDialogBuilder()
                .setTitle("Succès")
                .setText(message)
                .addButton(MessageDialogButton.OK);
        dialogBuilder.build().showDialog(textGUI);
    }

    /**
     * Vide l'inventaire de combat et replace tous les items dans le coffre
     * @param joueur Le joueur dont l'inventaire doit être vidé
     * @return true si l'opération a réussi, false sinon
     */
    private boolean viderInventaireCombatVersCoffre(Joueur joueur) {
        try {
            if (joueur == null || joueur.getPersonnage() == null || joueur.getPersonnage().getInventaire() == null) {
                return false;
            }
            
            // Obtenir le DAO d'inventaire pour accéder aux items
            InventairePersonnageMongoDAOImpl inventaireDAO = InventairePersonnageMongoDAOImpl.getInstance();
            
            // Récupérer les items dans l'inventaire de combat
            List<Item> itemsInventaire = inventaireDAO.obtenirItemsInventaire(joueur.getPseudo());
            
            if (itemsInventaire.isEmpty()) {
                // Si l'inventaire est déjà vide, rien à faire
                return true;
            }
            
            // Créer une copie de la liste pour éviter les problèmes de modification pendant l'itération
            List<Item> itemsACopier = new ArrayList<>(itemsInventaire);
            
            // Initialiser le service d'inventaire
            InventaireServiceImpl inventaireService = InventaireServiceImpl.getInstance();
            
            // Pour chaque item, le transférer vers le coffre
            for (Item item : itemsACopier) {
                try {
                    inventaireService.transfererDeInventaireVersCoffre(joueur, item.getId());
                } catch (Exception e) {
                    // Consigner l'erreur mais continuer avec les autres items
                    System.err.println("Erreur lors du transfert de l'item " + item.getNom() + ": " + e.getMessage());
                }
            }
            
            // Vérifier si tous les items ont été transférés
            itemsInventaire = inventaireDAO.obtenirItemsInventaire(joueur.getPseudo());
            return itemsInventaire.isEmpty();
            
        } catch (Exception e) {
            System.err.println("Erreur lors du vidage de l'inventaire de combat: " + e.getMessage());
            return false;
        }
    }

    /**
     * Affiche l'écran de sélection des compétences de combat
     * @param joueur Le joueur qui va combattre
     */
    public void afficherEcranGestionCompetences(Joueur joueur) {
        try {
            // Initialiser le service de compétences
            CompetenceServiceImpl competenceService = CompetenceServiceImpl.getInstance();
            
            // Créer la fenêtre
            Window fenetre = new BasicWindow("Compétences de Combat - " + joueur.getPseudo());
            fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));
            
            // Utiliser une référence finale à la fenêtre pour les lambdas
            final Window fenetreFinale = fenetre;
            
            Panel panel = new Panel(new GridLayout(1));
            
            // En-tête
            panel.addComponent(new Label("═══ Compétences de Combat ═══"));
            panel.addComponent(new Label("TerraCoins disponibles: " + joueur.getArgent() + " TerraCoins"));
            panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));
            
            // Obtenir les compétences déjà achetées
            List<Competence> competencesAchetees = competenceService.obtenirCompetencesJoueur(joueur);
            
            // Afficher les compétences déjà achetées
            if (!competencesAchetees.isEmpty()) {
                panel.addComponent(new Label("Compétences déjà achetées:"));
                Panel competencesPanel = new Panel(new GridLayout(3));
                competencesPanel.addComponent(new Label("Nom"));
                competencesPanel.addComponent(new Label("Description"));
                competencesPanel.addComponent(new Label("Prix"));
                
                for (Competence competence : competencesAchetees) {
                    competencesPanel.addComponent(new Label(competence.getNom()));
                    competencesPanel.addComponent(new Label(competence.getDescription()));
                    competencesPanel.addComponent(new Label(String.valueOf(competence.getPrix())));
                }
                
                panel.addComponent(competencesPanel);
                panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));
            } else {
                panel.addComponent(new Label("Vous n'avez pas encore acheté de compétences."));
                panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));
            }
            
            // Vérifier si le joueur peut acheter de nouvelles compétences
            boolean peutAcheter = competenceService.peutAcheterNouvelleCompetence(joueur);
            
            if (peutAcheter) {
                panel.addComponent(new Label("Compétences disponibles à l'achat:"));
                
                // Obtenir toutes les compétences disponibles
                List<Competence> toutesCompetences = competenceService.obtenirToutesCompetences();
                
                // Filtrer les compétences déjà achetées
                List<Competence> competencesDisponibles = new ArrayList<>();
                for (Competence competence : toutesCompetences) {
                    boolean dejaAchetee = false;
                    for (Competence competenceAchetee : competencesAchetees) {
                        if (competence.getId().equals(competenceAchetee.getId())) {
                            dejaAchetee = true;
                            break;
                        }
                    }
                    if (!dejaAchetee) {
                        competencesDisponibles.add(competence);
                    }
                }
                
                if (!competencesDisponibles.isEmpty()) {
                    // Créer un panel pour les compétences disponibles
                    Panel competencesDispoPanel = new Panel(new GridLayout(4));
                    competencesDispoPanel.addComponent(new Label("Nom"));
                    competencesDispoPanel.addComponent(new Label("Description"));
                    competencesDispoPanel.addComponent(new Label("Prix"));
                    competencesDispoPanel.addComponent(new Label("Action"));
                    
                    for (Competence competence : competencesDisponibles) {
                        competencesDispoPanel.addComponent(new Label(competence.getNom()));
                        competencesDispoPanel.addComponent(new Label(competence.getDescription()));
                        competencesDispoPanel.addComponent(new Label(String.valueOf(competence.getPrix())));
                        
                        Button btnAcheter = new Button("Acheter", () -> {
                            try {
                                boolean succes = competenceService.acheterCompetence(joueur, competence);
                                if (succes) {
                                    afficherMessageSucces("Compétence " + competence.getNom() + " achetée avec succès!");
                                    // Rafraîchir l'écran
                                    fenetreFinale.close();
                                    afficherEcranGestionCompetences(joueur);
                                }
                            } catch (Exception e) {
                                afficherMessageErreur(e.getMessage());
                            }
                        });
                        
                        // Désactiver le bouton si le joueur n'a pas assez d'argent
                        if (joueur.getArgent() < competence.getPrix()) {
                            btnAcheter.setEnabled(false);
                        }
                        
                        competencesDispoPanel.addComponent(btnAcheter);
                    }
                    
                    panel.addComponent(competencesDispoPanel);
                } else {
                    panel.addComponent(new Label("Vous avez déjà acheté toutes les compétences disponibles."));
                }
            } else {
                panel.addComponent(new Label("Vous avez atteint le nombre maximum de compétences (4)."));
            }
            
            panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));
            
            // Bouton pour continuer vers le combat
            Button btnLancerCombat = new Button("Lancer le Combat", () -> {
                fenetreFinale.close();
                
                // Initialisation du service de combat
                try {
                    // Appliquer les compétences achetées
                    competenceService.appliquerCompetences(joueur);
                    
                    be.helha.projects.GuerreDesRoyaumes.DAOImpl.CombatDAOImpl combatDAO = new be.helha.projects.GuerreDesRoyaumes.DAOImpl.CombatDAOImpl();
                    ServiceCombat serviceCombat = new ServiceCombatImpl(joueurDAO, combatDAO);
                    
                    // Lancement du combat
                    new EcranCombat(joueurDAO, textGUI, screen, joueur, null, serviceCombat).afficher();
                } catch (Exception e) {
                    afficherMessageErreur("Erreur lors de l'initialisation du combat: " + e.getMessage());
                }
            });
            
            // Bouton pour annuler et revenir à la sélection d'items
            Button btnRetourItems = new Button("Revenir à la Sélection d'Items", () -> {
                fenetreFinale.close();
                afficherEcranGestionCoffre(joueur, true);
            });
            
            // Bouton pour annuler la préparation au combat
            Button btnAnnuler = new Button("Annuler le Combat", () -> {
                // Afficher une boîte de dialogue de confirmation
                MessageDialogBuilder confirmDialog = new MessageDialogBuilder()
                        .setTitle("Confirmation")
                        .setText("Êtes-vous sûr de vouloir annuler la préparation au combat?")
                        .addButton(MessageDialogButton.Yes)
                        .addButton(MessageDialogButton.No);

                MessageDialogButton reponse = confirmDialog.build().showDialog(textGUI);
                
                if (reponse == MessageDialogButton.Yes) {
                    // Si l'utilisateur choisit "Oui", vider l'inventaire de combat et les compétences
                    annulerPreparationCombat(joueur);
                    
                    // Revenir à l'écran principal
                    fenetreFinale.close();
                    afficher();
                }
                // Si l'utilisateur choisit "Non", ne rien faire
            });
            
            // Panel pour les boutons d'action
            Panel boutonsPanel = new Panel(new GridLayout(3));
            boutonsPanel.addComponent(btnLancerCombat);
            boutonsPanel.addComponent(btnRetourItems);
            boutonsPanel.addComponent(btnAnnuler);
            
            panel.addComponent(boutonsPanel);
            
            fenetre.setComponent(panel);
            textGUI.addWindowAndWait(fenetre);
        } catch (Exception e) {
            afficherMessageErreur("Erreur lors de l'affichage de l'écran de gestion des compétences: " + e.getMessage());
            e.printStackTrace();
            afficher(); // Retour à l'écran principal en cas d'erreur
        }
    }

    /**
     * Vide l'inventaire de combat et les compétences achetées du joueur.
     * @param joueur Le joueur dont l'inventaire et les compétences doivent être vidés
     */
    private void annulerPreparationCombat(Joueur joueur) {
        try {
            // Vider l'inventaire de combat
            boolean vidageInventaire = viderInventaireCombatVersCoffre(joueur);
            
            // Vider les compétences
            CompetenceServiceImpl competenceService = CompetenceServiceImpl.getInstance();
            boolean reinitCompetences = competenceService.reinitialiserCompetences(joueur);
            
            if (!vidageInventaire) {
                afficherMessageErreur("Certains items n'ont pas pu être retransférés vers le coffre.");
            }
            
            if (!reinitCompetences) {
                afficherMessageErreur("Les compétences n'ont pas pu être réinitialisées.");
            }
            
            if (vidageInventaire && reinitCompetences) {
                afficherMessageSucces("Préparation au combat annulée avec succès.");
            }
        } catch (Exception e) {
            afficherMessageErreur("Erreur lors de l'annulation de la préparation au combat: " + e.getMessage());
        }
    }

}