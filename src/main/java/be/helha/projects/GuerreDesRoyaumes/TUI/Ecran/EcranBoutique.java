package be.helha.projects.GuerreDesRoyaumes.TUI.Ecran;

import be.helha.projects.GuerreDesRoyaumes.DAO.ItemDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
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
import com.googlecode.lanterna.gui2.table.Table;
import com.googlecode.lanterna.screen.Screen;

import java.util.List;

public class EcranBoutique {

    private final ServiceBoutique serviceBoutique;
    private final ItemDAO itemDAO;
    private final JoueurDAO joueurDAO;
    private final Joueur joueur;
    private final Screen screen;
    private final WindowBasedTextGUI textGUI;

    public EcranBoutique(ServiceBoutique serviceBoutique, ItemDAO itemDAO, Joueur joueur, Screen screen) {
        this.serviceBoutique = serviceBoutique;
        this.itemDAO = itemDAO;
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

        // Liste des items disponibles
        List<Item> items = itemDAO.obtenirTousLesItems();
        if (items.isEmpty()) {
            panel.addComponent(new Label("Aucun item disponible"));
        } else {
            // Créer une table pour afficher les items
            Table<String> tableItems = new Table<>("ID", "Nom", "Type", "Prix", "Description");
            for (Item item : items) {
                String description = "";
                if (item.getClass().getSimpleName().equals("Arme")) {
                    description = "Dégâts: " + ((Arme)item).getDegats();
                } else if (item.getClass().getSimpleName().equals("Bouclier")) {
                    description = "Défense: " + ((Bouclier)item).getDefense();
                } else if (item.getClass().getSimpleName().equals("Potion")) {
                    Potion potion = (Potion)item;
                    description = "Soin: " + potion.getSoin() + ", Dégâts: " + potion.getDegats();
                }

                tableItems.getTableModel().addRow(
                        String.valueOf(item.getId()),
                        item.getNom(),
                        item.getType(),
                        String.valueOf(item.getPrix()),
                        description
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

                    // Vérifier si l'item existe
                    Item itemAcheter = itemDAO.obtenirItemParId(itemId);
                    if (itemAcheter == null) {
                        afficherMessageErreur("L'item avec l'ID " + itemId + " n'existe pas.");
                        return;
                    }

                    // Vérifier si le joueur a assez d'argent
                    int coutTotal = itemAcheter.getPrix() * quantite;
                    if (joueur.getArgent() < coutTotal) {
                        afficherMessageErreur("Vous n'avez pas assez d'or. Coût total: " + coutTotal + ", Or disponible: " + joueur.getArgent());
                        return;
                    }

                    boolean succes = serviceBoutique.acheterItem(joueur.getId(), itemId, quantite);
                    if (succes) {
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

                        afficherMessage("Achat réussi! Vous avez acheté " + quantite + "x " + itemAcheter.getNom() + " pour " + coutTotal + " or.");

                        // Mise à jour de l'affichage de l'or disponible
                        labelOr.setText("Or disponible: " + joueur.getArgent());

                        // Réinitialisation des champs de saisie
                        champItemId.setText("");
                        champQuantite.setText("1");
                    } else {
                        afficherMessageErreur("Échec de l'achat. Veuillez réessayer.");
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
