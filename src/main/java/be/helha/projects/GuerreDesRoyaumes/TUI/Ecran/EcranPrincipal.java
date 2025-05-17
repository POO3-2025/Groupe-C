package be.helha.projects.GuerreDesRoyaumes.TUI.Ecran;

import be.helha.projects.GuerreDesRoyaumes.Config.ConnexionConfig.ConnexionManager;
import be.helha.projects.GuerreDesRoyaumes.Controller.CombatController;
import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.CombatDAOImpl;
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
            fenetre.close();
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
        boutonsPanel.addComponent(new Button("Modifier Profil", () -> {
            fenetre.close();
            afficherEcranGestionProfil(joueur);
        }));

        boutonsPanel.addComponent(new Button("Choisir Personnage", () -> {
            fenetre.close();
            afficherEcranChoixPersonnage(joueur);
        }));

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

    private void afficherEcranGestionCoffre(Joueur joueur) {
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
            Window fenetre = new BasicWindow("Gestion du Coffre - " + joueur.getPseudo());
            fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

            // En-tête
            panel.addComponent(new Label("═══ Coffre de " + joueur.getPseudo() + " ═══"));
            panel.addComponent(new Label("Or disponible: " + joueur.getArgent() + " TerraCoins"));
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

                // Afficher la valeur totale et la valeur de vente
                panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));
                panel.addComponent(new Label("Valeur totale du coffre: " + valeurTotale + " TerraCoins"));
                panel.addComponent(new Label("Valeur de vente (50%): " + valeurVente + " TerraCoins"));

                // Ajouter des options de gestion
                panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));
                panel.addComponent(new Label("Options:"));

                Panel optionsPanel = new Panel(new GridLayout(2));
                optionsPanel.addComponent(new Label("ID Item:"));
                TextBox idBox = new TextBox();
                optionsPanel.addComponent(idBox);

                optionsPanel.addComponent(new Label("Quantité:"));
                TextBox quantiteBox = new TextBox().setText("1");
                optionsPanel.addComponent(quantiteBox);

                panel.addComponent(optionsPanel);

                Panel boutonsPanel = new Panel(new GridLayout(3));

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
                            fenetre.close();
                            afficherEcranGestionCoffre(joueur);
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
                                fenetre.close();
                                afficherEcranGestionCoffre(joueur);
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
                            fenetre.close();
                            afficherEcranGestionCoffre(joueur);
                        } else {
                            afficherMessageErreur("Erreur lors de la vente des items");
                        }
                    }
                }));

                panel.addComponent(boutonsPanel);
            }

            panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));
            panel.addComponent(new Button("Retour", () -> {
                fenetre.close();
                afficher(); // Retour à l'écran principal
            }));

            fenetre.setComponent(panel);
            textGUI.addWindowAndWait(fenetre);
        } catch (Exception e) {
            afficherMessageErreur("Erreur lors du chargement du coffre : " + e.getMessage());
            afficher(); // Retour à l'écran principal en cas d'erreur
        }
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

}