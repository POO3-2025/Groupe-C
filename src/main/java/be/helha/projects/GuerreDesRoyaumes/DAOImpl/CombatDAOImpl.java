package be.helha.projects.GuerreDesRoyaumes.DAOImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.CombatDAO;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.DatabaseException;
import be.helha.projects.GuerreDesRoyaumes.Model.Combat.Combat;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;

@Repository
public class CombatDAOImpl implements CombatDAO {

    private Connection connection;

    public CombatDAOImpl() {
        // Constructeur par défaut
    }

    @Autowired(required = false)
    public void setDataSource(DataSource dataSource) {
        if (dataSource == null) {
            System.out.println("DataSource est null dans CombatDAOImpl.setDataSource");
            return;
        }

        try {
            this.connection = dataSource.getConnection();
            System.out.println("Connexion établie via DataSource dans CombatDAOImpl");
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la connexion à la base de données via DataSource", e);
        }
    }

    /**
     * Méthode alternative pour définir directement la connexion
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
        System.out.println("Connexion SQL définie directement dans CombatDAOImpl");
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
}


