package be.helha.projects.GuerreDesRoyaumes.DAOImpl;

import be.helha.projects.GuerreDesRoyaumes.Config.InitialiserAPP;
import be.helha.projects.GuerreDesRoyaumes.DAO.ActionCombatDAO;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.SQLConnectionException;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Implémentation de l'interface ActionCombatDAO pour gérer les actions
 * effectuées par les joueurs pendant les tours de combat
 */
@Repository
public class ActionCombatDAOImpl implements ActionCombatDAO {

    private Connection connection;
    private static ActionCombatDAOImpl instance;

    /**
     * Constructeur par défaut qui initialise la connexion à la base de données
     */
    public ActionCombatDAOImpl() {
        try {
            this.connection = InitialiserAPP.getSQLConnexion();
            System.out.println("Connexion SQL initialisée pour ActionCombatDAOImpl");
            creerTableActionsCombatSiInexistante();
            creerTableActionEtatsPersonnageSiInexistante();
        } catch (SQLConnectionException e) {
            throw new RuntimeException("Erreur lors de l'initialisation de la connexion SQL pour ActionCombatDAOImpl", e);
        }
    }

    /**
     * Méthode Singleton pour obtenir l'instance unique de ActionCombatDAOImpl
     * 
     * @return L'instance unique de ActionCombatDAOImpl
     */
    public static synchronized ActionCombatDAOImpl getInstance() {
        if (instance == null) {
            instance = new ActionCombatDAOImpl();
        }
        return instance;
    }

    /**
     * Définit la connexion SQL à utiliser pour les opérations DAO
     * 
     * @param connection La connexion SQL à utiliser
     */
    public void setConnection(Connection connection) {
        if (connection == null) {
            throw new IllegalArgumentException("La connexion ne peut pas être null");
        }
        this.connection = connection;
        System.out.println("Connexion SQL mise à jour dans ActionCombatDAOImpl");
    }

