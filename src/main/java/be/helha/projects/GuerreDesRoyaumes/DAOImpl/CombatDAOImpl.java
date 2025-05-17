package be.helha.projects.GuerreDesRoyaumes.DAOImpl;

import be.helha.projects.GuerreDesRoyaumes.Config.ConnexionConfig.ConnexionManager;
import be.helha.projects.GuerreDesRoyaumes.Config.InitialiserAPP;
import be.helha.projects.GuerreDesRoyaumes.DAO.CombatDAO;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.DatabaseException;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.MongoDBConnectionException;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.SQLConnectionException;
import be.helha.projects.GuerreDesRoyaumes.Model.Combat.Combat;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;

@Repository
public class CombatDAOImpl implements CombatDAO {

    private Connection connection;
    private String tableNameCombat = "joueur"; // Table pour gerer et lier les demandes de combat aux joueurs


    public CombatDAOImpl() {
        try {
            this.connection = InitialiserAPP.getSQLConnexion();
            System.out.println("Connexion SQL initialisée via InitialiserAPP");
        } catch (SQLConnectionException e) {
            throw new RuntimeException(e);
        }
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
        System.out.println("Connexion SQL mise à jour dans CombatDAOImpl");
    }

    // Create
    @Override
    public void enregistrerCombat(Combat combat) {
        if (connection == null) {
            throw new IllegalStateException("La connexion n'a pas été initialisée dans CombatDAOImpl");
        }

        // Insérer le combat dans la base de données
        String sql = "INSERT INTO combats (id_combat, joueur1_id, joueur2_id, gagnant, tours_final) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, combat.getId());
            stmt.setInt(2, combat.getJoueur1().getId());
            stmt.setInt(3, combat.getJoueur2().getId());
            stmt.setObject(4, combat.getVainqueur() != null ? combat.getVainqueur().getId() : null);
            stmt.setInt(5, combat.getNombreTours());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void enregistrerVictoire(Joueur joueur) {
        if (connection == null) {
            throw new IllegalStateException("La connexion n'a pas été initialisée dans CombatDAOImpl");
        }

        // Enregistrer une victoire du joueur dans la base de données
        String sql = "UPDATE joueurs SET victoires = victoires + 1 WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, joueur.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    @Override
    public void enregistrerDefaite(Joueur joueur) {
        if (connection == null) {
            throw new IllegalStateException("La connexion n'a pas été initialisée dans CombatDAOImpl");
        }

        // Enregistrer une défaite du joueur dans la base de données
        String sql = "UPDATE joueurs SET defaites = defaites + 1 WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, joueur.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<Joueur> getClassementParVictoires() {
        if (connection == null) {
            throw new IllegalStateException("La connexion n'a pas été initialisée dans CombatDAOImpl");
        }

        // Implémentation de la méthode pour obtenir le classement par victoires
        String sql = "SELECT * FROM joueurs ORDER BY victoires DESC";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            List<Joueur> classement = new ArrayList<>();
            while (resultSet.next()) {
                Joueur joueur = new Joueur();
                joueur.setId(resultSet.getInt("id"));
                joueur.setPseudo(resultSet.getString("pseudo"));
                joueur.setVictoires(resultSet.getInt("victoires"));
                classement.add(joueur);
            }
            return classement;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return List.of();
    }

    @Override
    public List<Joueur> getClassementParDefaites() {
        if (connection == null) {
            throw new IllegalStateException("La connexion n'a pas été initialisée dans CombatDAOImpl");
        }

        // Implémentation de la méthode pour obtenir le classement par défaites
        String sql = "SELECT * FROM joueurs ORDER BY defaites DESC";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            List<Joueur> classement = new ArrayList<>();
            while (resultSet.next()) {
                Joueur joueur = new Joueur();
                joueur.setId(resultSet.getInt("id"));
                joueur.setPseudo(resultSet.getString("pseudo"));
                joueur.setDefaites(resultSet.getInt("defaites"));
                classement.add(joueur);
            }
            return classement;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return List.of();
    }


    // Read
    @Override
    public Combat obtenirCombatParId(int id) {
        if (connection == null) {
            throw new IllegalStateException("La connexion n'a pas été initialisée dans CombatDAOImpl");
        }

        Combat combat = null;
        String sql = "SELECT * FROM combats WHERE id_combat = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return combat;
    }

    @Override
    public List<Combat> obtenirTousLesCombats() {
        if (connection == null) {
            throw new IllegalStateException("La connexion n'a pas été initialisée dans CombatDAOImpl");
        }

        List<Combat> combats = new ArrayList<>();
        String sql = "SELECT c.*, j.id as joueur_id FROM combats c LEFT JOIN joueurs j ON c.joueur_id = j.id";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return combats;
    }

    @Override
    public List<Combat> obtenirCombatsParJoueurId(int joueurId) {
        if (connection == null) {
            throw new IllegalStateException("La connexion n'a pas été initialisée dans CombatDAOImpl");
        }

        List<Combat> combats = new ArrayList<>();
        String sql = "SELECT c.*, j.id as joueur_id FROM combats c LEFT JOIN joueurs j ON c.joueur_id = j.id WHERE j.id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, joueurId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return combats;
    }



    /**
     * Création de la table demandes_combat si elle n'existe pas
     */
    private void creerTableDemandesCombatSiInexistante() {
        try {
            // Vérifier si la table existe déjà
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet tables = dbm.getTables(null, null, "demandes_combat", null);

            if (!tables.next()) {
                // La table n'existe pas, la créer (SQL Server syntax)
                String sql = "CREATE TABLE demandes_combat (" +
                        "id INT IDENTITY(1,1) PRIMARY KEY, " +
                        "id_demandeur INT NOT NULL, " +
                        "id_adversaire INT NOT NULL, " +
                        "date_demande DATETIME DEFAULT GETDATE(), " +
                        "FOREIGN KEY (id_demandeur) REFERENCES " + tableNameCombat + "(id_joueur), " +
                        "FOREIGN KEY (id_adversaire) REFERENCES " + tableNameCombat + "(id_joueur), " +
                        "CONSTRAINT UC_demande UNIQUE (id_demandeur, id_adversaire))";
                try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                    stmt.executeUpdate();
                    System.out.println("DEBUG: Table demandes_combat créée");
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de la table demandes_combat: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean envoyerDemandeCombat(int idDemandeur, int idAdversaire) {
        // S'assurer que la table demandes_combat existe
        creerTableDemandesCombatSiInexistante();

        // Vérifier d'abord si une demande existe déjà
        String sqlCheck = "SELECT COUNT(*) FROM demandes_combat WHERE id_demandeur = ? AND id_adversaire = ?";
        try (PreparedStatement stmtCheck = connection.prepareStatement(sqlCheck)) {
            stmtCheck.setInt(1, idDemandeur);
            stmtCheck.setInt(2, idAdversaire);
            ResultSet rs = stmtCheck.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                // Une demande existe déjà
                System.out.println("DEBUG: Une demande de combat existe déjà du joueur " + idDemandeur + " vers " + idAdversaire);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de demande de combat: " + e.getMessage());
            return false;
        }

        // Insérer la nouvelle demande
        String sql = "INSERT INTO demandes_combat (id_demandeur, id_adversaire) VALUES (?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idDemandeur);
            stmt.setInt(2, idAdversaire);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("DEBUG: Demande de combat envoyée du joueur " + idDemandeur + " vers " + idAdversaire);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'envoi de demande de combat: " + e.getMessage());
            return false;
        }
    }

    @Override
    public int verifierDemandesCombat(int idJoueur) {
        // S'assurer que la table demandes_combat existe
        creerTableDemandesCombatSiInexistante();

        // SQL Server n'utilise pas LIMIT mais TOP
        String sql = "SELECT TOP 1 id_demandeur FROM demandes_combat WHERE id_adversaire = ? ORDER BY date_demande ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idJoueur);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int idDemandeur = rs.getInt("id_demandeur");
                System.out.println("DEBUG: Demande de combat trouvée pour le joueur " + idJoueur + " de la part de " + idDemandeur);
                return idDemandeur;
            }
            return 0; // Pas de demande
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification des demandes de combat: " + e.getMessage());
            return 0; // En cas d'erreur
        }
    }

    @Override
    public boolean accepterDemandeCombat(int idDemandeur, int idAdversaire) {
        // S'assurer que la table demandes_combat existe
        creerTableDemandesCombatSiInexistante();

        // Vérifier que la demande existe
        String sqlCheck = "SELECT COUNT(*) FROM demandes_combat WHERE id_demandeur = ? AND id_adversaire = ?";
        try (PreparedStatement stmtCheck = connection.prepareStatement(sqlCheck)) {
            stmtCheck.setInt(1, idDemandeur);
            stmtCheck.setInt(2, idAdversaire);
            ResultSet rs = stmtCheck.executeQuery();
            if (rs.next() && rs.getInt(1) == 0) {
                // Pas de demande trouvée
                System.out.println("DEBUG: Aucune demande de combat trouvée du joueur " + idDemandeur + " vers " + idAdversaire);
                return false;
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de demande de combat: " + e.getMessage());
            return false;
        }

        // Supprimer la demande car elle va être acceptée
        return supprimerDemandeCombat(idDemandeur, idAdversaire);
    }

    @Override
    public boolean supprimerDemandeCombat(int idDemandeur, int idAdversaire) {
        // S'assurer que la table demandes_combat existe
        creerTableDemandesCombatSiInexistante();

        String sql = "DELETE FROM demandes_combat WHERE id_demandeur = ? AND id_adversaire = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, idDemandeur);
            stmt.setInt(2, idAdversaire);
            int rowsAffected = stmt.executeUpdate();
            System.out.println("DEBUG: Demande de combat supprimée entre joueurs " + idDemandeur + " et " + idAdversaire);
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de demande de combat: " + e.getMessage());
            return false;
        }
    }
}


