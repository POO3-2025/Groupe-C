package be.helha.projects.GuerreDesRoyaumes.DAOImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.RoyaumeDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Royaume;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation de l'interface RoyaumeDAO pour la gestion des royaumes en base de données.
 * Cette classe gère les opérations CRUD pour les entités Royaume.
 */
public class RoyaumeDAOImpl implements RoyaumeDAO {

    private Connection connection;

    /**
     * Constructeur avec connexion à la base de données.
     * Crée également la table des compétences si elle n'existe pas.
     *
     * @param connection La connexion à la base de données à utiliser
     */
    public RoyaumeDAOImpl(Connection connection) {
        this.connection = connection;
        creerTableCompetenceSiInexistante();
    }

    /**
     * Crée la table des compétences si elle n'existe pas déjà.
     *
     * @throws RuntimeException Si une erreur survient lors de la création de la table
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

    /**
     * Ajoute un nouveau royaume dans la base de données.
     *
     * @param royaume Le royaume à ajouter
     */
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

    /**
     * Récupère un royaume par son identifiant.
     *
     * @param id L'identifiant du royaume à récupérer
     * @return Le royaume correspondant à l'identifiant ou null si aucun royaume n'est trouvé
     */
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

    /**
     * Récupère tous les royaumes enregistrés dans la base de données.
     *
     * @return Une liste de tous les royaumes
     */
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

    /**
     * Récupère tous les royaumes d'un joueur spécifique.
     *
     * @param joueurId L'identifiant du joueur
     * @return Une liste des royaumes du joueur
     */
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

    /**
     * Met à jour les informations d'un royaume existant.
     *
     * @param royaume Le royaume avec les nouvelles informations
     */
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

    /**
     * Supprime un royaume de la base de données.
     *
     * @param id L'identifiant du royaume à supprimer
     */
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

    /**
     * Extrait les données d'un royaume à partir d'un ResultSet.
     *
     * @param resultSet Le ResultSet contenant les données du royaume
     * @return Un objet Royaume créé à partir des données du ResultSet
     * @throws SQLException Si une erreur survient lors de l'extraction des données
     */
    private Royaume extraireRoyaumeDeResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String nom = resultSet.getString("nom");
        int niveau = resultSet.getInt("niveau");

        return new Royaume(id, nom, niveau);
    }
}
