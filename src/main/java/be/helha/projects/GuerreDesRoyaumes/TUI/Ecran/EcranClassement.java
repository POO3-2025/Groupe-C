package be.helha.projects.GuerreDesRoyaumes.TUI.Ecran;

import be.helha.projects.GuerreDesRoyaumes.Config.ConnexionConfig.ConnexionManager;
import be.helha.projects.GuerreDesRoyaumes.Config.ConnexionConfig.SQLConfigManager;
import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Service.ClassementService;
import be.helha.projects.GuerreDesRoyaumes.ServiceImpl.ClassementServiceImpl;
import com.googlecode.lanterna.SGR;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogBuilder;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.screen.Screen;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;

public class EcranClassement {

    private final Screen screen;
    private final WindowBasedTextGUI textGUI;
    private final String pseudo;
    private final JoueurDAO joueurDAO;
    private ClassementService classementService;
    private Connection connection;

    public EcranClassement(JoueurDAO joueurDAO, Screen screen, String pseudo) {
        this.joueurDAO = joueurDAO;
        this.screen = screen;
        this.pseudo = pseudo;
        this.textGUI = new MultiWindowTextGUI(screen);
        
        // Obtenir une connexion SQL
        try {
            // D'abord, essayer d'obtenir la connexion du JoueurDAO (qui est déjà utilisée ailleurs)
            if (joueurDAO != null) {
                try {
                    // Utiliser la méthode de reflexion pour accéder à la connexion privée
                    java.lang.reflect.Field connectionField = joueurDAO.getClass().getDeclaredField("connexion");
                    connectionField.setAccessible(true);
                    this.connection = (Connection) connectionField.get(joueurDAO);
                    System.out.println("Connexion obtenue depuis le JoueurDAO: " + (this.connection != null));
                } catch (Exception e) {
                    System.out.println("Impossible d'accéder à la connexion du JoueurDAO: " + e.getMessage());
                }
            }
            
            // Si la connexion est null ou fermée, essayer d'autres méthodes
            if (this.connection == null || this.connection.isClosed()) {
                try {
                    this.connection = ConnexionManager.getInstance().getSQLConnection();
                    System.out.println("Connexion obtenue depuis ConnexionManager: " + (this.connection != null));
                } catch (Exception e) {
                    System.out.println("Erreur avec ConnexionManager: " + e.getMessage());
                }
            }
            
            // Dernière tentative avec SQLConfigManager
            if (this.connection == null || this.connection.isClosed()) {
                try {
                    this.connection = SQLConfigManager.getInstance().getConnection();
                    System.out.println("Connexion obtenue depuis SQLConfigManager: " + (this.connection != null));
                } catch (Exception e) {
                    System.out.println("Erreur avec SQLConfigManager: " + e.getMessage());
                }
            }
            
            // Vérifier si la connexion fonctionne en exécutant une requête simple
            if (this.connection != null && !this.connection.isClosed()) {
                try (Statement stmt = this.connection.createStatement();
                     ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM joueur")) {
                    if (rs.next()) {
                        int count = rs.getInt(1);
                        System.out.println("Nombre de joueurs dans la base de données: " + count);
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors du test de la connexion: " + e.getMessage());
                }
            }
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'obtention de la connexion SQL: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Créer le service avec la connexion obtenue
        this.classementService = new ClassementServiceImpl(this.connection);
    }

    public void afficher() {
        Joueur joueur = joueurDAO.obtenirJoueurParPseudo(pseudo);
        if (joueur == null) {
            afficherMessageErreur("Joueur non trouvé");
            return;
        }

        // Récupérer les classements
        List<Map<String, Object>> classementVD;
        List<Map<String, Object>> classementRichesse;
        List<Map> classementRoyaumes;
        
        try {
            // Si la connexion est disponible, récupérer les données directement (sans passer par le service)
            if (connection != null && !connection.isClosed()) {
                // Pour Victoires/Défaites
                classementVD = new ArrayList<>();
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(
                             "SELECT id_joueur, pseudo_joueur as nom, victoires_joueur as victoire, defaites_joueur as defaite, (victoires_joueur - defaites_joueur) AS score " +
                             "FROM joueur ORDER BY score DESC")) {
                    while (rs.next()) {
                        Map<String, Object> row = new HashMap<>();
                        row.put("id", rs.getLong("id_joueur"));
                        row.put("nom", rs.getString("nom"));
                        row.put("victoire", rs.getInt("victoire"));
                        row.put("defaite", rs.getInt("defaite"));
                        row.put("score", rs.getInt("score"));
                        classementVD.add(row);
                    }
                    System.out.println("ClassementVD: " + classementVD.size() + " entrées récupérées");
                } catch (Exception e) {
                    System.err.println("Erreur SQL (VD): " + e.getMessage());
                    e.printStackTrace();
                    classementVD = classementService.getClassementVictoiresDefaites();
                }
                
                // Pour Richesse
                classementRichesse = new ArrayList<>();
                try (Statement stmt = connection.createStatement();
                     ResultSet rs = stmt.executeQuery(
                             "SELECT id_joueur, pseudo_joueur as nom, argent_joueur as argent FROM joueur ORDER BY argent_joueur DESC")) {
                    while (rs.next()) {
                        Map<String, Object> row = new HashMap<>();
                        row.put("id", rs.getLong("id_joueur"));
                        row.put("nom", rs.getString("nom"));
                        row.put("argent", rs.getInt("argent"));
                        classementRichesse.add(row);
                    }
                    System.out.println("ClassementRichesse: " + classementRichesse.size() + " entrées récupérées");
                } catch (Exception e) {
                    System.err.println("Erreur SQL (Richesse): " + e.getMessage());
                    classementRichesse = classementService.getClassementRichesse();
                }
                
                // Pour les royaumes, utiliser le service car cela implique MongoDB
                classementRoyaumes = classementService.getClassementNiveauRoyaumes();
            } else {
                // Utiliser le service si la connexion directe n'est pas disponible
                classementVD = classementService.getClassementVictoiresDefaites();
                classementRichesse = classementService.getClassementRichesse();
                classementRoyaumes = classementService.getClassementNiveauRoyaumes();
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des classements: " + e.getMessage());
            e.printStackTrace();
            
            // Données par défaut en cas d'erreur
            classementVD = creerDonneesFictives();
            classementRichesse = creerDonneesFictives();
            classementRoyaumes = creerDonneesFictivesRoyaumes();
            
            afficherMessageErreur("Erreur lors de la récupération des classements: " + e.getMessage());
        }

        // Créer la fenêtre principale
        Window fenetre = new BasicWindow("Classements - Guerre des Royaumes");
        fenetre.setHints(Collections.singletonList(Window.Hint.CENTERED));

        // Créer un panel principal
        Panel panelPrincipal = new Panel(new LinearLayout(Direction.VERTICAL));
        
        // Créer les panels pour chaque type de classement
        Panel panelVD = creerPanelClassementVD(classementVD, joueur);
        Panel panelRichesse = creerPanelClassementRichesse(classementRichesse, joueur);
        Panel panelRoyaumes = creerPanelClassementRoyaumes(classementRoyaumes, joueur);
        
        // Panel de contenu qui contiendra le classement actif
        Panel panelContenu = new Panel();
        panelContenu.setLayoutManager(new LinearLayout(Direction.VERTICAL));
        
        // Initialiser avec le premier classement
        panelContenu.addComponent(panelVD);
        
        // Créer les boutons d'onglets
        Panel panelOnglets = new Panel(new LinearLayout(Direction.HORIZONTAL));
        
        Button ongletVD = new Button("Victoires/Défaites", () -> {
            panelContenu.removeAllComponents();
            panelContenu.addComponent(panelVD);
            panelContenu.invalidate();
        });
        
        Button ongletRichesse = new Button("Richesse", () -> {
            panelContenu.removeAllComponents();
            panelContenu.addComponent(panelRichesse);
            panelContenu.invalidate();
        });
        
        Button ongletRoyaumes = new Button("Niveau de Royaume", () -> {
            panelContenu.removeAllComponents();
            panelContenu.addComponent(panelRoyaumes);
            panelContenu.invalidate();
        });
        
        panelOnglets.addComponent(ongletVD);
        panelOnglets.addComponent(ongletRichesse);
        panelOnglets.addComponent(ongletRoyaumes);
        
        // Ajouter les panels au panel principal
        panelPrincipal.addComponent(panelOnglets);
        panelPrincipal.addComponent(new EmptySpace(new TerminalSize(0, 1)));
        panelPrincipal.addComponent(panelContenu);
        panelPrincipal.addComponent(new EmptySpace(new TerminalSize(0, 1)));
        
        // Bouton pour retourner au menu principal
        Button retourButton = new Button("Retour au menu principal", () -> {
            fenetre.close();
            new EcranPrincipal(null, joueurDAO, pseudo, screen).afficher();
        });
        panelPrincipal.addComponent(retourButton);

        fenetre.setComponent(panelPrincipal);
        textGUI.addWindowAndWait(fenetre);
    }
    
    private List<Map<String, Object>> creerDonneesFictives() {
        List<Map<String, Object>> data = new ArrayList<>();
        Map<String, Object> row = new HashMap<>();
        row.put("id", 1L);
        row.put("nom", "Les données ne peuvent pas être récupérées");
        row.put("victoire", 0);
        row.put("defaite", 0);
        row.put("score", 0);
        row.put("argent", 0);
        data.add(row);
        return data;
    }
    
    private List<Map> creerDonneesFictivesRoyaumes() {
        List<Map> data = new ArrayList<>();
        Map<String, Object> map = new HashMap<>();
        map.put("nom", "Les données ne peuvent pas être récupérées");
        map.put("niveau", 0);
        map.put("joueurNom", "Inconnu");
        data.add(map);
        return data;
    }

    private Panel creerPanelClassementVD(List<Map<String, Object>> classement, Joueur joueurActuel) {
        Panel panel = new Panel(new GridLayout(5));
        
        // En-têtes
        panel.addComponent(new Label("#").addStyle(SGR.BOLD));
        panel.addComponent(new Label("Joueur").addStyle(SGR.BOLD));
        panel.addComponent(new Label("Victoires").addStyle(SGR.BOLD));
        panel.addComponent(new Label("Défaites").addStyle(SGR.BOLD));
        panel.addComponent(new Label("Score").addStyle(SGR.BOLD));
        
        int position = 1;
        for (Map<String, Object> joueur : classement) {
            String nomJoueur = (String) joueur.get("nom");
            
            // Afficher la position
            Label posLabel = new Label(getPositionSymbole(position));
            panel.addComponent(posLabel);
            
            // Afficher le nom du joueur (en surbrillance si c'est le joueur actuel)
            Label nomLabel = new Label(nomJoueur);
            if (nomJoueur.equals(joueurActuel.getPseudo())) {
                nomLabel.setForegroundColor(TextColor.ANSI.YELLOW);
                nomLabel.addStyle(SGR.BOLD);
            }
            panel.addComponent(nomLabel);
            
            // Afficher les statistiques
            panel.addComponent(new Label(joueur.get("victoire") != null ? joueur.get("victoire").toString() : "0"));
            panel.addComponent(new Label(joueur.get("defaite") != null ? joueur.get("defaite").toString() : "0"));
            
            // Score avec mise en forme
            Label scoreLabel = new Label(joueur.get("score") != null ? joueur.get("score").toString() : "0");
            if (position <= 3) {
                scoreLabel.addStyle(SGR.BOLD);
            }
            panel.addComponent(scoreLabel);
            
            position++;
        }
        
        return panel;
    }

    private Panel creerPanelClassementRichesse(List<Map<String, Object>> classement, Joueur joueurActuel) {
        Panel panel = new Panel(new GridLayout(3));
        
        // En-têtes
        panel.addComponent(new Label("#").addStyle(SGR.BOLD));
        panel.addComponent(new Label("Joueur").addStyle(SGR.BOLD));
        panel.addComponent(new Label("Fortune").addStyle(SGR.BOLD));
        
        int position = 1;
        for (Map<String, Object> joueur : classement) {
            String nomJoueur = (String) joueur.get("nom");
            
            // Afficher la position
            Label posLabel = new Label(getPositionSymbole(position));
            panel.addComponent(posLabel);
            
            // Afficher le nom du joueur (en surbrillance si c'est le joueur actuel)
            Label nomLabel = new Label(nomJoueur);
            if (nomJoueur.equals(joueurActuel.getPseudo())) {
                nomLabel.setForegroundColor(TextColor.ANSI.YELLOW);
                nomLabel.addStyle(SGR.BOLD);
            }
            panel.addComponent(nomLabel);
            
            // Fortune avec mise en forme
            Label fortuneLabel = new Label((joueur.get("argent") != null ? joueur.get("argent") : "0") + " pièces d'or");
            if (position <= 3) {
                fortuneLabel.addStyle(SGR.BOLD);
                if (position == 1) {
                    fortuneLabel.setForegroundColor(TextColor.ANSI.YELLOW);
                }
            }
            panel.addComponent(fortuneLabel);
            
            position++;
        }
        
        return panel;
    }

    private Panel creerPanelClassementRoyaumes(List<Map> classement, Joueur joueurActuel) {
        Panel panel = new Panel(new GridLayout(4));
        
        // En-têtes
        panel.addComponent(new Label("#").addStyle(SGR.BOLD));
        panel.addComponent(new Label("Royaume").addStyle(SGR.BOLD));
        panel.addComponent(new Label("Joueur").addStyle(SGR.BOLD));
        panel.addComponent(new Label("Niveau").addStyle(SGR.BOLD));
        
        int position = 1;
        for (Map royaume : classement) {
            String nomRoyaume = (String) royaume.get("nom");
            String nomJoueur = (String) royaume.get("joueurNom");
            
            // Afficher la position
            Label posLabel = new Label(getPositionSymbole(position));
            panel.addComponent(posLabel);
            
            // Afficher le nom du royaume
            panel.addComponent(new Label(nomRoyaume));
            
            // Afficher le nom du joueur (en surbrillance si c'est le joueur actuel)
            Label nomLabel = new Label(nomJoueur);
            if (nomJoueur.equals(joueurActuel.getPseudo())) {
                nomLabel.setForegroundColor(TextColor.ANSI.YELLOW);
                nomLabel.addStyle(SGR.BOLD);
            }
            panel.addComponent(nomLabel);
            
            // Niveau avec mise en forme
            Label niveauLabel = new Label(royaume.get("niveau") != null ? royaume.get("niveau").toString() : "0");
            if (position <= 3) {
                niveauLabel.addStyle(SGR.BOLD);
                if (position == 1) {
                    niveauLabel.setForegroundColor(TextColor.ANSI.GREEN);
                }
            }
            panel.addComponent(niveauLabel);
            
            position++;
        }
        
        return panel;
    }

    private String getPositionSymbole(int position) {
        switch (position) {
            case 1:
                return "🥇";
            case 2:
                return "🥈";
            case 3:
                return "🥉";
            default:
                return String.valueOf(position);
        }
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