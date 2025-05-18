package be.helha.projects.GuerreDesRoyaumes.TUI.Ecran;

import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.*;
import be.helha.projects.GuerreDesRoyaumes.Model.Royaume;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceAuthentification;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.screen.Screen;
import be.helha.projects.GuerreDesRoyaumes.Config.DAOProvider;

public class EcranInscription {
    private final ServiceAuthentification serviceAuthentification;
    private final WindowBasedTextGUI textGUI;
    private final Screen screen;
    private final EcranAuthentification ecranAuthentification;
    private final JoueurDAO joueurDAO;

    public EcranInscription(ServiceAuthentification serviceAuthentification, WindowBasedTextGUI textGUI, Screen screen, EcranAuthentification ecranAuthentification) {
        this.serviceAuthentification = serviceAuthentification;
        this.textGUI = textGUI;
        this.screen = screen;
        this.ecranAuthentification = ecranAuthentification;
        this.joueurDAO = ecranAuthentification.getJoueurDAO();
    }

    public void afficher() {
        Window fenetre = new BasicWindow("Inscription - Guerre des Royaumes");
        fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(2));
        
        // Champs d'inscription
        panel.addComponent(new Label("Nom :"));
        TextBox nomBox = new TextBox();
        panel.addComponent(nomBox);

        panel.addComponent(new Label("Prénom :"));
        TextBox prenomBox = new TextBox();
        panel.addComponent(prenomBox);

        panel.addComponent(new Label("Pseudo :"));
        TextBox pseudoBox = new TextBox();
        panel.addComponent(pseudoBox);

