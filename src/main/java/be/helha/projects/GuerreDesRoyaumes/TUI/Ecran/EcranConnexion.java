package be.helha.projects.GuerreDesRoyaumes.TUI.Ecran;

import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceAuthentification;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.screen.Screen;

public class EcranConnexion {
    private final ServiceAuthentification serviceAuthentification;
    private final WindowBasedTextGUI textGUI;
    private final Screen screen;
    private final EcranAuthentification ecranAuthentification;
    private final JoueurDAO joueurDAO;

    public EcranConnexion(ServiceAuthentification serviceAuthentification, WindowBasedTextGUI textGUI, Screen screen, EcranAuthentification ecranAuthentification, JoueurDAO joueurDAO) {
        this.serviceAuthentification = serviceAuthentification;
        this.textGUI = textGUI;
        this.screen = screen;
        this.ecranAuthentification = ecranAuthentification;
        this.joueurDAO = joueurDAO;
    }

    public void afficher() {
        Window fenetre = new BasicWindow("Connexion - Guerre des Royaumes");
        fenetre.setHints(java.util.Collections.singletonList(Window.Hint.CENTERED));

        Panel panel = new Panel(new GridLayout(2));

        // Champs de connexion
        panel.addComponent(new Label("Pseudo :"));
        TextBox pseudoBox = new TextBox();
        panel.addComponent(pseudoBox);

        panel.addComponent(new Label("Mot de passe :"));
        TextBox mdpBox = new TextBox().setMask('*');
        panel.addComponent(mdpBox);

        panel.addComponent(new EmptySpace());
        panel.addComponent(new Button("Se connecter", () -> {
            String pseudo = pseudoBox.getText();
            String motDePasse = mdpBox.getText();

            if (pseudo.isEmpty() || motDePasse.isEmpty()) {
                afficherMessageErreur("Tous les champs sont obligatoires");
                return;
            }

            try {
                if (serviceAuthentification.authentifierJoueur(pseudo, motDePasse)) {
                    // L'authentification a réussi, maintenant vérifier si le joueur existe
                    try {
                        // Vérifier directement si l'utilisateur existe dans la base de données
                        Joueur joueur = joueurDAO.obtenirJoueurParPseudo(pseudo);
                        if (joueur != null) {
                            fenetre.close();
                            new EcranPrincipal(serviceAuthentification, joueurDAO, pseudo, screen).afficher();
                        } else {
                            afficherMessageErreur("Le joueur avec le pseudo " + pseudo + " existe mais impossible de récupérer ses données.");
                        }
                    } catch (Exception e) {
                        afficherMessageErreur("Erreur lors de la récupération des données du joueur : " + e.getMessage());
                    }
                } else {
                    afficherMessageErreur("Pseudo ou mot de passe incorrect");
                }
            } catch (Exception e) {
                afficherMessageErreur("Erreur lors de la connexion : " + e.getMessage());
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

    private void afficherMessageErreur(String message) {
        new MessageDialogBuilder()
                .setTitle("Erreur")
                .setText(message)
                .addButton(MessageDialogButton.OK)
                .build()
                .showDialog(textGUI);
    }
}