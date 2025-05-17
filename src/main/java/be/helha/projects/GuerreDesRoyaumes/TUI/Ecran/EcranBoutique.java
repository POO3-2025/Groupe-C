package be.helha.projects.GuerreDesRoyaumes.TUI.Ecran;

import be.helha.projects.GuerreDesRoyaumes.DAO.ItemDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.ItemMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.ItemDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.ItemMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.JoueurDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Arme;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Bouclier;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Potion;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceBoutique;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.dialogs.TextInputDialogBuilder;
import com.googlecode.lanterna.screen.Screen;

import java.util.List;

public class EcranBoutique {

    private final ServiceBoutique serviceBoutique;
    private final ItemDAO itemDAO;
    private final ItemMongoDAO itemMongoDAO;
    private final JoueurDAO joueurDAO;
    private final Joueur joueur;
    private final Screen screen;
    private final WindowBasedTextGUI textGUI;

    public EcranBoutique(ServiceBoutique serviceBoutique, ItemDAO itemDAO, Joueur joueur, Screen screen) {
        this.serviceBoutique = serviceBoutique;
        this.itemDAO = itemDAO;
        this.itemMongoDAO = ItemMongoDAOImpl.getInstance();
        this.joueurDAO = JoueurDAOImpl.getInstance();
        this.joueur = joueur;
        this.screen = screen;
        this.textGUI = new MultiWindowTextGUI(screen);
    }

    public void afficher() {
        Window fenetre = new BasicWindow("Boutique");
        fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(1));