        panel.addComponent(new Label("Mot de passe :"));
        TextBox mdpBox = new TextBox().setMask('*');
        panel.addComponent(mdpBox);

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("S'inscrire", () -> {
            String nom = nomBox.getText();
            String prenom = prenomBox.getText();
            String pseudo = pseudoBox.getText();
            String motDePasse = mdpBox.getText();

            if (nom.isEmpty() || prenom.isEmpty() || pseudo.isEmpty() || motDePasse.isEmpty()) {
                afficherMessageErreur("Tous les champs sont obligatoires");
                return;
            }

            try {
                serviceAuthentification.inscrireJoueur(nom, prenom, pseudo, motDePasse);
                fenetre.close();
                afficherInitialisation(pseudo);
            } catch (IllegalArgumentException e) {
                afficherMessageErreur(e.getMessage());
            }
        }));

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Retour", () -> {
            fenetre.close();
            ecranAuthentification.afficher();
        }));

        fenetre.setComponent(panel);
        textGUI.addWindowAndWait(fenetre);
    }

    private void afficherInitialisation(String pseudo) {
        Joueur joueur = null;
        try {
            // Récupérer le joueur nouvellement inscrit
            joueur = serviceAuthentification.obtenirJoueurParPseudo(pseudo);
            if (joueur == null) {
                afficherMessageErreur("Erreur: Impossible de récupérer les informations du joueur");
                return;
            }
            
            // Fenêtre pour le nom du royaume
            Window fenetreRoyaume = new BasicWindow("Création du Royaume - Guerre des Royaumes");
            fenetreRoyaume.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));
            
            Panel panelRoyaume = new Panel(new GridLayout(1));
            panelRoyaume.addComponent(new Label("Bienvenue dans le jeu Guerre des Royaumes, " + pseudo + " !"));
            panelRoyaume.addComponent(new Label("Veuillez choisir un nom pour votre royaume :"));
            
            // Vérifier si le royaume est initialisé
            if (joueur.getRoyaume() == null) {
                // Créer un nouveau royaume si null
                joueur.setRoyaume(new Royaume(0, "Royaume de " + pseudo, 1));
            }
            
            TextBox nomRoyaumeBox = new TextBox(joueur.getRoyaume().getNom());
            panelRoyaume.addComponent(nomRoyaumeBox);
            
            panelRoyaume.addComponent(new EmptySpace());
            
            // Un joueur final pour pouvoir l'utiliser dans les lambdas
            final Joueur joueurFinal = joueur;
            
            panelRoyaume.addComponent(new Button("Confirmer", () -> {
                String nomRoyaume = nomRoyaumeBox.getText();
                if (nomRoyaume.isEmpty()) {
                    afficherMessageErreur("Le nom du royaume ne peut pas être vide");
                    return;
                }
                
                try {
                    // Mettre à jour le nom du royaume dans l'objet joueur
                    joueurFinal.getRoyaume().setNom(nomRoyaume);
                    serviceAuthentification.mettreAJourJoueur(joueurFinal);
                    
                    // Mettre à jour le royaume dans MongoDB
                    be.helha.projects.GuerreDesRoyaumes.DAO.RoyaumeMongoDAO royaumeMongoDAO = DAOProvider.getRoyaumeMongoDAO();
                    
                    // Vérifier s'il faut mettre à jour ou créer un royaume dans MongoDB
                    Royaume royaumeMongo = royaumeMongoDAO.obtenirRoyaumeParJoueurId(joueurFinal.getId());
                    if (royaumeMongo != null) {
                        royaumeMongoDAO.mettreAJourRoyaume(joueurFinal.getRoyaume(), joueurFinal.getId());
                    } else {
                        royaumeMongoDAO.ajouterRoyaume(joueurFinal.getRoyaume(), joueurFinal.getId());
                    }
                    
                    fenetreRoyaume.close();
                    afficherEcranChoixPersonnage(joueurFinal);
                } catch (Exception e) {
                    afficherMessageErreur("Erreur lors de la mise à jour du royaume: " + e.getMessage());
                    e.printStackTrace();
                }
            }));
            
            fenetreRoyaume.setComponent(panelRoyaume);
            textGUI.addWindowAndWait(fenetreRoyaume);
        } catch (Exception e) {
            afficherMessageErreur("Erreur lors de l'initialisation: " + e.getMessage());
            e.printStackTrace();
            ecranAuthentification.afficher();
        }
    }
    
    private void afficherEcranChoixPersonnage(Joueur joueur) {
        Window fenetre = new BasicWindow("Choix du Personnage - Guerre des Royaumes");
        fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(1));
        panel.addComponent(new Label("Votre royaume \"" + joueur.getRoyaume().getNom() + "\" a été créé avec succès."));
        panel.addComponent(new Label("Veuillez maintenant choisir votre personnage :"));
        
        panel.addComponent(new EmptySpace());
        
        // Ajouter les boutons pour chaque type de personnage
        String[] personnages = {"Guerrier", "Voleur", "Golem", "Titan"};
        
        for (String nomPersonnage : personnages) {
            Button btnPersonnage = new Button(nomPersonnage, () -> {
                try {
                    // Créer le personnage
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
                    
                    // Enregistrer le personnage dans MongoDB
                    be.helha.projects.GuerreDesRoyaumes.DAO.PersonnageMongoDAO personnageMongoDAO = 
                        DAOProvider.getPersonnageMongoDAO();
                    personnageMongoDAO.ajouterPersonnage(personnage, joueur.getId());
                    
                    // Mettre à jour le joueur
                    joueur.setPersonnage(personnage);
                    serviceAuthentification.mettreAJourJoueur(joueur);

                    // Connecter le joueur
                    serviceAuthentification.connecterJoueur(joueur.getPseudo());

                    fenetre.close();
                    afficherMessageSucces("Personnage " + nomPersonnage + " choisi avec succès");
                    
                    // Rediriger vers l'écran principal au lieu de l'écran d'authentification
                    new EcranPrincipal(serviceAuthentification, joueurDAO, joueur.getPseudo(), screen).afficher();
                } catch (Exception e) {
                    afficherMessageErreur("Erreur lors du choix du personnage: " + e.getMessage());
                }
            });
            panel.addComponent(btnPersonnage);
        }
        
        // Ajouter un bouton pour passer cette étape avec redirection vers l'écran principal
        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Passer cette étape", () -> {
            // Connecter le joueur même s'il passe l'étape
            serviceAuthentification.connecterJoueur(joueur.getPseudo());
            
            fenetre.close();
            // Rediriger vers l'écran principal
            new EcranPrincipal(serviceAuthentification, joueurDAO, joueur.getPseudo(), screen).afficher();
        }));

        fenetre.setComponent(panel);
        textGUI.addWindowAndWait(fenetre);
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
            .setTitle("Succès")
            .setText(message)
            .addButton(MessageDialogButton.OK)
            .build()
            .showDialog(textGUI);
    }
} 