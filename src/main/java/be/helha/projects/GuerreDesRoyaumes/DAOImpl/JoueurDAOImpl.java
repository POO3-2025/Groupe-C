package be.helha.projects.GuerreDesRoyaumes.DAOImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Coffre;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;
import be.helha.projects.GuerreDesRoyaumes.Model.Royaume;

import java.io.ObjectInputFilter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class JoueurDAOImpl implements JoueurDAO {

    private static JoueurDAOImpl instance;
    private Connection connection;
    private String tableName = "joueur"; // Table principale

    public JoueurDAOImpl() {
    }

    /**
     * Définit la connexion à la base de données
     * @param connection La connexion à utiliser
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
        // Créer la table joueur si elle n'existe pas
        creerTableJoueurSiInexistante();
    }

    @Override
    public int getNextJoueurID() throws SQLException {
        String query = "SELECT MAX(id_joueur) FROM " + tableName;
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

    /**
     * Vérifie si un joueur avec le pseudo et mot de passe spécifiés existe dans la base de données.
     * Cette méthode est utilisée par ServiceAuthentificationImpl.
     * @param pseudo Le pseudo du joueur
     * @param motDePasse Le mot de passe du joueur
     * @return true si le joueur existe avec ces identifiants, false sinon
     */
    public boolean verifierIdentifiants(String pseudo, String motDePasse) {
        String sql = "SELECT * FROM " + tableName + " WHERE pseudo_joueur = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, pseudo);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                // Récupérer le mot de passe haché de la base de données
                String motDePasseHache = resultSet.getString("motDePasse_joueur");

                // Vérifier si le mot de passe saisi correspond au hash stocké
                return org.springframework.security.crypto.bcrypt.BCrypt.checkpw(motDePasse, motDePasseHache);
            }
            return false; // Aucun utilisateur trouvé avec ce pseudo
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void creerTableJoueurSiInexistante() {
        String createTableQuery = """
        IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='joueur' AND xtype='U')
        BEGIN
            CREATE TABLE joueur (
                id_joueur INT PRIMARY KEY IDENTITY(1,1),
                nom_joueur NVARCHAR(255) NOT NULL,
                prenom_joueur NVARCHAR(255) NOT NULL,
                pseudo_joueur NVARCHAR(255) NOT NULL,
                motDePasse_joueur NVARCHAR(255) NOT NULL,
                argent_joueur INT NOT NULL
            )
        END
        """;

        try (PreparedStatement statement = connection.prepareStatement(createTableQuery)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la création de la table joueur: " + e.getMessage());
        }
    }

    // Create
    @Override
    public void ajouterJoueur(Joueur joueur) {
        String sql = "INSERT INTO " + tableName + " (nom_joueur, prenom_joueur, pseudo_joueur, motDePasse_joueur, argent_joueur) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, joueur.getNom());
            statement.setString(2, joueur.getPrenom());
            statement.setString(3, joueur.getPseudo());
            statement.setString(4, joueur.getMotDePasse());
            statement.setInt(5, joueur.getArgent());
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
        String sql = "SELECT * FROM " + tableName + " WHERE id_joueur = ?";
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
        String sql = "SELECT * FROM " + tableName + " WHERE pseudo_joueur = ?";
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
        String sql = "SELECT * FROM " + tableName;
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
        String sql = "UPDATE " + tableName + " SET nom_joueur = ?, prenom_joueur = ?, pseudo_joueur = ?, motDePasse_joueur = ?, argent_joueur = ? WHERE id_joueur = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, joueur.getNom());
            statement.setString(2, joueur.getPrenom());
            statement.setString(3, joueur.getPseudo());
            statement.setString(4, joueur.getMotDePasse());
            statement.setInt(5, joueur.getArgent());
            statement.setInt(6, joueur.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete
    @Override
    public void supprimerJoueur(int id) {
        String sql = "DELETE FROM " + tableName + " WHERE id_joueur = ?";
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
        int id = resultSet.getInt("id_joueur");
        String nom = resultSet.getString("nom_joueur");
        String prenom = resultSet.getString("prenom_joueur");
        String pseudo = resultSet.getString("pseudo_joueur");
        String motDePasse = resultSet.getString("motDePasse_joueur");
        int argent = resultSet.getInt("argent_joueur");

        // Ces objets seraient normalement récupérés via leurs propres DAOs
        Royaume royaume = null; // TODO À compléter avec le DAO du royaume
        Personnage personnage = null; // TODO À compléter avec le DAO du personnage
        Coffre coffre = null; // TODO À compléter avec le DAO du coffre

        return new Joueur(id, nom, prenom, pseudo, motDePasse, argent, royaume, personnage, coffre);
    }
}




