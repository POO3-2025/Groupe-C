package be.helha.projects.GuerreDesRoyaumes.DAOImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.RoyaumeDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Royaume;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RoyaumeDAOImpl implements RoyaumeDAO {

    private Connection connection;

    public RoyaumeDAOImpl(Connection connection) {
        this.connection = connection;
        creerTableCompetenceSiInexistante();
    }

    /**
     * Crée la table des compétences si elle n'existe pas déjà
     */
    private void creerTableCompetenceSiInexistante() {
        String createTableQuery = """
        IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='competence' AND xtype='U')
        BEGIN
            CREATE TABLE competence (
                id_competence VARCHAR(50) PRIMARY KEY,
                nom_competence NVARCHAR(100) NOT NULL,
                prix_competence INT NOT NULL,
                description_competence NVARCHAR(255) NOT NULL,
                type_competence VARCHAR(50) NOT NULL
            )
        END
        """;

        try (PreparedStatement statement = connection.prepareStatement(createTableQuery)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de la table competence: " + e.getMessage());
        }
    }

    @Override
    public void ajouterRoyaume(Royaume royaume) {
        String sql = "INSERT INTO royaumes (nom, niveau) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, royaume.getNom());
            statement.setInt(2, royaume.getNiveau());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                royaume.setId(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Royaume obtenirRoyaumeParId(int id) {
        String sql = "SELECT * FROM royaumes WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return extraireRoyaumeDeResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Royaume> obtenirTousLesRoyaumes() {
        List<Royaume> royaumes = new ArrayList<>();
        String sql = "SELECT * FROM royaumes";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                royaumes.add(extraireRoyaumeDeResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return royaumes;
    }

    @Override
    public List<Royaume> obtenirRoyaumesParJoueurId(int joueurId) {
        List<Royaume> royaumes = new ArrayList<>();
        String sql = "SELECT r.* FROM royaumes r INNER JOIN joueurs_royaumes jr ON r.id = jr.royaume_id WHERE jr.joueur_id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, joueurId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                royaumes.add(extraireRoyaumeDeResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return royaumes;
    }

    @Override
    public void mettreAJourRoyaume(Royaume royaume) {
        String sql = "UPDATE royaumes SET nom = ?, niveau = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, royaume.getNom());
            statement.setInt(2, royaume.getNiveau());
            statement.setInt(3, royaume.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimerRoyaume(int id) {
        String sql = "DELETE FROM royaumes WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Royaume extraireRoyaumeDeResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String nom = resultSet.getString("nom");
        int niveau = resultSet.getInt("niveau");

        return new Royaume(id, nom, niveau);
    }
}
