package be.helha.projects.GuerreDesRoyaumes.DAOImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.CombatDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Combat;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class CombatDAOImpl implements CombatDAO {

    private Connection connection;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        try {
            this.connection = dataSource.getConnection();
            // Ici on pourrait ajouter une méthode pour créer la table si elle n'existe pas
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la connexion à la base de données", e);
        }
    }

    // Constructeur par défaut requis par Spring
    public CombatDAOImpl() {
    }

    @Override
    public void ajouterCombat(Combat combat) {
        String sql = "INSERT INTO combats (nbrTour, victoire, joueur_id) VALUES (?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setInt(1, combat.getNbrTour());
            statement.setBoolean(2, combat.isVictoire());
            statement.setInt(3, combat.getJoueur().getId());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                combat.setId(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Combat obtenirCombatParId(int id) {
        String sql = "SELECT c.*, j.id as joueur_id FROM combats c LEFT JOIN joueurs j ON c.joueur_id = j.id WHERE c.id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return extraireCombatDeResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Combat> obtenirTousLesCombats() {
        List<Combat> combats = new ArrayList<>();
        String sql = "SELECT c.*, j.id as joueur_id FROM combats c LEFT JOIN joueurs j ON c.joueur_id = j.id";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                combats.add(extraireCombatDeResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return combats;
    }

    @Override
    public List<Combat> obtenirCombatsParJoueurId(int joueurId) {
        List<Combat> combats = new ArrayList<>();
        String sql = "SELECT c.*, j.id as joueur_id FROM combats c LEFT JOIN joueurs j ON c.joueur_id = j.id WHERE j.id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, joueurId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                combats.add(extraireCombatDeResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return combats;
    }

    @Override
    public void mettreAJourCombat(Combat combat) {
        String sql = "UPDATE combats SET nbrTour = ?, victoire = ?, joueur_id = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, combat.getNbrTour());
            statement.setBoolean(2, combat.isVictoire());
            statement.setInt(3, combat.getJoueur().getId());
            statement.setInt(4, combat.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimerCombat(int id) {
        String sql = "DELETE FROM combats WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Combat extraireCombatDeResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        int nbrTour = resultSet.getInt("nbrTour");
        boolean victoire = resultSet.getBoolean("victoire");

        // Créer un joueur avec juste l'ID (à compléter plus tard)
        int joueurId = resultSet.getInt("joueur_id");
        Joueur joueur = new Joueur();
        joueur.setId(joueurId);

        return new Combat(id, nbrTour, victoire, joueur);
    }
}