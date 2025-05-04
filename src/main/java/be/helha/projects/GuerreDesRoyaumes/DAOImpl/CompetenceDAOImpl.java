package be.helha.projects.GuerreDesRoyaumes.DAOImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.CompetenceDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Competence;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompetenceDAOImpl implements CompetenceDAO {

    private Connection connection;

    public CompetenceDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addCompetence(Competence competence) {
        String sql = "INSERT INTO competences (nom, description, bonusVie, bonusAttaque, bonusDefense, bonusArgent) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, competence.getNom());
            statement.setString(2, competence.getDescription());
            statement.setInt(3, competence.getBonusVie());
            statement.setInt(4, competence.getBonusAttaque());
            statement.setInt(5, competence.getBonusDefense());
            statement.setInt(6, competence.getBonusArgent());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Competence getCompetenceById(int id) {
        String sql = "SELECT * FROM competences WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return new Competence(
                        resultSet.getString("nom"),
                        resultSet.getString("description"),
                        resultSet.getInt("bonusVie"),
                        resultSet.getInt("bonusAttaque"),
                        resultSet.getInt("bonusDefense"),
                        resultSet.getInt("bonusArgent")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Competence> getAllCompetences() {
        List<Competence> competences = new ArrayList<>();
        String sql = "SELECT * FROM competences";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                competences.add(new Competence(
                        resultSet.getString("nom"),
                        resultSet.getString("description"),
                        resultSet.getInt("bonusVie"),
                        resultSet.getInt("bonusAttaque"),
                        resultSet.getInt("bonusDefense"),
                        resultSet.getInt("bonusArgent")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return competences;
    }

    @Override
    public void updateCompetence(Competence competence) {
        String sql = "UPDATE competences SET nom = ?, description = ?, bonusVie = ?, bonusAttaque = ?, bonusDefense = ?, bonusArgent = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, competence.getNom());
            statement.setString(2, competence.getDescription());
            statement.setInt(3, competence.getBonusVie());
            statement.setInt(4, competence.getBonusAttaque());
            statement.setInt(5, competence.getBonusDefense());
            statement.setInt(6, competence.getBonusArgent());
            statement.setInt(7, competence.getId()); // Assurez-vous d'avoir un champ `id` dans votre modèle
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteCompetence(int id) {
        String sql = "DELETE FROM competences WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
