package be.helha.projects.GuerreDesRoyaumes.TUI.Ecran;

import be.helha.projects.GuerreDesRoyaumes.DAO.CombatSessionMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.DTO.SkillManager;
import be.helha.projects.GuerreDesRoyaumes.Model.Bot.AdvancedBot;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Slot;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Arme;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Bouclier;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Potion;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceCombat;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.screen.Screen;

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
    private String actionJoueur = null;
    private String actionBot = null;
    
    // Dépendances
    private final CombatSessionMongoDAO sessionDAO;
    private final SkillManager skillManager;
    
    // Bot pour le mode solo
    private final AdvancedBot bot;
    
    public EcranCombat(JoueurDAO joueurDAO, WindowBasedTextGUI textGUI, Screen screen,
                       Joueur joueur, Joueur adversaire, ServiceCombat serviceCombat, 
                       CombatSessionMongoDAO sessionDAO, SkillManager skillManager) {
        this.joueurDAO = joueurDAO;
        this.textGUI = textGUI;
        this.screen = screen;
        this.joueur = joueur;
        this.adversaire = adversaire;
        this.serviceCombat = serviceCombat;
        this.sessionDAO = sessionDAO;
        this.skillManager = skillManager;
        
        // Initialiser le bot avec l'adversaire
        this.bot = new AdvancedBot(adversaire);
        
        // Utiliser les valeurs réelles de vie des personnages au lieu de les forcer à 100
        // Ces valeurs viennent des classes spécifiques (Golem: 120, Titan: 200, Voleur: 90, Guerrier: 100)
        System.out.println("Points de vie initiaux de " + joueur.getPersonnage().getNom() + ": " + joueur.getPersonnage().getVie());
        System.out.println("Points de vie initiaux de " + adversaire.getPersonnage().getNom() + ": " + adversaire.getPersonnage().getVie());
    }

    public void afficher() {
        // Le joueur humain joue toujours en premier
        boolean estTourDuJoueur = true;

        Window fenetre = new BasicWindow("Combat - Tour " + tourActuel + "/" + MAX_TOURS);
        fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(1));

        // Statut du combat - utiliser Math.floor pour un affichage cohérent
        panel.addComponent(new Label("Votre personnage: " + joueur.getPersonnage().getNom() +
                " (PV: " + (int)Math.floor(joueur.getPersonnage().getPointsDeVie()) + ")"));
        panel.addComponent(new Label("Adversaire: " + adversaire.getPersonnage().getNom() +
                " (PV: " + (int)Math.floor(adversaire.getPersonnage().getPointsDeVie()) + ")"));

        panel.addComponent(new EmptySpace());

        // Si c'est au joueur de jouer
        if (estTourDuJoueur) {
            panel.addComponent(new Label("Tour " + tourActuel + " - Choisissez votre action:"));

            // Actions possibles
            Panel actionsPanel = new Panel(new GridLayout(4));

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
            
            actionsPanel.addComponent(new Button("Utiliser Item", () -> {
                // Ouvrir une fenêtre pour sélectionner un item
                afficherSelectionItems(fenetre);
            }));

            panel.addComponent(actionsPanel);
        }

        // Ajouter un bouton pour abandonner le combat
        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Abandonner", () -> {
            MessageDialogButton confirmation = new MessageDialogBuilder()
                .setTitle("Confirmation")
                .setText("Êtes-vous sûr de vouloir abandonner ce combat?")
                .addButton(MessageDialogButton.Yes)
                .addButton(MessageDialogButton.No)
                .build()
                .showDialog(textGUI);
            
            if (confirmation == MessageDialogButton.Yes) {
                fenetre.close();
                terminerCombat(null); // L'adversaire (bot) gagne par forfait
            }
        }));

        fenetre.setComponent(panel);
        textGUI.addWindowAndWait(fenetre);
    }

    private void executerAction(String typeAction) {
        try {
            // Enregistrer l'action du joueur
            actionJoueur = typeAction;
            String messageJoueur = "Vous avez choisi: " + typeAction;
            
            // Afficher l'action du joueur
            Window fenetreAttente = new BasicWindow("Action en cours");
            fenetreAttente.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));
            
            Panel panelAttente = new Panel(new GridLayout(1));
            panelAttente.addComponent(new Label(messageJoueur));
            panelAttente.addComponent(new Label("Le bot réfléchit..."));
            
            fenetreAttente.setComponent(panelAttente);
            textGUI.addWindow(fenetreAttente);
            
            // Simuler un délai
            bot.simulerDelaiReflexion();
            
            // Fermer la fenêtre d'attente
            fenetreAttente.close();
            
            // Le bot choisit son action en fonction de l'état du jeu
            actionBot = bot.choisirAction(joueur, tourActuel);
            
            // Appliquer les effets de l'action du joueur et du bot
            appliquerEffetsActions(actionJoueur, actionBot);
            
            // Afficher les résultats du tour
            afficherResultatsTour();
            
            // Passer au tour suivant
            tourActuel++;
            
            // Vérifier si le combat est terminé
            if (estCombatTermine()) {
                terminerCombat();
            } else {
                // Continuer au tour suivant
                afficher();
            }
        } catch (Exception e) {
            afficherMessageErreur("Erreur lors de l'exécution de l'action: " + e.getMessage());
        }
    }
    
    private void appliquerEffetsActions(String actionJoueur, String actionBot) {
        // Calculer les dégâts en fonction des actions et des statistiques spécifiques de chaque type de personnage
        int degatsJoueur = 0;
        int degatsBot = 0;
        
        Personnage joueurPerso = joueur.getPersonnage();
        Personnage botPerso = adversaire.getPersonnage();
        
        // Afficher les statistiques réelles des personnages
        System.out.println("Statistiques de " + joueurPerso.getNom() + ": " +
                "Vie=" + joueurPerso.getVie() + ", " +
                "Dégâts=" + joueurPerso.getDegats() + ", " +
                "Résistance=" + joueurPerso.getResistance());
        
        System.out.println("Statistiques du bot " + botPerso.getNom() + ": " +
                "Vie=" + botPerso.getVie() + ", " +
                "Dégâts=" + botPerso.getDegats() + ", " +
                "Résistance=" + botPerso.getResistance());
        
        // Dégâts de base selon l'action du joueur utilisant ses statistiques spécifiques
        switch (actionJoueur) {
            case "attaque":
                // Utiliser les dégâts exacts du personnage sans variation
                degatsJoueur = (int)joueurPerso.getDegats();
                break;
            case "special":
                // Compétence spéciale: 50% de bonus, sans variation
                degatsJoueur = (int)(joueurPerso.getDegats() * 1.5);
                break;
            case "utiliser_item":
                // Les dégâts des items sont gérés par appliquerEffetsActionsAvecItem
                return;
        }
        
        // Utiliser la méthode calculerDegatsAction du bot pour ses dégâts
        degatsBot = bot.calculerDegatsAction(actionBot);
        
        // Bonus de défense supplémentaire si action de défense
        if (actionBot.equals("defense")) {
            // 25% de réduction supplémentaire si le bot est en défense
            degatsJoueur = (int)(degatsJoueur * 0.75);
        }
        
        if (actionJoueur.equals("defense")) {
            // 25% de réduction supplémentaire si le joueur est en défense
            degatsBot = (int)(degatsBot * 0.75);
        }
        
        // S'assurer que les dégâts ne sont pas négatifs
        degatsJoueur = Math.max(0, degatsJoueur);
        degatsBot = Math.max(0, degatsBot);
        
        System.out.println("Dégâts bruts infligés par " + joueurPerso.getNom() + ": " + degatsJoueur);
        System.out.println("Dégâts bruts infligés par " + botPerso.getNom() + ": " + degatsBot);
        
        // Appliquer les dégâts avec la méthode subirDegats spécifique à chaque personnage
        // Cette méthode prend en compte la résistance selon la formule définie dans chaque classe
        if (degatsJoueur > 0) {
            botPerso.subirDegats(degatsJoueur);
        }
        
        if (degatsBot > 0) {
            joueurPerso.subirDegats(degatsBot);
        }
    }
    
    private void afficherResultatsTour() {
        Window fenetreResultat = new BasicWindow("Résultats du tour " + tourActuel);
        fenetreResultat.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));
        
        Panel panel = new Panel(new GridLayout(1));
        
        // Action du joueur
        panel.addComponent(new Label("Vous avez choisi: " + actionJoueur));
        switch (actionJoueur) {
            case "attaque":
                panel.addComponent(new Label("Vous attaquez et infligez des dégâts."));
                break;
            case "defense":
                panel.addComponent(new Label("Vous vous mettez en position défensive."));
                break;
            case "special":
                panel.addComponent(new Label("Vous utilisez votre compétence spéciale!"));
                break;
            case "utiliser_item":
                panel.addComponent(new Label("Vous utilisez un item de votre inventaire."));
                break;
            default:
                panel.addComponent(new Label("Action inconnue."));
                break;
        }
        
        panel.addComponent(new EmptySpace());
        
        // Action du bot
        String messageBot = bot.executerAction(actionBot, actionJoueur);
        String[] lignes = messageBot.split("\n");
        for (String ligne : lignes) {
            panel.addComponent(new Label(ligne));
        }
        
        panel.addComponent(new EmptySpace());
        
        // Points de vie après le tour - utiliser les valeurs exactes sans conversion en int
        panel.addComponent(new Label("Points de vie restants:"));
        panel.addComponent(new Label("- Vous: " + (int)Math.floor(joueur.getPersonnage().getPointsDeVie())));
        panel.addComponent(new Label("- Bot: " + (int)Math.floor(adversaire.getPersonnage().getPointsDeVie())));
        
        panel.addComponent(new Button("Continuer", fenetreResultat::close));
        
        fenetreResultat.setComponent(panel);
        textGUI.addWindowAndWait(fenetreResultat);
    }

    private boolean estCombatTermine() {
        return tourActuel > MAX_TOURS ||
                joueur.getPersonnage().getPointsDeVie() <= 0 ||
                adversaire.getPersonnage().getPointsDeVie() <= 0;
    }

    private void terminerCombat() {
        Joueur vainqueur = determinerVainqueur();
        
        terminerCombat(vainqueur);
    }
    
    private void terminerCombat(Joueur vainqueur) {
        // Méthode surchargée pour l'abandon ou la fin du combat
        Window fenetre = new BasicWindow(vainqueur != null && vainqueur.getId() == joueur.getId() ? "Victoire !" : "Défaite...");
        fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(1));
        
        boolean joueurHumainGagne = (vainqueur != null && vainqueur.getId() == joueur.getId());
        
        try {
            if (joueurHumainGagne) {
                panel.addComponent(new Label("Félicitations! Vous avez vaincu " + adversaire.getPersonnage().getNom()));
                
                // Enregistrer la victoire
                serviceCombat.enregistrerVictoire(joueur);
                
                // Mettre à jour le niveau du royaume
                serviceCombat.terminerCombat(joueur, adversaire, joueur);
                
                panel.addComponent(new Label("Votre nombre de victoires a été incrémenté."));
                panel.addComponent(new Label("Le niveau de votre royaume a été augmenté de 1!"));
            } else {
                if (vainqueur == null) {
                    panel.addComponent(new Label("Vous avez abandonné le combat."));
                    panel.addComponent(new Label("Votre adversaire remporte la victoire."));
                } else {
                    panel.addComponent(new Label("Vous avez été vaincu par " + adversaire.getPersonnage().getNom()));
                }
                
                // Enregistrer la défaite directement dans la base de données
                try {
                    // Utiliser directement le DAO pour s'assurer que la défaite est enregistrée
                    System.out.println("Enregistrement de la défaite pour le joueur ID=" + joueur.getId() + " (pseudo: " + joueur.getPseudo() + ")");
                    serviceCombat.getCombatDAO().enregistrerDefaite(joueur);
                    // Mettre également à jour l'objet joueur
                    joueur.ajouterDefaite();
                    System.out.println("Après ajout, le joueur " + joueur.getPseudo() + " a maintenant " + joueur.getDefaites() + " défaites");
                } catch (Exception ex) {
                    System.err.println("Erreur lors de l'enregistrement direct de la défaite: " + ex.getMessage());
                    ex.printStackTrace();
                }
                
                // Enregistrer la défaite via le service de combat
                serviceCombat.terminerCombat(joueur, adversaire, adversaire);
                
                panel.addComponent(new Label("Votre nombre de défaites a été incrémenté."));
            }
        } catch (Exception e) {
            panel.addComponent(new Label("Erreur lors de la finalisation du combat: " + e.getMessage()));
        }

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Retour au menu principal", () -> {
            fenetre.close();
            retourMenuPrincipal();
        }));

        fenetre.setComponent(panel);
        textGUI.addWindowAndWait(fenetre);
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

    // Nouvelle méthode pour afficher et sélectionner des items
    private void afficherSelectionItems(Window fenetrePrecedente) {
        Window fenetreItems = new BasicWindow("Sélection d'item");
        fenetreItems.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));
        
        Panel panel = new Panel(new GridLayout(1));
        panel.addComponent(new Label("Choisissez un item à utiliser:"));
        
        // Récupérer les items de l'inventaire du joueur
        List<Slot> slots = joueur.getPersonnage().getInventaire().getSlots();
        boolean itemsTrouves = false;
        
        for (Slot slot : slots) {
            if (slot != null && slot.getItem() != null && slot.getQuantity() > 0) {
                itemsTrouves = true;
                Item item = slot.getItem();
                String description = item.getNom() + " (x" + slot.getQuantity() + ")";
                
                if (item instanceof Potion) {
                    Potion potion = (Potion) item;
                    if (potion.getSoin() > 0) {
                        description += " - Soin: +" + potion.getSoin();
                    }
                    if (potion.getDegats() > 0) {
                        description += " - Dégâts: +" + potion.getDegats();
                    }
                } else if (item instanceof Arme) {
                    Arme arme = (Arme) item;
                    description += " - Dégâts: +" + arme.getDegats();
                } else if (item instanceof Bouclier) {
                    Bouclier bouclier = (Bouclier) item;
                    description += " - Défense: +" + bouclier.getDefense();
                }
                
                panel.addComponent(new Button(description, () -> {
                    // Utiliser l'item sélectionné
                    utiliserItem(item);
                    fenetreItems.close();
                    fenetrePrecedente.close();
                }));
            }
        }
        
        if (!itemsTrouves) {
            panel.addComponent(new Label("Votre inventaire est vide!"));
        }
        
        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Retour", fenetreItems::close));
        
        fenetreItems.setComponent(panel);
        textGUI.addWindowAndWait(fenetreItems);
    }
    
    // Méthode pour utiliser un item sélectionné
    private void utiliserItem(Item item) {
        try {
            // Retirer l'item de l'inventaire
            joueur.getPersonnage().getInventaire().enleverItem(item, 1);
            
            // Enregistrer l'action du joueur
            actionJoueur = "utiliser_item";
            String messageJoueur = "Vous avez utilisé: " + item.getNom();
            
            // Afficher l'action du joueur
            Window fenetreAttente = new BasicWindow("Action en cours");
            fenetreAttente.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));
            
            Panel panelAttente = new Panel(new GridLayout(1));
            panelAttente.addComponent(new Label(messageJoueur));
            panelAttente.addComponent(new Label("Le bot réfléchit..."));
            
            fenetreAttente.setComponent(panelAttente);
            textGUI.addWindow(fenetreAttente);
            
            // Simuler un délai
            bot.simulerDelaiReflexion();
            
            // Fermer la fenêtre d'attente
            fenetreAttente.close();
            
            // Le bot choisit son action
            actionBot = bot.choisirAction(joueur, tourActuel);
            
            // Appliquer les effets de l'action du joueur et du bot
            appliquerEffetsActionsAvecItem(item, actionBot);
            
            // Afficher les résultats du tour
            afficherResultatsTour();
            
            // Passer au tour suivant
            tourActuel++;
            
            // Vérifier si le combat est terminé
            if (estCombatTermine()) {
                terminerCombat();
            } else {
                // Continuer au tour suivant
                afficher();
            }
        } catch (Exception e) {
            afficherMessageErreur("Erreur lors de l'utilisation de l'item: " + e.getMessage());
        }
    }
    
    // Méthode spécifique pour appliquer les effets des actions quand un item est utilisé
    private void appliquerEffetsActionsAvecItem(Item item, String actionBot) {
        // Calculer les dégâts en fonction des actions et des statistiques
        int degatsJoueur = 0;
        int degatsBot = 0;
        int soinJoueur = 0;
        
        Personnage joueurPerso = joueur.getPersonnage();
        Personnage botPerso = adversaire.getPersonnage();
        
        // Appliquer les effets de l'item
        if (item instanceof Potion) {
            Potion potion = (Potion) item;
            if (potion.getSoin() > 0) {
                soinJoueur = (int)potion.getSoin();
                // Soigner le joueur
                joueurPerso.soigner(soinJoueur);
            }
            if (potion.getDegats() > 0) {
                degatsJoueur = (int)potion.getDegats();
            }
        } else if (item instanceof Arme) {
            Arme arme = (Arme) item;
            // Attaque spéciale avec l'arme (dégâts doublés)
            degatsJoueur = (int)(arme.getDegats() * 2);
        } else if (item instanceof Bouclier) {
            // Effet défensif: réduction des dégâts reçus par 2
            // Cet effet sera appliqué plus tard
        }
        
        // Calculer les dégâts du bot
        degatsBot = bot.calculerDegatsAction(actionBot);
        
        // Réduction des dégâts si le joueur a utilisé un bouclier
        if (item instanceof Bouclier) {
            degatsBot = degatsBot / 2;
        }
        
        // Bonus de défense supplémentaire si action de défense du bot
        if (actionBot.equals("defense")) {
            degatsJoueur = (int)(degatsJoueur * 0.75);
        }
        
        // S'assurer que les dégâts ne sont pas négatifs
        degatsJoueur = Math.max(0, degatsJoueur);
        degatsBot = Math.max(0, degatsBot);
        
        // Appliquer les dégâts
        if (degatsJoueur > 0) {
            botPerso.subirDegats(degatsJoueur);
        }
        
        if (degatsBot > 0) {
            joueurPerso.subirDegats(degatsBot);
        }
    }
}
