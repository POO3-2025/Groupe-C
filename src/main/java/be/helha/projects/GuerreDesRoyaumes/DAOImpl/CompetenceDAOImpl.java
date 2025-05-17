package be.helha.projects.GuerreDesRoyaumes.DAOImpl;

import be.helha.projects.GuerreDesRoyaumes.Config.InitialiserAPP;
import be.helha.projects.GuerreDesRoyaumes.DAO.CompetenceDAO;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.SQLConnectionException;
import be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat.Competence;
import be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat.DoubleDegats;
import be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat.DoubleArgent;
import be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat.Regeneration;
import be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat.DoubleResistance;
import com.mongodb.client.MongoDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CompetenceDAOImpl implements CompetenceDAO {

    private static CompetenceDAOImpl instance;
    private Connection connection;
    private final String tableName = "competence";

    private CompetenceDAOImpl() {
        try {
            connection = InitialiserAPP.getSQLConnexion();
        } catch (SQLConnectionException ex) {
            throw new RuntimeException(ex);
        }
    }

    public void setMongodb(Connection mongodb) {
        this.connection = mongodb;
        creerTableCompetenceSiInexistante();
    }

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
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la création de la table competence: " + e.getMessage());
        }
    }

    public synchronized static CompetenceDAOImpl getInstance() {
        if (instance == null) {
            instance = new CompetenceDAOImpl();
        }
        return instance;
    }

    @Override
    public void addCompetence(Competence competence) {
        String sql = "INSERT INTO " + tableName + " (id_competence, nom_competence, prix_competence, description_competence, type_competence) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, competence.getId());
            statement.setString(2, competence.getNom());
            statement.setInt(3, competence.getPrix());
            statement.setString(4, competence.getDescription());
            statement.setString(5, getCompetenceType(competence));
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Competence getCompetenceById(int id) {
        String sql = "SELECT * FROM " + tableName + " WHERE id_competence = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return extractCompetenceFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Competence> getAllCompetences() {
        List<Competence> competences = new ArrayList<>();
        String sql = "SELECT * FROM " + tableName;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                competences.add(extractCompetenceFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return competences;
    }

    @Override
    public void updateCompetence(Competence competence) {
        String sql = "UPDATE " + tableName + " SET nom_competence = ?, prix_competence = ?, description_competence = ?, type_competence = ? WHERE id_competence = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, competence.getNom());
            statement.setInt(2, competence.getPrix());
            statement.setString(3, competence.getDescription());
            statement.setString(4, getCompetenceType(competence));
            statement.setString(5, competence.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteCompetence(int id) {
        String sql = "DELETE FROM " + tableName + " WHERE id_competence = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private Competence extractCompetenceFromResultSet(ResultSet resultSet) throws SQLException {
        String type = resultSet.getString("type_competence");

        // Créer l'instance de compétence appropriée selon le type
        if ("double_degats".equals(type)) {
            return new DoubleDegats();
        } else if ("double_argent".equals(type)) {
            return new DoubleArgent();
        } else if ("regeneration".equals(type)) {
            return new Regeneration();
        } else if ("double_resistance".equals(type)) {
            return new DoubleResistance();
        } else {
            throw new SQLException("Type de compétence inconnu: " + type);
        }
    }

    private String getCompetenceType(Competence competence) {
        if (competence instanceof DoubleDegats) {
            return "double_degats";
        } else if (competence instanceof DoubleArgent) {
            return "double_argent";
        } else if (competence instanceof Regeneration) {
            return "regeneration";
        } else if (competence instanceof DoubleResistance) {
            return "double_resistance";
        } else {
            return "unknown";
        }
    }
}

