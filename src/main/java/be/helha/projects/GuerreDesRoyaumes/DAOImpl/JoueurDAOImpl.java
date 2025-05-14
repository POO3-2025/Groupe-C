package be.helha.projects.GuerreDesRoyaumes.DAOImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.DatabaseException;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.JoueurNotFoundException;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.AuthentificationException;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Coffre;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Golem;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Guerrier;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Voleur;
import be.helha.projects.GuerreDesRoyaumes.Model.Royaume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.ObjectInputFilter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JoueurDAOImpl implements JoueurDAO {

    private static JoueurDAOImpl instance;
    private Connection connection;
    private String tableName = "joueur"; // Table principale

    public JoueurDAOImpl() {
    }

    /**
     * Définit une connexion SQL externe pour ce DAO.
     * Cette méthode est utilisée lorsque la connexion est gérée manuellement en dehors du contexte Spring.
     * @param connection La connexion SQL à utiliser
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
        try {
            // Créer la table joueur si elle n'existe pas
            creerTableJoueurSiInexistante();
        } catch (Exception e) {
            throw new DatabaseException("Erreur lors de la création de la table joueur", e);
        }
    }

    @Autowired
    public void setDataSource(DataSource dataSource) {
        try {
            this.connection = dataSource.getConnection();
            // Créer la table joueur si elle n'existe pas
            creerTableJoueurSiInexistante();
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la connexion à la base de données", e);
        }
    }

    @Override
    public int getNextJoueurID() {
        String query = "SELECT MAX(id_joueur) FROM " + tableName;
        try {
            if (connection == null || connection.isClosed()) {
                throw new DatabaseException("La connexion à la base de données est fermée ou inexistante !");
            }

            try (PreparedStatement statement = connection.prepareStatement(query);
                 ResultSet resultSet = statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt(1) + 1;
                } else {
                    return 1; // Si aucun joueur n'existe, l'ID suivant est 1
                }
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la récupération du prochain ID de joueur", e);
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
            throw new AuthentificationException("Erreur lors de la vérification des identifiants", e);
        }
    }

    private void creerTableJoueurSiInexistante() {
        // Créer la table joueur
        String createTableJoueur = """
        IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='joueur' AND xtype='U')
        BEGIN
            CREATE TABLE joueur (
                id_joueur INT PRIMARY KEY IDENTITY(1,1),
                nom_joueur NVARCHAR(255) NOT NULL,
                prenom_joueur NVARCHAR(255) NOT NULL,
                pseudo_joueur NVARCHAR(255) NOT NULL,
                motDePasse_joueur NVARCHAR(255) NOT NULL,
                argent_joueur INT NOT NULL DEFAULT 100,
                victoires_joueur INT NOT NULL DEFAULT 0,
                defaites_joueur INT NOT NULL DEFAULT 0
            )
        END
        """;

        // Créer la table royaume
        String createTableRoyaume = """
        IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='royaume' AND xtype='U')
        BEGIN
            CREATE TABLE royaume (
                id_royaume INT PRIMARY KEY IDENTITY(1,1),
                nom_royaume NVARCHAR(255) NOT NULL,
                niveau_royaume INT NOT NULL DEFAULT 1,
                id_joueur INT NOT NULL,
                FOREIGN KEY (id_joueur) REFERENCES joueur(id_joueur)
            )
        END
        """;

        // Créer la table personnage_joueur
        String createTablePersonnage = """
        IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='personnage_joueur' AND xtype='U')
        BEGIN
            CREATE TABLE personnage_joueur (
                id_personnage INT PRIMARY KEY IDENTITY(1,1),
                type_personnage NVARCHAR(50) NOT NULL,
                id_joueur INT NOT NULL,
                FOREIGN KEY (id_joueur) REFERENCES joueur(id_joueur)
            )
        END
        """;

        try {
            // Exécuter les créations de tables
            try (PreparedStatement stmtJoueur = connection.prepareStatement(createTableJoueur);
                 PreparedStatement stmtRoyaume = connection.prepareStatement(createTableRoyaume);
                 PreparedStatement stmtPersonnage = connection.prepareStatement(createTablePersonnage)) {
                
                stmtJoueur.executeUpdate();
                stmtRoyaume.executeUpdate();
                stmtPersonnage.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la création des tables : " + e.getMessage());
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
            throw new DatabaseException("Erreur lors de l'ajout du joueur: " + joueur.getPseudo(), e);
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
            } else {
                throw new JoueurNotFoundException(id);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la récupération du joueur avec l'ID: " + id, e);
        }
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
            throw new DatabaseException("Erreur lors de la récupération du joueur avec le pseudo: " + pseudo, e);
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
            throw new DatabaseException("Erreur lors de la récupération de tous les joueurs", e);
        }
        return joueurs;
    }

    // Update
    @Override
    public void mettreAJourJoueur(Joueur joueur) {
        // Mettre à jour les informations de base du joueur
        String sqlJoueur = "UPDATE joueur SET nom_joueur = ?, prenom_joueur = ?, pseudo_joueur = ?, motDePasse_joueur = ?, argent_joueur = ?, victoires_joueur = ?, defaites_joueur = ? WHERE id_joueur = ?";
        
        // Vérifier si le royaume existe
        String sqlCheckRoyaume = "SELECT COUNT(*) FROM royaume WHERE id_joueur = ?";
        String sqlInsertRoyaume = "INSERT INTO royaume (nom_royaume, niveau_royaume, id_joueur) VALUES (?, ?, ?)";
        String sqlUpdateRoyaume = "UPDATE royaume SET nom_royaume = ?, niveau_royaume = ? WHERE id_joueur = ?";
        
        // Vérifier si le personnage existe
        String sqlCheckPersonnage = "SELECT COUNT(*) FROM personnage_joueur WHERE id_joueur = ?";
        String sqlInsertPersonnage = "INSERT INTO personnage_joueur (type_personnage, id_joueur) VALUES (?, ?)";
        String sqlUpdatePersonnage = "UPDATE personnage_joueur SET type_personnage = ? WHERE id_joueur = ?";

        try {
            connection.setAutoCommit(false); // Démarrer une transaction

            // Mise à jour du joueur
            try (PreparedStatement stmtJoueur = connection.prepareStatement(sqlJoueur)) {
                stmtJoueur.setString(1, joueur.getNom());
                stmtJoueur.setString(2, joueur.getPrenom());
                stmtJoueur.setString(3, joueur.getPseudo());
                stmtJoueur.setString(4, joueur.getMotDePasse());
                stmtJoueur.setInt(5, joueur.getArgent());
                stmtJoueur.setInt(6, joueur.getVictoires());
                stmtJoueur.setInt(7, joueur.getDefaites());
                stmtJoueur.setInt(8, joueur.getId());
                stmtJoueur.executeUpdate();
            }

            // Gestion du royaume
            if (joueur.getRoyaume() != null) {
                // Vérifier si le royaume existe déjà
                boolean royaumeExiste = false;
                try (PreparedStatement stmtCheck = connection.prepareStatement(sqlCheckRoyaume)) {
                    stmtCheck.setInt(1, joueur.getId());
                    ResultSet rs = stmtCheck.executeQuery();
                    if (rs.next()) {
                        royaumeExiste = rs.getInt(1) > 0;
                    }
                }

                if (royaumeExiste) {
                    // Mise à jour du royaume existant
                    try (PreparedStatement stmtRoyaume = connection.prepareStatement(sqlUpdateRoyaume)) {
                        stmtRoyaume.setString(1, joueur.getRoyaume().getNom());
                        stmtRoyaume.setInt(2, joueur.getRoyaume().getNiveau());
                        stmtRoyaume.setInt(3, joueur.getId());
                        stmtRoyaume.executeUpdate();
                    }
                } else {
                    // Insertion d'un nouveau royaume
                    try (PreparedStatement stmtRoyaume = connection.prepareStatement(sqlInsertRoyaume)) {
                        stmtRoyaume.setString(1, joueur.getRoyaume().getNom());
                        stmtRoyaume.setInt(2, joueur.getRoyaume().getNiveau());
                        stmtRoyaume.setInt(3, joueur.getId());
                        stmtRoyaume.executeUpdate();
                    }
                }
            }

            // Gestion du personnage
            if (joueur.getPersonnage() != null) {
                // Vérifier si le personnage existe déjà
                boolean personnageExiste = false;
                try (PreparedStatement stmtCheck = connection.prepareStatement(sqlCheckPersonnage)) {
                    stmtCheck.setInt(1, joueur.getId());
                    ResultSet rs = stmtCheck.executeQuery();
                    if (rs.next()) {
                        personnageExiste = rs.getInt(1) > 0;
                    }
                }

                if (personnageExiste) {
                    // Mise à jour du personnage existant
                    try (PreparedStatement stmtPersonnage = connection.prepareStatement(sqlUpdatePersonnage)) {
                        stmtPersonnage.setString(1, joueur.getPersonnage().getClass().getSimpleName().toLowerCase());
                        stmtPersonnage.setInt(2, joueur.getId());
                        stmtPersonnage.executeUpdate();
                    }
                } else {
                    // Insertion d'un nouveau personnage
                    try (PreparedStatement stmtPersonnage = connection.prepareStatement(sqlInsertPersonnage)) {
                        stmtPersonnage.setString(1, joueur.getPersonnage().getClass().getSimpleName().toLowerCase());
                        stmtPersonnage.setInt(2, joueur.getId());
                        stmtPersonnage.executeUpdate();
                    }
                }
            }

            connection.commit(); // Valider la transaction
        } catch (SQLException e) {
            try {
                connection.rollback(); // Annuler la transaction en cas d'erreur
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la mise à jour du joueur : " + e.getMessage());
        } finally {
            try {
                connection.setAutoCommit(true); // Réactiver l'auto-commit
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Delete
    @Override
    public void supprimerJoueur(int id) {
        String sql = "DELETE FROM " + tableName + " WHERE id_joueur = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected == 0) {
                throw new JoueurNotFoundException(id);
            }
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la suppression du joueur avec l'ID: " + id, e);
        }
    }
}