    /**
     * Crée la table actions_combat si elle n'existe pas déjà
     */
    private void creerTableActionsCombatSiInexistante() {
        String sql = "IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'actions_combat') " +
                    "BEGIN " +
                    "CREATE TABLE actions_combat (" +
                    "id_action INT IDENTITY(1,1) PRIMARY KEY, " +
                    "id_combat VARCHAR(100) NOT NULL, " +
                    "numero_tour INT NOT NULL, " +
                    "joueur_id INT NOT NULL, " +
                    "type_action VARCHAR(50) NOT NULL, " +
                    "parametres TEXT, " +
                    "vie_restante_joueur FLOAT, " +
                    "vie_restante_adversaire FLOAT, " +
                    "date_action DATETIME DEFAULT GETDATE(), " +
                    "FOREIGN KEY (id_combat) REFERENCES combats_en_cours(id_combat), " +
                    "FOREIGN KEY (joueur_id) REFERENCES joueur(id_joueur)" +
                    ") " +
                    "END";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.executeUpdate();
            System.out.println("Table actions_combat créée ou vérifiée avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de la table actions_combat: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Crée la table action_etats_personnage si elle n'existe pas déjà
     * Cette table permet de suivre l'état des personnages avant et après les actions
     */
    private void creerTableActionEtatsPersonnageSiInexistante() {
        String sql = "IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'action_etats_personnage') " +
                    "BEGIN " +
                    "CREATE TABLE action_etats_personnage (" +
                    "id_etat INT IDENTITY(1,1) PRIMARY KEY, " +
                    "id_combat VARCHAR(100) NOT NULL, " +
                    "joueur_id INT NOT NULL, " +
                    "points_de_vie INT NOT NULL, " +
                    "points_defense INT DEFAULT 0, " +
                    "buffs TEXT, " +
                    "FOREIGN KEY (id_combat) REFERENCES combats_en_cours(id_combat), " +
                    "FOREIGN KEY (joueur_id) REFERENCES joueur(id_joueur)" +
                    ") " +
                    "END";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.executeUpdate();
            System.out.println("Table action_etats_personnage créée ou vérifiée avec succès");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de la table action_etats_personnage: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean enregistrerAction(String idCombat, int numeroTour, int joueurId, String typeAction, String parametres) {
        return enregistrerAction(idCombat, numeroTour, joueurId, typeAction, parametres, 0, 0);
    }

    /**
     * Enregistre une action de combat dans la base de données avec les informations de vie restante
     * 
     * @param idCombat L'identifiant du combat
     * @param numeroTour Le numéro du tour (1-5)
     * @param joueurId L'identifiant du joueur qui effectue l'action
     * @param typeAction Le type d'action (attaque, defense, competence, potion)
     * @param parametres Détails supplémentaires sur l'action (JSON ou texte)
     * @param vieRestanteJoueur Points de vie restants du joueur après l'action
     * @param vieRestanteAdversaire Points de vie restants de l'adversaire après l'action
     * @return true si l'action a été enregistrée avec succès, false sinon
     */
    public boolean enregistrerAction(String idCombat, int numeroTour, int joueurId, String typeAction, 
                                   String parametres, double vieRestanteJoueur, double vieRestanteAdversaire) {
        if (connection == null) {
            throw new IllegalStateException("La connexion n'a pas été initialisée dans ActionCombatDAOImpl");
        }
        
        // Vérifier si le joueur a déjà effectué une action pour ce tour
        if (joueurAEffectueAction(idCombat, numeroTour, joueurId)) {
            // Supprimer l'action précédente
            String sqlDelete = "DELETE FROM actions_combat WHERE id_combat = ? AND numero_tour = ? AND joueur_id = ?";
            try (PreparedStatement stmtDelete = connection.prepareStatement(sqlDelete)) {
                stmtDelete.setString(1, idCombat);
                stmtDelete.setInt(2, numeroTour);
                stmtDelete.setInt(3, joueurId);
                stmtDelete.executeUpdate();
            } catch (SQLException e) {
                System.err.println("Erreur lors de la suppression de l'action précédente: " + e.getMessage());
                return false;
            }
        }
        
        // Insérer la nouvelle action
        String sql = "INSERT INTO actions_combat (id_combat, numero_tour, joueur_id, type_action, parametres, " +
                    "vie_restante_joueur, vie_restante_adversaire) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idCombat);
            stmt.setInt(2, numeroTour);
            stmt.setInt(3, joueurId);
            stmt.setString(4, typeAction);
            stmt.setString(5, parametres);
            stmt.setDouble(6, vieRestanteJoueur);
            stmt.setDouble(7, vieRestanteAdversaire);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'enregistrement de l'action: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public String obtenirTypeAction(String idCombat, int numeroTour, int joueurId) {
        if (connection == null) {
            throw new IllegalStateException("La connexion n'a pas été initialisée dans ActionCombatDAOImpl");
        }
        
        String sql = "SELECT type_action FROM actions_combat " +
                    "WHERE id_combat = ? AND numero_tour = ? AND joueur_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idCombat);
            stmt.setInt(2, numeroTour);
            stmt.setInt(3, joueurId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("type_action");
            }
            return null;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération du type d'action: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String obtenirParametresAction(String idCombat, int numeroTour, int joueurId) {
        if (connection == null) {
            throw new IllegalStateException("La connexion n'a pas été initialisée dans ActionCombatDAOImpl");
        }
        
        String sql = "SELECT parametres FROM actions_combat " +
                    "WHERE id_combat = ? AND numero_tour = ? AND joueur_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idCombat);
            stmt.setInt(2, numeroTour);
            stmt.setInt(3, joueurId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("parametres");
            }
            return null;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des paramètres d'action: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean joueurAEffectueAction(String idCombat, int numeroTour, int joueurId) {
        if (connection == null) {
            throw new IllegalStateException("La connexion n'a pas été initialisée dans ActionCombatDAOImpl");
        }
        
        String sql = "SELECT COUNT(*) as count FROM actions_combat " +
                    "WHERE id_combat = ? AND numero_tour = ? AND joueur_id = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idCombat);
            stmt.setInt(2, numeroTour);
            stmt.setInt(3, joueurId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification d'action du joueur: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean tourEstComplet(String idCombat, int numeroTour, int joueur1Id, int joueur2Id) {
        return joueurAEffectueAction(idCombat, numeroTour, joueur1Id) && 
               joueurAEffectueAction(idCombat, numeroTour, joueur2Id);
    }

    @Override
    public boolean supprimerActionsCombat(String idCombat) {
        if (connection == null) {
            throw new IllegalStateException("La connexion n'a pas été initialisée dans ActionCombatDAOImpl");
        }
        
        String sql = "DELETE FROM actions_combat WHERE id_combat = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idCombat);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression des actions du combat: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Obtient le dernier tour complété pour un combat donné
     * 
     * @param idCombat L'identifiant du combat
     * @param joueur1Id L'identifiant du premier joueur
     * @param joueur2Id L'identifiant du deuxième joueur
     * @return Le numéro du dernier tour complété ou 0 si aucun tour n'est complété
     */
    public int obtenirDernierTourComplete(String idCombat, int joueur1Id, int joueur2Id) {
        if (connection == null) {
            throw new IllegalStateException("La connexion n'a pas été initialisée dans ActionCombatDAOImpl");
        }
        
        // On part du principe que les tours sont complétés séquentiellement
        // On vérifie donc les tours dans l'ordre décroissant jusqu'à trouver un tour complet
        for (int tour = 5; tour >= 1; tour--) {
            if (tourEstComplet(idCombat, tour, joueur1Id, joueur2Id)) {
                return tour;
            }
        }
        
        return 0; // Aucun tour complété
    }
    
    /**
     * Récupère toutes les actions d'un tour spécifique
     * 
     * @param idCombat L'identifiant du combat
     * @param numeroTour Le numéro du tour
     * @return Un ResultSet contenant toutes les actions du tour ou null en cas d'erreur
     */
    public ResultSet obtenirActionsTour(String idCombat, int numeroTour) {
        if (connection == null) {
            throw new IllegalStateException("La connexion n'a pas été initialisée dans ActionCombatDAOImpl");
        }
        
        String sql = "SELECT * FROM actions_combat " +
                    "WHERE id_combat = ? AND numero_tour = ? " +
                    "ORDER BY date_action ASC";
        
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, idCombat);
            stmt.setInt(2, numeroTour);
            
            return stmt.executeQuery();
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des actions du tour: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Enregistre l'état d'un personnage à un moment donné du combat
     * 
     * @param idCombat L'identifiant du combat
     * @param joueurId L'identifiant du joueur
     * @param pointsDeVie Points de vie actuels du personnage
     * @param pointsDefense Points de défense actuels du personnage
     * @param buffs JSON des buffs actifs sur le personnage
     * @return true si l'état a été enregistré avec succès, false sinon
     */
    public boolean enregistrerEtatPersonnage(String idCombat, int joueurId, double pointsDeVie, double pointsDefense, String buffs) {
        if (connection == null) {
            throw new IllegalStateException("La connexion n'a pas été initialisée dans ActionCombatDAOImpl");
        }
        
        String sql = "INSERT INTO action_etats_personnage (id_combat, joueur_id, points_de_vie, points_defense, buffs) " +
                     "VALUES (?, ?, ?, ?, ?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idCombat);
            stmt.setInt(2, joueurId);
            stmt.setDouble(3, pointsDeVie);
            stmt.setDouble(4, pointsDefense);
            stmt.setString(5, buffs);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'enregistrement de l'état du personnage: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Récupère le dernier état enregistré d'un personnage dans un combat
     * 
     * @param idCombat L'identifiant du combat
     * @param joueurId L'identifiant du joueur
     * @return ResultSet contenant les informations d'état du personnage, ou null en cas d'erreur
     */
    public ResultSet obtenirDernierEtatPersonnage(String idCombat, int joueurId) {
        if (connection == null) {
            throw new IllegalStateException("La connexion n'a pas été initialisée dans ActionCombatDAOImpl");
        }
        
        String sql = "SELECT TOP 1 * FROM action_etats_personnage " +
                     "WHERE id_combat = ? AND joueur_id = ? " +
                     "ORDER BY id_etat DESC";
        
        try {
            PreparedStatement stmt = connection.prepareStatement(sql);
            stmt.setString(1, idCombat);
            stmt.setInt(2, joueurId);
            
            return stmt.executeQuery();
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de l'état du personnage: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Récupère la dernière action effectuée dans un combat spécifique
     * 
     * @param idCombat L'identifiant du combat
     * @return Description textuelle de la dernière action ou null en cas d'erreur
     */
    public String obtenirDerniereAction(String idCombat) {
        if (connection == null) {
            throw new IllegalStateException("La connexion n'a pas été initialisée dans ActionCombatDAOImpl");
        }
        
        String sql = "SELECT TOP 1 ac.type_action, ac.parametres, ac.joueur_id, j.pseudo_joueur as pseudo " +
                     "FROM actions_combat ac " +
                     "JOIN joueur j ON ac.joueur_id = j.id_joueur " +
                     "WHERE ac.id_combat = ? " +
                     "ORDER BY ac.date_action DESC, ac.id_action DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idCombat);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String typeAction = rs.getString("type_action");
                String parametres = rs.getString("parametres");
                String pseudo = rs.getString("pseudo");
                
                StringBuilder description = new StringBuilder();
                description.append(pseudo).append(" a utilisé ");
                
                switch (typeAction.toLowerCase()) {
                    case "attaque":
                        description.append("une attaque");
                        if (parametres != null && parametres.contains("degats")) {
                            // Extraction simple des dégâts du format JSON
                            String degats = parametres.replaceAll(".*\"degats\":(\\d+\\.?\\d*).*", "$1");
                            description.append(" infligeant ").append(degats).append(" points de dégâts");
                        }
                        break;
                    case "defense":
                        description.append("une défense");
                        if (parametres != null && parametres.contains("bonusDefense")) {
                            // Extraction simple du bonus de défense du format JSON
                            String bonus = parametres.replaceAll(".*\"bonusDefense\":(\\d+\\.?\\d*).*", "$1");
                            description.append(" avec un bonus de ").append(bonus).append(" points");
                        }
                        break;
                    case "competence":
                        description.append("une compétence");
                        if (parametres != null && parametres.contains("nom")) {
                            String nom = parametres.replaceAll(".*\"nom\":\"([^\"]+)\".*", "$1");
                            description.append(" : ").append(nom);
                        }
                        break;
                    case "potion":
                        description.append("une potion");
                        if (parametres != null && parametres.contains("type")) {
                            String type = parametres.replaceAll(".*\"type\":\"([^\"]+)\".*", "$1");
                            description.append(" de ").append(type);
                        }
                        break;
                    default:
                        description.append("une action de type ").append(typeAction);
                }
                
                return description.toString();
            }
            return "Aucune action récente";
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de la dernière action: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Obtient les informations détaillées sur les changements d'état suite à une action
     * 
     * @param idCombat L'identifiant du combat
     * @param joueurId L'identifiant du joueur ayant effectué l'action
     * @param numeroTour Le numéro du tour où l'action a été effectuée
     * @return Description des changements d'état (points de vie, défense) ou null en cas d'erreur
     */
    public String obtenirChangementsEtat(String idCombat, int joueurId, int numeroTour) {
        if (connection == null) {
            throw new IllegalStateException("La connexion n'a pas été initialisée dans ActionCombatDAOImpl");
        }
        
        String sql = "SELECT ac.vie_restante_joueur, ac.vie_restante_adversaire, " +
                     "ac.type_action, j.pseudo_joueur as pseudo " +
                     "FROM actions_combat ac " +
                     "JOIN joueur j ON ac.joueur_id = j.id_joueur " +
                     "WHERE ac.id_combat = ? AND ac.joueur_id = ? AND ac.numero_tour = ? " +
                     "ORDER BY ac.date_action DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idCombat);
            stmt.setInt(2, joueurId);
            stmt.setInt(3, numeroTour);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                double vieJoueur = rs.getDouble("vie_restante_joueur");
                double vieAdversaire = rs.getDouble("vie_restante_adversaire");
                String pseudo = rs.getString("pseudo");
                
                return "État après action de " + pseudo + " - PV Joueur: " + vieJoueur + 
                       ", PV Adversaire: " + vieAdversaire;
            }
            return "Aucune information d'état disponible";
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des changements d'état: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Vérifie s'il y a eu des changements dans l'état du combat depuis une date donnée
     * Utile pour l'actualisation en temps réel
     * 
     * @param idCombat L'identifiant du combat
     * @param lastCheckTime Timestamp de la dernière vérification
     * @return true s'il y a eu des changements, false sinon
     */
    public boolean estCombatModifie(String idCombat, java.sql.Timestamp lastCheckTime) {
        if (connection == null) {
            throw new IllegalStateException("La connexion n'a pas été initialisée dans ActionCombatDAOImpl");
        }
        
        String sql = "SELECT COUNT(*) as count FROM actions_combat " +
                     "WHERE id_combat = ? AND date_action > ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idCombat);
            stmt.setTimestamp(2, lastCheckTime);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
            return false;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification des modifications du combat: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Récupère les derniers points de vie et de défense d'un joueur dans un combat
     * 
     * @param idCombat L'identifiant du combat
     * @param joueurId L'identifiant du joueur
     * @return Un tableau contenant [points_de_vie, points_defense] ou null en cas d'erreur
     */
    public int[] recupererDerniersStats(String idCombat, int joueurId) {
        if (connection == null) {
            throw new IllegalStateException("La connexion n'a pas été initialisée dans ActionCombatDAOImpl");
        }
        
        String sql = "SELECT TOP 1 points_de_vie, points_defense " +
                     "FROM action_etats_personnage " +
                     "WHERE id_combat = ? AND joueur_id = ? " +
                     "ORDER BY id_etat DESC";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idCombat);
            stmt.setInt(2, joueurId);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int[] stats = new int[2];
                stats[0] = rs.getInt("points_de_vie");
                stats[1] = rs.getInt("points_defense");
                return stats;
            }
            
            return null; // Aucun enregistrement trouvé
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des derniers stats du joueur: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Récupère les derniers états de tous les joueurs dans un combat
     * 
     * @param idCombat L'identifiant du combat
     * @return Une map avec joueurId comme clé et un tableau [points_de_vie, points_defense] comme valeur
     */
    public java.util.Map<Integer, int[]> recupererDerniersStatsJoueurs(String idCombat) {
        if (connection == null) {
            throw new IllegalStateException("La connexion n'a pas été initialisée dans ActionCombatDAOImpl");
        }
        
        java.util.Map<Integer, int[]> statsJoueurs = new java.util.HashMap<>();
        
        String sql = "WITH DerniersEtats AS (" +
                     "    SELECT joueur_id, points_de_vie, points_defense, " +
                     "           ROW_NUMBER() OVER (PARTITION BY joueur_id ORDER BY id_etat DESC) AS rn " +
                     "    FROM action_etats_personnage " +
                     "    WHERE id_combat = ? " +
                     ") " +
                     "SELECT joueur_id, points_de_vie, points_defense " +
                     "FROM DerniersEtats " +
                     "WHERE rn = 1";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idCombat);
            
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                int joueurId = rs.getInt("joueur_id");
                int[] stats = new int[2];
                stats[0] = rs.getInt("points_de_vie");
                stats[1] = rs.getInt("points_defense");
                statsJoueurs.put(joueurId, stats);
            }
            
            return statsJoueurs;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des stats des joueurs: " + e.getMessage());
            e.printStackTrace();
            return new java.util.HashMap<>(); // Retourner une map vide en cas d'erreur
        }
    }

    /**
     * Supprime tous les états de personnage liés à un combat spécifique
     * 
     * @param idCombat L'identifiant du combat
     * @return true si la suppression a réussi, false sinon
     */
    public boolean supprimerEtatsPersonnage(String idCombat) {
        if (connection == null) {
            throw new IllegalStateException("La connexion n'a pas été initialisée dans ActionCombatDAOImpl");
        }
        
        String sql = "DELETE FROM action_etats_personnage WHERE id_combat = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, idCombat);
            
            int rowsAffected = stmt.executeUpdate();
            System.out.println("Suppression des états de personnage pour le combat " + idCombat + ": " + rowsAffected + " entrées supprimées");
            return true;
            
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression des états de personnage: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
