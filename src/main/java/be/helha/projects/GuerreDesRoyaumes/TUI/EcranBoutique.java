package be.helha.projects.GuerreDesRoyaumes.TUI;

import be.helha.projects.GuerreDesRoyaumes.DAO.ItemDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceBoutique;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.screen.Screen;

import java.util.List;

public class EcranBoutique {

    private final ServiceBoutique serviceBoutique;
    private final ItemDAO itemDAO;
    private final Joueur joueur;
    private final Screen screen;
    private final WindowBasedTextGUI textGUI;

    public EcranBoutique(ServiceBoutique serviceBoutique, ItemDAO itemDAO, Joueur joueur, Screen screen) {
        this.serviceBoutique = serviceBoutique;
        this.itemDAO = itemDAO;
        this.joueur = joueur;
        this.screen = screen;
        this.textGUI = new MultiWindowTextGUI(screen);
    }

    public void afficher() {
        Window fenetre = new BasicWindow("Boutique");
        fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(1));
        panel.addComponent(new Label("Or disponible: " + joueur.getArgent()));
        panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));

        // Liste des items disponibles
        List<Item> items = itemDAO.obtenirTousLesItems();
        if (items.isEmpty()) {
            panel.addComponent(new Label("Aucun item disponible"));
        } else {
            // Créer une table pour afficher les items
            Table<String> tableItems = new Table<>("ID", "Nom", "Prix", "Type");
            for (Item item : items) {
                tableItems.getTableModel().addRow(
                        String.valueOf(item.getId()),
                        item.getNom(),
                        "100", // Prix fictif pour l'exemple
                        getItemType(item)
                );
            }
            panel.addComponent(tableItems);

            // Ajouter des champs pour acheter un item
            panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));
            panel.addComponent(new Label("Acheter un item:"));

            Panel panelAchat = new Panel(new GridLayout(2));
            panelAchat.addComponent(new Label("ID de l'item:"));
            TextBox champItemId = new TextBox(new TerminalSize(10, 1));
            panelAchat.addComponent(champItemId);

            panelAchat.addComponent(new Label("Quantité:"));
            TextBox champQuantite = new TextBox(new TerminalSize(10, 1)).setText("1");
            panelAchat.addComponent(champQuantite);

            panel.addComponent(panelAchat);

            panel.addComponent(new Button("Acheter", () -> {
                try {
                    int itemId = Integer.parseInt(champItemId.getText());
                    int quantite = Integer.parseInt(champQuantite.getText());

                    boolean succes = serviceBoutique.acheterItem(joueur.getId(), itemId, quantite);
                    if (succes) {
                        afficherMessage("Achat réussi");
                        // Rafraîchir l'affichage
                        fenetre.close();
                        afficher();
                    } else {
                        afficherMessageErreur("Échec de l'achat");
                    }
                } catch (NumberFormatException e) {
                    afficherMessageErreur("Veuillez entrer des valeurs numériques valides");
                } catch (Exception e) {
                    afficherMessageErreur(e.getMessage());
                }
            }));
        }

        panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));
        panel.addComponent(new Button("Retour", fenetre::close));

        fenetre.setComponent(panel);
        textGUI.addWindowAndWait(fenetre);
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
