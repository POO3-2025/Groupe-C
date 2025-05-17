package be.helha.projects.GuerreDesRoyaumes.TUI.Ecran;

import be.helha.projects.GuerreDesRoyaumes.Config.ConnexionConfig.ConnexionManager;
import be.helha.projects.GuerreDesRoyaumes.Controller.CombatController;
import be.helha.projects.GuerreDesRoyaumes.DAO.ItemDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.CombatDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.ItemDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Coffre;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Slot;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.*;
import be.helha.projects.GuerreDesRoyaumes.Model.Royaume;
import be.helha.projects.GuerreDesRoyaumes.Service.CoffreService;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceAuthentification;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceBoutique;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceCombat;
import be.helha.projects.GuerreDesRoyaumes.ServiceImpl.CoffreServiceImpl;
import be.helha.projects.GuerreDesRoyaumes.ServiceImpl.CoffreServiceMongoImpl;
import be.helha.projects.GuerreDesRoyaumes.ServiceImpl.ServiceBoutiqueImpl;
import be.helha.projects.GuerreDesRoyaumes.ServiceImpl.ServiceCombatImpl;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.screen.Screen;
import org.bson.Document;

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
            try {
                afficherEcranCombat(joueur);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }));

        panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));
        panel.addComponent(new Button("Déconnexion", fenetre::close));

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
                // Hacher le nouveau mot de passe
                joueur.setMotDePasse(org.springframework.security.crypto.bcrypt.BCrypt.hashpw(motDePasse, org.springframework.security.crypto.bcrypt.BCrypt.gensalt()));
            }

            // Mettre à jour les informations du joueur
            joueur.setNom(nom);
            joueur.setPrenom(prenom);
            joueur.setPseudo(pseudo);

            try {
                joueurDAO.mettreAJourJoueur(joueur);
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
            
            // Si on trouve un personnage dans MongoDB mais pas dans le joueur, on le met à jour
            if (personnageMongo != null && joueur.getPersonnage() == null) {
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
        } else if (personnageMongo != null) {
            panel.addComponent(new Label("Votre personnage actuel (MongoDB): " + personnageMongo.getClass().getSimpleName()));
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
                    afficher(); // Retour à l'écran principal
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
            ItemDAO itemDAO = ItemDAOImpl.getInstance();
            EcranBoutique ecranBoutique = new EcranBoutique(serviceBoutique, itemDAO, joueur, screen);
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

    private void afficherEcranCombat(Joueur joueur) throws SQLException {
        // Vérifier si le joueur a un personnage
        if (joueur.getPersonnage() == null) {
            afficherMessageErreur("Vous devez d'abord choisir un personnage avant de combattre.");
            return;
        }

        // Obtenir la liste des joueurs disponibles pour le combat
        List<Joueur> adversairesPotentiels = joueurDAO.obtenirTousLesJoueurs();
        adversairesPotentiels.removeIf(j ->
                j.getPseudo().equals(joueur.getPseudo()) || // Enlever le joueur actuel
                        j.getPersonnage() == null // Enlever les joueurs sans personnage
        );

        if (adversairesPotentiels.isEmpty()) {
            afficherMessageErreur("Aucun adversaire disponible pour le combat. Assurez-vous qu'il y a au moins un autre joueur avec un personnage.");
            return;
        }

        // Créer une fenêtre pour choisir l'adversaire
        Window fenetreAdversaire = new BasicWindow("Choisir un adversaire");
        Panel panelAdversaire = new Panel(new GridLayout(1));
        panelAdversaire.addComponent(new Label("Choisissez votre adversaire :"));

        // Ajouter un bouton pour chaque adversaire potentiel
        for (Joueur adversaire : adversairesPotentiels) {
            Button boutonAdversaire = new Button(adversaire.getPseudo() + " (" + adversaire.getPersonnage().getNom() + ")", () -> {
                fenetreAdversaire.close();
                demarrerCombat(joueur, adversaire);
            });
            panelAdversaire.addComponent(boutonAdversaire);
        }

        fenetreAdversaire.setComponent(panelAdversaire);
        textGUI.addWindowAndWait(fenetreAdversaire);
    }

    private void demarrerCombat(Joueur joueur, Joueur adversaire) {
        // Initialisation du service de combat
        ServiceCombat serviceCombat = new ServiceCombatImpl();

        // Création et configuration du DAO
        CombatDAOImpl combatDAO = new CombatDAOImpl();

        try {
            // Obtenir une connexion directe via ConnexionManager
            Connection connection = ConnexionManager.getInstance().getSQLConnection();
            combatDAO.setConnection(connection);
            System.out.println("Connexion SQL établie pour le combat via ConnexionManager");
        } catch (SQLException e) {
            afficherMessageErreur("Erreur de connexion à la base de données: " + e.getMessage());
            return;
        }

        // Initialisation du CombatController avec les deux joueurs
        CombatController combatController = new CombatController(
                serviceCombat,
                combatDAO,
                joueur,
                adversaire
        );

        try {
            combatController.initialiserCombat();
        } catch (IllegalStateException e) {
            afficherMessageErreur("Erreur lors de l'initialisation du combat : " + e.getMessage());
            return;
        }

        // Vérifier si le combat est correctement initialisé
        if (combatController.getCombatEnCours() == null) {
            afficherMessageErreur("Erreur lors de l'initialisation du combat");
            return;
        }

        // Afficher l'écran de préparation au combat
        EcranPreparationCombat ecranPreparationCombat = new EcranPreparationCombat(combatController, textGUI);
        ecranPreparationCombat.afficher();
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