        // Création du Label pour l'or qui pourra être mis à jour
        Label labelOr = new Label("Or disponible: " + joueur.getArgent());
        panel.addComponent(labelOr);
        panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));

        // Liste des items disponibles - UTILISER LA COLLECTION MONGODB
        List<Item> items = itemMongoDAO.obtenirTousLesItems();
        if (items.isEmpty()) {
            panel.addComponent(new Label("Aucun item disponible dans la boutique"));
        } else {
            // Log des items disponibles pour le débogage
            System.out.println("Items disponibles dans MongoDB:");
            for (Item item : items) {
                System.out.println("ID: " + item.getId() + ", Nom: " + item.getNom() + ", Type: " + item.getClass().getSimpleName());
            }
            
            // Créer une table pour afficher les items
            panel.addComponent(new Label("=== ARTICLES DISPONIBLES ==="));
            
            // Regrouper les items par catégorie
            Panel panelCategories = new Panel(new GridLayout(1));
            
            // Armes
            Panel panelArmes = new Panel(new GridLayout(1));
            panelArmes.addComponent(new Label("▓▒░ ARMES ░▒▓"));
            boolean armesTrouvees = false;
            
            for (Item item : items) {
                if (item instanceof Arme) {
                    armesTrouvees = true;
                    Arme arme = (Arme) item;
                    String description = "ID: " + item.getId() + " - " + item.getNom() + " - Prix: " + item.getPrix() + " - Dégâts: " + arme.getDegats();
                    
                    Panel itemPanel = new Panel(new GridLayout(2));
                    itemPanel.addComponent(new Label(description));
                    itemPanel.addComponent(new Button("Acheter", () -> {
                        effectuerAchat(item, 1, labelOr);
                    }));
                    
                    panelArmes.addComponent(itemPanel);
                }
            }
            
            if (!armesTrouvees) {
                panelArmes.addComponent(new Label("Aucune arme disponible"));
            }
            panelCategories.addComponent(panelArmes);
            
            // Boucliers
            Panel panelBoucliers = new Panel(new GridLayout(1));
            panelBoucliers.addComponent(new Label("▓▒░ BOUCLIERS ░▒▓"));
            boolean boucliersTrouves = false;
            
            for (Item item : items) {
                if (item instanceof Bouclier) {
                    boucliersTrouves = true;
                    Bouclier bouclier = (Bouclier) item;
                    String description = "ID: " + item.getId() + " - " + item.getNom() + " - Prix: " + item.getPrix() + " - Défense: " + bouclier.getDefense();
                    
                    Panel itemPanel = new Panel(new GridLayout(2));
                    itemPanel.addComponent(new Label(description));
                    itemPanel.addComponent(new Button("Acheter", () -> {
                        effectuerAchat(item, 1, labelOr);
                    }));
                    
                    panelBoucliers.addComponent(itemPanel);
                }
            }
            
            if (!boucliersTrouves) {
                panelBoucliers.addComponent(new Label("Aucun bouclier disponible"));
            }
            panelCategories.addComponent(panelBoucliers);
            
            // Potions
            Panel panelPotions = new Panel(new GridLayout(1));
            panelPotions.addComponent(new Label("▓▒░ POTIONS ░▒▓"));
            boolean potionsTrouvees = false;
            
            for (Item item : items) {
                if (item instanceof Potion) {
                    potionsTrouvees = true;
                    Potion potion = (Potion) item;
                    String description = "ID: " + item.getId() + " - " + item.getNom() + " - Prix: " + item.getPrix() 
                        + " - Soin: " + potion.getSoin() + " - Dégâts: " + potion.getDegats();
                    
                    Panel itemPanel = new Panel(new GridLayout(2));
                    itemPanel.addComponent(new Label(description));
                    itemPanel.addComponent(new Button("Acheter", () -> {
                        // Pour les potions, demander la quantité
                        TextInputDialogBuilder inputDialog = new TextInputDialogBuilder()
                            .setTitle("Quantité")
                            .setDescription("Entrez la quantité souhaitée (1-99):")
                            .setInitialContent("1");
                        
                        String quantiteStr = inputDialog.build().showDialog(textGUI);
                        
                        if (quantiteStr != null) {
                            try {
                                int quantite = Integer.parseInt(quantiteStr);
                                if (quantite <= 0 || quantite > 99) {
                                    afficherMessageErreur("Veuillez entrer une quantité entre 1 et 99");
                                } else {
                                    effectuerAchat(item, quantite, labelOr);
                                }
                            } catch (NumberFormatException e) {
                                afficherMessageErreur("Veuillez entrer un nombre valide");
                            }
                        }
                    }));
                    
                    panelPotions.addComponent(itemPanel);
                }
            }
            
            if (!potionsTrouvees) {
                panelPotions.addComponent(new Label("Aucune potion disponible"));
            }
            panelCategories.addComponent(panelPotions);
            
            panel.addComponent(panelCategories);
        }

        panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));
        panel.addComponent(new Button("Retour", () -> {
            fenetre.close();
            // Rediriger vers l'écran principal
            retourEcranPrincipal();
        }));

        fenetre.setComponent(panel);
        textGUI.addWindowAndWait(fenetre);
        
        // Si on sort de la boucle d'attente de la fenêtre, on revient également à l'écran principal
        retourEcranPrincipal();
    }

    /**
     * Redirige vers l'écran principal
     */
    private void retourEcranPrincipal() {
        try {
            // Créer une instance d'EcranPrincipal sans ServiceAuthentification
            // La valeur null pour ServiceAuthentification est acceptable car l'écran principal 
            // n'en a pas besoin pour son fonctionnement de base
            EcranPrincipal ecranPrincipal = new EcranPrincipal(null, joueurDAO, joueur.getPseudo(), screen);
            ecranPrincipal.afficher();
        } catch (Exception e) {
            System.err.println("Erreur lors du retour à l'écran principal: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Effectue l'achat d'un item
     */
    private void effectuerAchat(Item item, int quantite, Label labelOr) {
        try {
            System.out.println("Tentative d'achat - ID: " + item.getId() + ", Nom: " + item.getNom() + ", Quantité: " + quantite);
            
            // Vérifier si le joueur a assez d'argent
            int coutTotal = item.getPrix() * quantite;
            if (joueur.getArgent() < coutTotal) {
                System.out.println("Fonds insuffisants - Argent: " + joueur.getArgent() + ", Coût: " + coutTotal);
                afficherMessageErreur("Vous n'avez pas assez d'or. Coût total: " + coutTotal + ", Or disponible: " + joueur.getArgent());
                return;
            }

            // Effectuer l'achat un par un pour chaque item plutôt qu'en lot
            boolean tousOk = true;
            for (int i = 0; i < quantite; i++) {
                boolean achatOk = serviceBoutique.acheterItem(joueur.getId(), item.getId(), 1);
                if (!achatOk) {
                    System.out.println("Échec de l'achat du " + (i+1) + "e exemplaire de " + item.getNom());
                    tousOk = false;
                    break;
                } else {
                    System.out.println("Achat réussi du " + (i+1) + "e exemplaire de " + item.getNom());
                }
            }
            
            if (tousOk) {
                // Récupérer les données à jour du joueur depuis la base de données
                try {
                    Joueur joueurMisAJour = joueurDAO.obtenirJoueurParId(joueur.getId());
                    if (joueurMisAJour != null) {
                        // Mettre à jour l'argent de l'objet joueur local
                        joueur.setArgent(joueurMisAJour.getArgent());
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors de la mise à jour des données du joueur: " + e.getMessage());
                }

                afficherMessage("Achat réussi! Vous avez acheté " + quantite + "x " + item.getNom() + " pour " + coutTotal + " or.");

                // Mise à jour de l'affichage de l'or disponible
                labelOr.setText("Or disponible: " + joueur.getArgent());
            } else {
                afficherMessageErreur("Échec partiel de l'achat. Certains items n'ont pas pu être ajoutés au coffre.");
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de l'achat: " + e.getMessage());
            e.printStackTrace();
            afficherMessageErreur(e.getMessage());
        }
    }

    private String getItemType(Item item) {
        // Logique pour déterminer le type d'item
        return item.getClass().getSimpleName();
    }

    private void afficherMessage(String message) {
        MessageDialogBuilder dialogBuilder = new MessageDialogBuilder()
                .setTitle("Information")
                .setText(message)
                .addButton(MessageDialogButton.OK);
        dialogBuilder.build().showDialog(textGUI);
    }

    private void afficherMessageErreur(String message) {
        MessageDialogBuilder dialogBuilder = new MessageDialogBuilder()
                .setTitle("Erreur")
                .setText(message)
                .addButton(MessageDialogButton.OK);
        dialogBuilder.build().showDialog(textGUI);
    }
}
