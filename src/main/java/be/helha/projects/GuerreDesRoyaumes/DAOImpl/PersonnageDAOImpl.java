package be.helha.projects.GuerreDesRoyaumes.DAOImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.PersonnageDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Guerrier;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Golem;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Titan;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Voleur;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonnageDAOImpl implements PersonnageDAO {

    private Connection connection;

    public PersonnageDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void ajouterPersonnage(Personnage personnage) {
        String sql = "INSERT INTO personnages (nom, vie, degats, resistance, type) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, personnage.getNom());
            statement.setDouble(2, personnage.getVie());
            statement.setDouble(3, (int) personnage.getDegats());
            statement.setInt(4, personnage.getResistance());
            statement.setString(5, determinerTypePersonnage(personnage));
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
        String sql = "SELECT * FROM personnages WHERE id = ?";
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
        String sql = "SELECT * FROM personnages";
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
        String sql = "UPDATE personnages SET nom = ?, vie = ?, degats = ?, resistance = ?, type = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, personnage.getNom());
            statement.setDouble(2, personnage.getVie());
            statement.setDouble(3, personnage.getDegats());
            statement.setInt(4, personnage.getResistance());
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
        String sql = "DELETE FROM personnages WHERE id = ?";
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
        String nom = resultSet.getString("nom");
        int vie = resultSet.getInt("vie");
        int degats = resultSet.getInt("degats");
        int resistance = resultSet.getInt("resistance");
        String type = resultSet.getString("type");

        // Créer le bon type de personnage selon la valeur de la colonne "type"
        switch (type.toLowerCase()) {
            case "guerrier":
                return new Guerrier(nom, vie, degats, resistance);
            case "golem":
                return new Golem(nom, vie, degats, resistance);
            case "titan":
                return new Titan(nom, vie, degats, resistance);
            case "voleur":
                return new Voleur(nom, vie, degats, resistance);
            default:
                // Cas par défaut - cela dépendra de votre implémentation
                throw new SQLException("Type de personnage inconnu: " + type);
        }
    }
}
