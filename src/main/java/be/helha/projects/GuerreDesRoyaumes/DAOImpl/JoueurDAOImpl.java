package be.helha.projects.GuerreDesRoyaumes.DAOImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.Config.InitialiserAPP;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.DatabaseException;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.JoueurNotFoundException;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.SQLConnectionException;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Coffre;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Guerrier;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;
import be.helha.projects.GuerreDesRoyaumes.Model.Royaume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class JoueurDAOImpl implements JoueurDAO {

    private static JoueurDAOImpl instance;
    private Connection connection;
    private String tableName = "joueur"; // Table principale

    public JoueurDAOImpl() {
        // Constructeur par défaut
        try {
            connection = InitialiserAPP.getSQLConnexion();
            creerTableJoueurSiInexistante();
        } catch (SQLConnectionException ex) {
            throw new RuntimeException(ex);
        }
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

    public synchronized static JoueurDAOImpl getInstance() {
        if (instance == null) {
            instance = new JoueurDAOImpl();
        }
        return instance;
    }

    private Joueur extraireJoueurDeResultSet(ResultSet resultSet) throws SQLException {
        try {
            System.out.println("DEBUG: Début extraction des données du joueur du ResultSet");

            int id = resultSet.getInt("id_joueur");
            System.out.println("DEBUG: ID joueur: " + id);

            String nom = resultSet.getString("nom_joueur");
            System.out.println("DEBUG: Nom joueur: " + nom);

            String prenom = resultSet.getString("prenom_joueur");
            System.out.println("DEBUG: Prénom joueur: " + prenom);

            String pseudo = resultSet.getString("pseudo_joueur");
            System.out.println("DEBUG: Pseudo joueur: " + pseudo);

            String motDePasse = resultSet.getString("motDePasse_joueur");
            System.out.println("DEBUG: Mot de passe récupéré");

            int argent = resultSet.getInt("argent_joueur");
            System.out.println("DEBUG: Argent joueur: " + argent);

            // Pour les victoires et les défaites, vérifier si les colonnes existent
            int victoires = 0;
            int defaites = 0;

            try {
                victoires = resultSet.getInt("victoires_joueur");
                System.out.println("DEBUG: Victoires joueur: " + victoires);
            } catch (SQLException e) {
                System.out.println("DEBUG: Colonne victoires_joueur n'existe pas, utilisation de la valeur par défaut 0");
            }

            try {
                defaites = resultSet.getInt("defaites_joueur");
                System.out.println("DEBUG: Défaites joueur: " + defaites);
            } catch (SQLException e) {
                System.out.println("DEBUG: Colonne defaites_joueur n'existe pas, utilisation de la valeur par défaut 0");
            }

            // Créer le coffre par défaut
            System.out.println("DEBUG: Création du coffre par défaut");
            Coffre coffre = new Coffre();

            // Création du personnage si id_personnage existe
            Personnage personnage = null;

            try {
                // Vérifier si la colonne id_personnage existe
                if (verifierExistenceColonne(tableName, "id_personnage")) {
                    int personnageId = resultSet.getInt("id_personnage");
                    if (personnageId > 0 && !resultSet.wasNull()) {
                        System.out.println("DEBUG: ID personnage trouvé: " + personnageId);
                        // Décider quel type de personnage créer
                        // Cette logique est simplifiée - dans un vrai système vous auriez une table personnage
                        // Pour l'instant, on crée simplement un guerrier par défaut
                        personnage = new Guerrier();
                        System.out.println("DEBUG: Personnage créé (Guerrier par défaut)");
                    }
                } else {
                    System.out.println("DEBUG: Pas de colonne id_personnage dans la table joueur");
                }
            } catch (Exception e) {
                System.out.println("DEBUG: Erreur lors de la récupération du personnage: " + e.getMessage());
            }

            System.out.println("DEBUG: Pas de récupération du royaume (sera géré par RoyaumeDAO)");
            Royaume royaume = null;

            System.out.println("DEBUG: Création de l'objet Joueur");
            return new Joueur(id, nom, prenom, pseudo, motDePasse, argent, royaume, personnage, coffre, victoires, defaites);

        } catch (SQLException e) {
            System.err.println("ERREUR SQL lors de l'extraction des données du joueur: " + e.getMessage());
            e.printStackTrace();
            throw e;
        } catch (Exception e) {
            System.err.println("ERREUR générale lors de l'extraction des données du joueur: " + e.getMessage());
            e.printStackTrace();
            throw new SQLException("Erreur lors de l'extraction des données du joueur", e);
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
        try {
            if (connection == null || connection.isClosed()) {
                System.err.println("Attention : La connexion à la base de données est fermée ou inexistante !");
                return false;
            }

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, pseudo);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    // Récupérer le mot de passe haché de la base de données
                    String motDePasseHache = resultSet.getString("motDePasse_joueur");

                    // Vérifier si le mot de passe saisi correspond au hash stocké
                    try {
                        return org.springframework.security.crypto.bcrypt.BCrypt.checkpw(motDePasse, motDePasseHache);
                    } catch (Exception e) {
                        System.err.println("Erreur lors de la vérification du mot de passe: " + e.getMessage());
                        e.printStackTrace();
                        return false;
                    }
                }
                return false; // Aucun utilisateur trouvé avec ce pseudo
            }
        } catch (SQLException e) {
            System.err.println("Erreur SQL lors de la vérification des identifiants: " + e.getMessage());
            e.printStackTrace();
            return false; // En cas d'erreur SQL, retourner false au lieu de lancer une exception
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
                defaites_joueur INT NOT NULL DEFAULT 0,
                actif_joueur BIT NOT NULL DEFAULT 0
            )
        END
        """;

        try {
            // Exécuter la création de la table joueur
            try (PreparedStatement stmt = connection.prepareStatement(createTableJoueur)) {
                stmt.executeUpdate();
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
        System.out.println("DEBUG: Tentative de récupération du joueur avec pseudo: " + pseudo);

        if (connection == null) {
            System.err.println("ERREUR: Connection SQL est null");
            return null;
        }

        try {
            if (connection.isClosed()) {
                System.err.println("ERREUR: Connection SQL est fermée");
                return null;
            }
        } catch (SQLException e) {
            System.err.println("ERREUR: Impossible de vérifier si la connection SQL est fermée: " + e.getMessage());
            return null;
        }

        String sql = "SELECT * FROM " + tableName + " WHERE pseudo_joueur = ?";
        System.out.println("DEBUG: Requête SQL: " + sql + " avec paramètre: " + pseudo);

        try {
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, pseudo);

            System.out.println("DEBUG: Exécution de la requête");
            ResultSet resultSet = statement.executeQuery();

            System.out.println("DEBUG: Requête exécutée, analyse des résultats");
            if (resultSet.next()) {
                System.out.println("DEBUG: Joueur trouvé dans la base de données");

                try {
                    Joueur joueur = extraireJoueurDeResultSet(resultSet);
                    System.out.println("DEBUG: Joueur extrait avec succès: " + joueur.getPseudo());
                    return joueur;
                } catch (Exception e) {
                    System.err.println("ERREUR lors de l'extraction du joueur: " + e.getMessage());
                    e.printStackTrace();
                    // Essayons une approche plus simple
                    return creerJoueurSimple(resultSet);
                }
            } else {
                System.out.println("DEBUG: Aucun joueur trouvé avec le pseudo: " + pseudo);
                return null;
            }
        } catch (SQLException e) {
            System.err.println("ERREUR SQL: " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.err.println("ERREUR GÉNÉRALE: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private Joueur creerJoueurSimple(ResultSet rs) {
        try {
            int id = rs.getInt("id_joueur");
            String nom = rs.getString("nom_joueur");
            String prenom = rs.getString("prenom_joueur");
            String pseudo = rs.getString("pseudo_joueur");
            String motDePasse = rs.getString("motDePasse_joueur");
            int argent = rs.getInt("argent_joueur");

            // Pour les victoires et les défaites, utiliser des valeurs par défaut
            int victoires = 0;
            int defaites = 0;

            // Créer le coffre
            Coffre coffre = new Coffre();
            System.out.println("DEBUG: Création d'un joueur simple: " + pseudo);

            // Essayer de récupérer le personnage si possible
            Personnage personnage = null;
            try {
                if (verifierExistenceColonne(tableName, "id_personnage")) {
                    int personnageId = rs.getInt("id_personnage");
                    if (personnageId > 0 && !rs.wasNull()) {
                        // Nous créons un Guerrier par défaut
                        personnage = new Guerrier();
                        System.out.println("DEBUG: Personnage créé pour joueur simple: Guerrier");
                    }
                }
            } catch (Exception e) {
                System.out.println("DEBUG: Pas de personnage pour le joueur simple");
            }

            return new Joueur(id, nom, prenom, pseudo, motDePasse, argent, null, personnage, coffre, victoires, defaites);
        } catch (SQLException e) {
            System.err.println("ERREUR création joueur simple: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
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

    @Override
    public List<Joueur> obtenirJoueursActifs() {
        List<Joueur> joueursActifs = new ArrayList<>();
        // Vérifier si la colonne actif_joueur existe
        boolean colonneExiste = verifierExistenceColonne(tableName, "actif_joueur");
        if (!colonneExiste) {
            System.out.println("DEBUG: La colonne actif_joueur n'existe pas encore. Retour de tous les joueurs.");
            return obtenirTousLesJoueurs();
        }

        try {
            String sql = "SELECT * FROM " + tableName + " WHERE actif_joueur = 1";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    joueursActifs.add(extraireJoueurDeResultSet(resultSet));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des joueurs actifs: " + e.getMessage());
            // En cas d'erreur, retourner tous les joueurs pour ne pas bloquer la sélection
            return obtenirTousLesJoueurs();
        }

        // Si aucun joueur actif n'est trouvé, retourner tous les joueurs
        if (joueursActifs.isEmpty()) {
            System.out.println("DEBUG: Aucun joueur actif trouvé. Retour de tous les joueurs.");
            return obtenirTousLesJoueurs();
        }

        return joueursActifs;
    }

    @Override
    public boolean estJoueurActif(int id) {
        if (!verifierExistenceColonne(tableName, "actif_joueur")) {
            System.out.println("DEBUG: Colonne actif_joueur absente. Retour true par défaut.");
            return true;
        }

        try {
            String sql = "SELECT actif_joueur FROM " + tableName + " WHERE id_joueur = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, id);
                ResultSet rs = statement.executeQuery();
                return rs.next() && rs.getInt(1) == 1;
            }
        } catch (SQLException e) {
            System.err.println("Erreur vérification statut actif pour ID " + id + ": " + e.getMessage());
            return false;
        }
    }

    // Update
    @Override
    public void mettreAJourJoueur(Joueur joueur) {
        // Version améliorée qui met à jour les colonnes existantes et l'association avec le personnage
        String sqlJoueur = "UPDATE " + tableName + " SET nom_joueur = ?, prenom_joueur = ?, pseudo_joueur = ?, motDePasse_joueur = ?, argent_joueur = ? WHERE id_joueur = ?";

        try {
            // Mise à jour du joueur
            try (PreparedStatement stmtJoueur = connection.prepareStatement(sqlJoueur)) {
                stmtJoueur.setString(1, joueur.getNom());
                stmtJoueur.setString(2, joueur.getPrenom());
                stmtJoueur.setString(3, joueur.getPseudo());
                stmtJoueur.setString(4, joueur.getMotDePasse());
                stmtJoueur.setInt(5, joueur.getArgent());
                stmtJoueur.setInt(6, joueur.getId());
                stmtJoueur.executeUpdate();
                System.out.println("Joueur mis à jour avec succès, nouvel argent: " + joueur.getArgent());
            }

            // Mise à jour additionnelle: sauvegarde de l'association avec le personnage
            if (joueur.getPersonnage() != null) {
                // Vérifier si la colonne id_personnage existe
                boolean colonnePersonnageExiste = verifierExistenceColonne(tableName, "id_personnage");

                if (!colonnePersonnageExiste) {
                    // Créer la colonne si elle n'existe pas
                    try (PreparedStatement stmtAlter = connection.prepareStatement(
                            "ALTER TABLE " + tableName + " ADD id_personnage INT NULL")) {
                        stmtAlter.executeUpdate();
                    }
                }

                // Sauvegarder l'association (temporairement en utilisant le hashCode du personnage comme ID)
                int personnageId = joueur.getPersonnage().hashCode();
                try (PreparedStatement stmtPersonnage = connection.prepareStatement(
                        "UPDATE " + tableName + " SET id_personnage = ? WHERE id_joueur =?")) {
                    stmtPersonnage.setInt(1, personnageId);
                    stmtPersonnage.setInt(2, joueur.getId());
                    stmtPersonnage.executeUpdate();
                    System.out.println("Association joueur-personnage mise à jour: Personnage ID=" + personnageId);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la mise à jour du joueur : " + e.getMessage());
        }
    }

    @Override
    public void definirStatutConnexion(int id, boolean estActif) {
        // Vérifier si la colonne actif_joueur existe
        boolean colonneExiste = verifierExistenceColonne(tableName, "actif_joueur");
        if (!colonneExiste) {
            // Créer la colonne si elle n'existe pas
            try (PreparedStatement stmtAlter = connection.prepareStatement(
                    "ALTER TABLE " + tableName + " ADD actif_joueur BIT NOT NULL DEFAULT 0")) {
                stmtAlter.executeUpdate();
                System.out.println("DEBUG: Colonne actif_joueur ajoutée à la table " + tableName);
            } catch (SQLException e) {
                throw new DatabaseException("Erreur lors de l'ajout de la colonne actif_joueur", e);
            }
        }

        // Mettre à jour le statut de connexion
        String sql = "UPDATE " + tableName + " SET actif_joueur = ? WHERE id_joueur = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setBoolean(1, estActif);
            statement.setInt(2, id);
            int rowsAffected = statement.executeUpdate();
            if (rowsAffected == 0) {
                throw new JoueurNotFoundException(id);
            }
            System.out.println("DEBUG: Statut de connexion du joueur ID=" + id + " mis à jour: " + (estActif ? "connecté" : "déconnecté"));
        } catch (SQLException e) {
            throw new DatabaseException("Erreur lors de la mise à jour du statut de connexion du joueur", e);
        }
    }

    /**
     * Vérifie si une colonne existe dans une table
     * @param table Nom de la table
     * @param colonne Nom de la colonne
     * @return true si la colonne existe, false sinon
     */
    private boolean verifierExistenceColonne(String table, String colonne) {
        try {
            PreparedStatement stmt = connection.prepareStatement(
                    "SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME = ? AND COLUMN_NAME = ?");
            stmt.setString(1, table);
            stmt.setString(2, colonne);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
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
