package be.helha.projects.GuerreDesRoyaumes.DAOImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.PersonnageDAO;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.DatabaseException;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Guerrier;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Golem;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Titan;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Voleur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;

@Repository
public class PersonnageDAOImpl implements PersonnageDAO {

    private Connection connection;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        try {
            this.connection = dataSource.getConnection();
            // Créer la table si elle n'existe pas
            creerTablePersonnageSiInexistante();
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la connexion à la base de données", e);
        }
    }

    /**
     * Crée la table des personnages si elle n'existe pas déjà
     */
    private void creerTablePersonnageSiInexistante() {
        String createTableQuery = """
        IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='personnage' AND xtype='U')
        BEGIN
            CREATE TABLE personnage (
                id_personnage INT PRIMARY KEY IDENTITY(1,1),
                nom_personnage NVARCHAR(255) NOT NULL,
                vie_personnage FLOAT NOT NULL,
                degats_personnage FLOAT NOT NULL,
                resistance_personnage FLOAT NOT NULL,
                type_personnage NVARCHAR(50) NOT NULL
            )
        END
        """;

        try (PreparedStatement statement = connection.prepareStatement(createTableQuery)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la création de la table personnage", e);
        }
    }

    @Override
    public void ajouterPersonnage(Personnage personnage) {
        String sql = "INSERT INTO personnage (nom_personnage, vie_personnage, degats_personnage, resistance_personnage) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, personnage.getNom());
            statement.setDouble(2, personnage.getVie());
            statement.setDouble(3, personnage.getDegats());
            statement.setDouble(4, personnage.getDefense());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                // Stocker l'ID généré dans l'objet personnage
                // Cela nécessiterait d'ajouter un champ id et un setter dans la classe Personnage
                // personnage.setId(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Personnage obtenirPersonnageParId(int id) {
        String sql = "SELECT * FROM personnage WHERE id_personnage = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return extrairePersonnageDeResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Personnage> obtenirTousLesPersonnages() {
        List<Personnage> personnages = new ArrayList<>();
        String sql = "SELECT * FROM personnage";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                personnages.add(extrairePersonnageDeResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return personnages;
    }

    @Override
    public void mettreAJourPersonnage(Personnage personnage) {
        String sql = "UPDATE personnage SET nom_personnage = ?, vie_personnage = ?, degats_personnage = ?, resistance_personnage = ? WHERE id_personnage = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, personnage.getNom());
            statement.setDouble(2, personnage.getVie());
            statement.setDouble(3, personnage.getDegats());
            statement.setDouble(4, personnage.getDefense());
            statement.setString(5, determinerTypePersonnage(personnage));
            // Nécessiterait un getter getId() dans la classe Personnage
            // statement.setInt(6, personnage.getId());
            statement.setInt(6, 0); // Temporaire, à remplacer par l'ID réel
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimerPersonnage(int id) {
        String sql = "DELETE FROM personnage WHERE id_personnage = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String determinerTypePersonnage(Personnage personnage) {
        if (personnage instanceof Guerrier) {
            return "guerrier";
        } else if (personnage instanceof Golem) {
            return "golem";
        } else if (personnage instanceof Titan) {
            return "Titan";
        } else if (personnage instanceof Voleur) {
            return "Voleur";
        }
        return "personnage";
    }

    private Personnage extrairePersonnageDeResultSet(ResultSet resultSet) throws SQLException {
        String nom = resultSet.getString("nom_personnage");
        int vie = resultSet.getInt("vie_personnage");
        int degats = resultSet.getInt("degats_personnage");
        int resistance = resultSet.getInt("resistance_personnage");
        String type = resultSet.getString("type_personnage");

        // Créer le bon type de personnage selon la valeur de la colonne "type"
        switch (type.toLowerCase()) {
            case "guerrier":
                return new Guerrier();
            case "golem":
                return new Golem();
            case "titan":
                return new Titan();
            case "voleur":
                return new Voleur();
            default:
                // Cas par défaut - cela dépendra de votre implémentation
                throw new SQLException("Type de personnage inconnu: " + type);
        }
    }
}
