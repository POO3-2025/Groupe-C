package be.helha.projects.GuerreDesRoyaumes.DAOImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;
import be.helha.projects.GuerreDesRoyaumes.Model.Royaume;

import java.io.ObjectInputFilter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JoueurDAOImpl implements JoueurDAO {

    private static JoueurDAOImpl instance;
    private Connection connection;
    private String tableName = "guerre_des_royaumes"; // Changer à "guerre_des_royaumes" pour la production et guerre_des_royaumes_test pour la table de test

    public JoueurDAOImpl() {
    }

    @Override
    public int getNextJoueurID() throws SQLException {
        String query = "SELECT MAX(id) FROM " + tableName;
        if (connection == null || connection.isClosed()) {
            throw new SQLException("La connexion à la base de données est fermée ou inexistante !");
        }

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getInt(1) + 1;
            } else {
                return 1; // Si aucun joueur n'existe, l'ID suivant est 1
            }
        }
    }

    @Override
    public boolean authentifierJoueur(String pseudo, String motDePasse) {
        String sql = "SELECT * FROM joueurs WHERE pseudo = ? AND motDePasse = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, pseudo);
            statement.setString(2, motDePasse);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next(); // Retourne true si un joueur correspondant est trouvé
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void creerJoueuroSiInexistante() {
        String createTableQuery = """
        IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='joueur' AND xtype='U')
        CREATE TABLE Joueur (
            id INT PRIMARY KEY, 
            nom String NOT NULL,
            prenom String NOT NULL,
            pseudo String NOT NULL,
            motDePasse String NOT NULL
        )
         """;

        try (PreparedStatement statement = connection.prepareStatement(createTableQuery)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la création de la table moto");
        }
    }

    // Create
    @Override
    public void ajouterJoueur(Joueur joueur) {
        String sql = "INSERT INTO joueurs (nom, prenom, pseudo, motDePasse, argent) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, joueur.getNom());
            statement.setString(2, joueur.getPrenom());
            statement.setString(3, joueur.getPseudo());
            statement.setString(5, joueur.getMotDePasse());
            statement.setInt(6, joueur.getArgent());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                joueur.setId(generatedKeys.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Read
    @Override
    public Joueur obtenirJoueurParId(int id) {
        String sql = "SELECT * FROM joueurs WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return extraireJoueurDeResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Joueur obtenirJoueurParPseudo(String pseudo) {
        String sql = "SELECT * FROM joueurs WHERE pseudo = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, pseudo);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return extraireJoueurDeResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Joueur> obtenirTousLesJoueurs() {
        List<Joueur> joueurs = new ArrayList<>();
        String sql = "SELECT * FROM joueurs";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                joueurs.add(extraireJoueurDeResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return joueurs;
    }

    // Update
    @Override
    public void mettreAJourJoueur(Joueur joueur) {
        String sql = "UPDATE joueurs SET nom = ?, prenom = ?, pseudo = ?, motDePasse = ?, argent = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, joueur.getNom());
            statement.setString(2, joueur.getPrenom());
            statement.setString(3, joueur.getPseudo());
            statement.setString(5, joueur.getMotDePasse());
            statement.setInt(6, joueur.getArgent());
            statement.setInt(7, joueur.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete
    @Override
    public void supprimerJoueur(int id) {
        String sql = "DELETE FROM joueurs WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public synchronized static JoueurDAOImpl getInstance() {
        if (instance == null) {
            instance = new JoueurDAOImpl();
        }
        return instance;
    }

    private Joueur extraireJoueurDeResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String nom = resultSet.getString("nom");
        String prenom = resultSet.getString("prenom");
        String pseudo = resultSet.getString("pseudo");
        String motDePasse = resultSet.getString("motDePasse");
        int argent = resultSet.getInt("argent");

        // Ces objets seraient normalement récupérés via leurs propres DAOs
        Royaume royaume = null; // À compléter avec le DAO du royaume
        Personnage personnage = null; // À compléter avec le DAO du personnage
        Inventaire inventaire = null; // À compléter avec le DAO de l'inventaire

        return new Joueur(id, nom, prenom, pseudo, motDePasse, argent, royaume, personnage, inventaire);
    }
}




