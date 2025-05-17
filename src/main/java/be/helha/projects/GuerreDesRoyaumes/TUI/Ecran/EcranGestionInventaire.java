package be.helha.projects.GuerreDesRoyaumes.TUI.Ecran;

import be.helha.projects.GuerreDesRoyaumes.DAO.CoffreDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.InventaireDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.CoffreMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.InventaireMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Service.InventaireService;
import be.helha.projects.GuerreDesRoyaumes.ServiceImpl.InventaireServiceImpl;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.screen.Screen;

import java.util.List;

public class EcranGestionInventaire {

    private final Joueur joueur;
    private final Screen screen;
    private final WindowBasedTextGUI textGUI;
    private final CoffreDAO coffreDAO;
    private final InventaireDAO inventaireDAO;
    private final InventaireService inventaireService;

    // Composants UI qui seront mis à jour
    private Table<String> tableCoffre;
    private Table<String> tableInventaire;

    public EcranGestionInventaire(Joueur joueur, Screen screen) {
        this.joueur = joueur;
        this.screen = screen;
        this.textGUI = new MultiWindowTextGUI(screen);
        this.coffreDAO = CoffreMongoDAOImpl.getInstance();
        this.inventaireDAO = InventaireMongoDAOImpl.getInstance();
        this.inventaireService = InventaireServiceImpl.getInstance();
    }

    public void afficher() {
        Window fenetre = new BasicWindow("Gestion de l'inventaire");
        fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        Panel mainPanel = new Panel(new GridLayout(1));
        mainPanel.addComponent(new Label("Gestion de l'inventaire de " + joueur.getPseudo()));
        mainPanel.addComponent(new EmptySpace(new TerminalSize(0, 1)));

        // Créer un panel avec deux colonnes pour le coffre et l'inventaire
        Panel inventairePanel = new Panel(new GridLayout(2));

        // Colonne du coffre
        Panel coffrePanel = new Panel(new GridLayout(1));
        coffrePanel.addComponent(new Label("Coffre:"));
        tableCoffre = new Table<>("ID", "Nom", "Type", "Prix", "Détails");
        tableCoffre.setVisibleRows(8);
        coffrePanel.addComponent(tableCoffre);

        // Boutons de transfert depuis le coffre
        Panel boutonsCoffre = new Panel(new GridLayout(2));
        TextBox champIdCoffre = new TextBox(new TerminalSize(5, 1));
        boutonsCoffre.addComponent(new Label("ID:"));
        boutonsCoffre.addComponent(champIdCoffre);

        Button boutonTransfererCoffre = new Button("Vers Inventaire", () -> {
            try {
                int itemId = Integer.parseInt(champIdCoffre.getText());
                if (inventaireService.transfererDuCoffreVersInventaire(joueur, itemId)) {
                    afficherMessage("Item transféré avec succès");
                    rafraichirTables();
                } else {
                    afficherMessageErreur("Impossible de transférer l'item");
                }
                champIdCoffre.setText("");
            } catch (NumberFormatException e) {
                afficherMessageErreur("Veuillez entrer un ID valide");
            } catch (Exception e) {
                afficherMessageErreur("Erreur: " + e.getMessage());
            }
        });

        coffrePanel.addComponent(boutonsCoffre);
        coffrePanel.addComponent(boutonTransfererCoffre);

        // Colonne de l'inventaire
        Panel inventaireColPanel = new Panel(new GridLayout(1));
        inventaireColPanel.addComponent(new Label("Inventaire de combat:"));
        tableInventaire = new Table<>("ID", "Nom", "Type", "Prix", "Détails");
        tableInventaire.setVisibleRows(8);
        inventaireColPanel.addComponent(tableInventaire);

        // Boutons de transfert depuis l'inventaire
        Panel boutonsInventaire = new Panel(new GridLayout(2));
        TextBox champIdInventaire = new TextBox(new TerminalSize(5, 1));
        boutonsInventaire.addComponent(new Label("ID:"));
        boutonsInventaire.addComponent(champIdInventaire);

        Button boutonTransfererInventaire = new Button("Vers Coffre", () -> {
            try {
                int itemId = Integer.parseInt(champIdInventaire.getText());
                if (inventaireService.transfererDeInventaireVersCoffre(joueur, itemId)) {
                    afficherMessage("Item transféré avec succès");
                    rafraichirTables();
                } else {
                    afficherMessageErreur("Impossible de transférer l'item");
                }
                champIdInventaire.setText("");
            } catch (NumberFormatException e) {
                afficherMessageErreur("Veuillez entrer un ID valide");
            } catch (Exception e) {
                afficherMessageErreur("Erreur: " + e.getMessage());
            }
        });

        inventaireColPanel.addComponent(boutonsInventaire);
        inventaireColPanel.addComponent(boutonTransfererInventaire);

        // Ajouter les deux colonnes au panel d'inventaire
        inventairePanel.addComponent(coffrePanel);
        inventairePanel.addComponent(inventaireColPanel);

        // Ajouter le panel d'inventaire au panel principal
        mainPanel.addComponent(inventairePanel);

        // Bouton de retour
        mainPanel.addComponent(new EmptySpace(new TerminalSize(0, 1)));
        mainPanel.addComponent(new Button("Retour", fenetre::close));

        // Charger les données initiales
        rafraichirTables();

        fenetre.setComponent(mainPanel);
        textGUI.addWindowAndWait(fenetre);
    }

    /**
     * Rafraîchit les tables d'affichage des items
     */
    private void rafraichirTables() {
        // Vider les tables
        tableCoffre.getTableModel().clear();
        tableInventaire.getTableModel().clear();

        // Remplir la table du coffre
        List<Item> itemsCoffre = coffreDAO.obtenirItemsDuCoffre(joueur.getPseudo());
        for (Item item : itemsCoffre) {
            String details = obtenirDetailsItem(item);
            tableCoffre.getTableModel().addRow(
                    String.valueOf(item.getId()),
                    item.getNom(),
                    item.getType(),
                    String.valueOf(item.getPrix()),
                    details
            );
        }

        // Remplir la table de l'inventaire
        List<Item> itemsInventaire = inventaireDAO.obtenirItemsInventaire(joueur.getPseudo());
        for (Item item : itemsInventaire) {
            String details = obtenirDetailsItem(item);
            tableInventaire.getTableModel().addRow(
                    String.valueOf(item.getId()),
                    item.getNom(),
                    item.getType(),
                    String.valueOf(item.getPrix()),
                    details
            );
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