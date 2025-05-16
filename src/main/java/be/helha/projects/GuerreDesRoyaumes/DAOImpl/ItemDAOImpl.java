package be.helha.projects.GuerreDesRoyaumes.DAOImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.ItemDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Arme;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Bouclier;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Potion;

import org.springframework.stereotype.Repository;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implémentation de l'interface ItemDAO pour la gestion des items en base de données.
 * Cette classe gère les opérations CRUD pour les entités Item et leurs sous-types (Arme, Bouclier, Potion).
 * Elle utilise un pattern Singleton pour assurer une instance unique.
 */
@Repository
public class ItemDAOImpl implements ItemDAO {

    private Connection connection;
    private static ItemDAOImpl instance;

    /**
     * Constructeur par défaut pour Spring.
     */
    public ItemDAOImpl() {
    }

    /**
     * Constructeur avec connexion à la base de données.
     *
     * @param connection La connexion à la base de données à utiliser
     */
    public ItemDAOImpl(Connection connection) {
        this.connection = connection;
        creerTablesItemsSiInexistantes();
    }

    /**
     * Configure la connexion à la base de données.
     *
     * @param connection La connexion à la base de données à utiliser
     */
    public void setConnection(Connection connection) {
        System.out.println("ItemDAOImpl: Configuration de la connexion à la base de données");
        this.connection = connection;

        if (connection != null) {
            System.out.println("ItemDAOImpl: Connexion valide, création des tables si nécessaire");
            creerTablesItemsSiInexistantes();
        } else {
            System.err.println("ItemDAOImpl: ATTENTION - La connexion est null!");
        }
    }

    /**
     * Crée les tables nécessaires pour les items si elles n'existent pas déjà.
     */
    private void creerTablesItemsSiInexistantes() {
        if (connection == null) {
            return;
        }

        // Création de la table items
        String createTableItems = """
        IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='items' AND xtype='U')
        BEGIN
            CREATE TABLE items (
                id INT PRIMARY KEY IDENTITY(1,1),
                nom NVARCHAR(255) NOT NULL,
                quantiteMax INT NOT NULL,
                type NVARCHAR(50) NOT NULL,
                prix INT NOT NULL
            )
        END
        """;

        // Création de la table armes
        String createTableArmes = """
        IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='armes' AND xtype='U')
        BEGIN
            CREATE TABLE armes (
                item_id INT PRIMARY KEY,
                degats FLOAT NOT NULL,
                FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE
            )
        END
        """;

        // Création de la table boucliers
        String createTableBoucliers = """
        IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='boucliers' AND xtype='U')
        BEGIN
            CREATE TABLE boucliers (
                item_id INT PRIMARY KEY,
                defense FLOAT NOT NULL,
                FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE
            )
        END
        """;

        // Création de la table potions
        String createTablePotions = """
        IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='potions' AND xtype='U')
        BEGIN
            CREATE TABLE potions (
                item_id INT PRIMARY KEY,
                degats FLOAT NOT NULL,
                soin FLOAT NOT NULL,
                FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE
            )
        END
        """;

        try {
            // Exécuter les créations de tables
            try (PreparedStatement stmtItems = connection.prepareStatement(createTableItems)) {
                stmtItems.executeUpdate();
                System.out.println("Table items créée ou déjà existante");
            }

            try (PreparedStatement stmtArmes = connection.prepareStatement(createTableArmes)) {
                stmtArmes.executeUpdate();
                System.out.println("Table armes créée ou déjà existante");
            }

            try (PreparedStatement stmtBoucliers = connection.prepareStatement(createTableBoucliers)) {
                stmtBoucliers.executeUpdate();
                System.out.println("Table boucliers créée ou déjà existante");
            }

            try (PreparedStatement stmtPotions = connection.prepareStatement(createTablePotions)) {
                stmtPotions.executeUpdate();
                System.out.println("Table potions créée ou déjà existante");
            }

            // Ajouter des items par défaut si la table est vide
            ajouterItemsParDefaut();

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la création des tables pour les items: " + e.getMessage());
        }
    }

    /**
     * Ajoute des items par défaut dans la boutique si aucun item n'existe.
     */
    private void ajouterItemsParDefaut() {
        try {
            // Vérifier si la table items est vide
            String countQuery = "SELECT COUNT(*) FROM items";
            int itemCount = 0;

            try (PreparedStatement stmt = connection.prepareStatement(countQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    itemCount = rs.getInt(1);
                }
            }

            // Si des items existent déjà, ne pas en ajouter d'autres
            if (itemCount > 0) {
                System.out.println("Des items existent déjà dans la base de données, pas d'initialisation nécessaire");
                return;
            }

            System.out.println("Initialisation de la boutique avec des items par défaut...");

            // Ajouter des armes par défaut
            Arme epee = new Arme("Épée en fer", 5, 10.0, 50);
            Arme hache = new Arme("Hache de bataille", 5, 15.0, 100);
            Arme lance = new Arme("Lance en acier", 3, 12.0, 75);

            // Ajouter des boucliers par défaut (nom, quantiteMax, prix, defense)
            Bouclier bouclierBois = new Bouclier("Bouclier en bois", 2, 20, 5.0);
            Bouclier bouclierAcier = new Bouclier("Bouclier en acier", 1, 100, 15.0);

            // Ajouter des potions par défaut (nom, quantiteMax, prix, degats, soin)
            Potion potionSoin = new Potion("Potion de soin", 10, 30, 0.0, 20.0);
            Potion potionForce = new Potion("Potion de force", 5, 70, 10.0, 0.0);

            // Ajouter tous les items dans la base de données
            ajouterItem(epee);
            ajouterItem(hache);
            ajouterItem(lance);
            ajouterItem(bouclierBois);
            ajouterItem(bouclierAcier);
            ajouterItem(potionSoin);
            ajouterItem(potionForce);

            System.out.println("Boutique initialisée avec succès avec des items par défaut!");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'initialisation des items par défaut: " + e.getMessage());
        }
    }

    /**
     * Retourne l'instance unique de ItemDAOImpl (pattern Singleton).
     *
     * @return L'instance unique de ItemDAOImpl
     */
    public synchronized static ItemDAOImpl getInstance() {
        if (instance == null) {
            instance = new ItemDAOImpl();
        }
        return instance;
    }

    /**
     * Ajoute un nouvel item dans la base de données.
     * Gère également l'insertion des propriétés spécifiques selon le type d'item (Arme, Bouclier, Potion).
     *
     * @param item L'item à ajouter
     * @throws RuntimeException Si la connexion n'est pas établie ou si une erreur survient lors de l'ajout
     */
    @Override
    public void ajouterItem(Item item) {
        // Si la connexion n'est pas établie, on ne peut pas ajouter d'item
        if (connection == null) {
            throw new RuntimeException("La connexion à la base de données n'est pas établie");
        }

        String sql = "INSERT INTO items (nom, quantiteMax, type, prix) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, item.getNom());
            statement.setInt(2, item.getQuantiteMax());
            statement.setString(3, obtenirTypeItem(item));
            statement.setInt(4, item.getPrix());
            statement.executeUpdate();

            ResultSet generatedKeys = statement.getGeneratedKeys();
            if (generatedKeys.next()) {
                item.setId(generatedKeys.getInt(1));
            }

            // Si c'est une arme, ajouter les propriétés spécifiques
            if (item instanceof Arme) {
                Arme arme = (Arme) item;
                String sqlArme = "INSERT INTO armes (item_id, degats) VALUES (?, ?)";
                try (PreparedStatement statementArme = connection.prepareStatement(sqlArme)) {
                    statementArme.setInt(1, arme.getId());
                    statementArme.setDouble(2, arme.getDegats());
                    statementArme.executeUpdate();
                }
            }
            // Si c'est un bouclier, ajouter les propriétés spécifiques
            else if (item instanceof Bouclier) {
                Bouclier bouclier = (Bouclier) item;
                String sqlBouclier = "INSERT INTO boucliers (item_id, defense) VALUES (?, ?)";
                try (PreparedStatement statementBouclier = connection.prepareStatement(sqlBouclier)) {
                    statementBouclier.setInt(1, bouclier.getId());
                    statementBouclier.setDouble(2, bouclier.getDefense());
                    statementBouclier.executeUpdate();
                }
            }
            // Si c'est une potion, ajouter les propriétés spécifiques
            else if (item instanceof Potion) {
                Potion potion = (Potion) item;
                String sqlPotion = "INSERT INTO potions (item_id, degats, soin) VALUES (?, ?, ?)";
                try (PreparedStatement statementPotion = connection.prepareStatement(sqlPotion)) {
                    statementPotion.setInt(1, potion.getId());
                    statementPotion.setDouble(2, potion.getDegats());
                    statementPotion.setDouble(3, potion.getSoin());
                    statementPotion.executeUpdate();
                }
            }
            // Ajouter d'autres types d'items ici
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'ajout de l'item: " + e.getMessage());
        }
    }

    /**
     * Détermine le type d'item sous forme de chaîne de caractères.
     *
     * @param item L'item dont on veut déterminer le type
     * @return Une chaîne de caractères représentant le type de l'item
     */
    private String obtenirTypeItem(Item item) {
        if (item instanceof Arme) {
            return "arme";
        }
        if (item instanceof Bouclier) {
            return "bouclier";
        }
        if (item instanceof Potion) {
            return "potion";
        }
        // Ajouter d'autres types ici
        return "item";
    }

    /**
     * Récupère un item par son identifiant.
     *
     * @param id L'identifiant de l'item à récupérer
     * @return L'item correspondant à l'identifiant ou null si aucun item n'est trouvé ou si la connexion n'est pas établie
     */
    @Override
    public Item obtenirItemParId(int id) {
        // Si la connexion n'est pas établie, on retourne null
        if (connection == null) {
            return null;
        }

        String sql = "SELECT i.*, a.degats FROM items i LEFT JOIN armes a ON i.id = a.item_id WHERE i.id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return extraireItemDeResultSet(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Extrait les données d'un item à partir d'un ResultSet et crée l'instance appropriée selon le type.
     *
     * @param resultSet Le ResultSet contenant les données de l'item
     * @return Un objet Item (ou une de ses sous-classes) créé à partir des données du ResultSet
     * @throws SQLException Si une erreur survient lors de l'extraction des données
     */
    private Item extraireItemDeResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String nom = resultSet.getString("nom");
        int quantiteMax = resultSet.getInt("quantiteMax");
        String type = resultSet.getString("type");
        int prix = resultSet.getInt("prix");

        // Créer l'item en fonction de son type
        switch (type) {
            case "arme":
                double degats = 0.0;
                // Récupérer les dégats de l'arme depuis la table armes
                try {
                    String sqlArme = "SELECT degats FROM armes WHERE item_id = ?";
                    try (PreparedStatement stmtArme = connection.prepareStatement(sqlArme)) {
                        stmtArme.setInt(1, id);
                        ResultSet rsArme = stmtArme.executeQuery();
                        if (rsArme.next()) {
                            degats = rsArme.getDouble("degats");
                        }
                    }
                } catch (SQLException e) {
                    System.err.println("Erreur lors de la récupération des dégâts de l'arme: " + e.getMessage());
                    // Si la table n'existe pas encore ou s'il y a un autre problème, on utilise une valeur par défaut
                    degats = 10.0;
                }
                return new Arme(id, nom, quantiteMax, prix, degats);
            case "bouclier":
                // Récupérer la défense du bouclier depuis la base de données
                double defense = 0;
                try {
                    String sqlBouclier = "SELECT defense FROM boucliers WHERE item_id = ?";
                    try (PreparedStatement stmtBouclier = connection.prepareStatement(sqlBouclier)) {
                        stmtBouclier.setInt(1, id);
                        ResultSet rsBouclier = stmtBouclier.executeQuery();
                        if (rsBouclier.next()) {
                            defense = rsBouclier.getDouble("defense");
                        }
                    }
                } catch (SQLException e) {
                    System.err.println("Erreur lors de la récupération de la défense du bouclier: " + e.getMessage());
                    // Si la table n'existe pas encore ou s'il y a un autre problème, on utilise une valeur par défaut
                    defense = 10.0; // Valeur par défaut pour la défense
                }
                return new Bouclier(id, nom, quantiteMax, prix, defense);
            case "potion":
                // Récupérer les valeurs degats et soin de la potion depuis la base de données
                double potionDegats = 0;
                double potionSoin = 0;
                try {
                    String sqlPotion = "SELECT degats, soin FROM potions WHERE item_id = ?";
                    try (PreparedStatement stmtPotion = connection.prepareStatement(sqlPotion)) {
                        stmtPotion.setInt(1, id);
                        ResultSet rsPotion = stmtPotion.executeQuery();
                        if (rsPotion.next()) {
                            potionDegats = rsPotion.getDouble("degats");
                            potionSoin = rsPotion.getDouble("soin");
                        }
                    }
                } catch (SQLException e) {
                    System.err.println("Erreur lors de la récupération des propriétés de la potion: " + e.getMessage());
                    // Si la table n'existe pas encore ou s'il y a un autre problème, on utilise des valeurs par défaut
                    potionDegats = 0.0; // Valeur par défaut pour les dégâts
                    potionSoin = 20.0;  // Valeur par défaut pour les soins
                }
                return new Potion(id, nom, quantiteMax, prix, potionDegats, potionSoin);
            default:
                // Gérer le cas par défaut
                throw new SQLException("Type d'item inconnu: " + type);
        }
    }

    /**
     * Récupère tous les items enregistrés dans la base de données.
     *
     * @return Une liste de tous les items ou une liste vide si la connexion n'est pas établie
     */
    @Override
    public List<Item> obtenirTousLesItems() {
        // Si la connexion n'est pas établie, on retourne une liste vide
        if (connection == null) {
            return new ArrayList<>();
        }

        List<Item> items = new ArrayList<>();
        // Requête complète qui récupère tous les items avec leurs propriétés spécifiques
        String sql = "SELECT i.* FROM items i";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                try {
                    Item item = extraireItemDeResultSet(resultSet);
                    items.add(item);
                } catch (SQLException e) {
                    System.err.println("Erreur lors de l'extraction d'un item: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la récupération des items: " + e.getMessage());
        }
        return items;
    }

    /**
     * Récupère tous les items d'un type spécifique.
     *
     * @param type Le type d'item à récupérer (arme, bouclier, potion, etc.)
     * @return Une liste des items du type spécifié ou une liste vide si la connexion n'est pas établie
     */
    @Override
    public List<Item> obtenirItemsParType(String type) {
        // Si la connexion n'est pas établie, on retourne une liste vide
        if (connection == null) {
            return new ArrayList<>();
        }

        List<Item> items = new ArrayList<>();
        // Requête qui récupère les items d'un type spécifique
        String sql = "SELECT i.* FROM items i WHERE i.type = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, type);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                try {
                    Item item = extraireItemDeResultSet(resultSet);
                    items.add(item);
                } catch (SQLException e) {
                    System.err.println("Erreur lors de l'extraction d'un item: " + e.getMessage());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Erreur lors de la récupération des items par type: " + e.getMessage());
        }
        return items;
    }

    /**
     * Met à jour les informations d'un item existant.
     * Gère également la mise à jour des propriétés spécifiques selon le type d'item.
     *
     * @param item L'item avec les nouvelles informations
     * @throws RuntimeException Si la connexion n'est pas établie ou si une erreur survient lors de la mise à jour
     */
    @Override
    public void mettreAJourItem(Item item) {
        // Si la connexion n'est pas établie, on ne peut pas mettre à jour d'item
        if (connection == null) {
            throw new RuntimeException("La connexion à la base de données n'est pas établie");
        }

        String sql = "UPDATE items SET nom = ?, quantiteMax = ?, prix = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, item.getNom());
            statement.setInt(2, item.getQuantiteMax());
            statement.setInt(3, item.getPrix());
            statement.setInt(4, item.getId());
            statement.executeUpdate();

            // Mettre à jour les propriétés spécifiques si nécessaire
            if (item instanceof Arme) {
                Arme arme = (Arme) item;
                String sqlArme = "UPDATE armes SET degats = ? WHERE item_id = ?";
                try (PreparedStatement statementArme = connection.prepareStatement(sqlArme)) {
                    statementArme.setDouble(1, arme.getDegats());
                    statementArme.setInt(2, arme.getId());
                    statementArme.executeUpdate();
                }
            }
            // Si c'est un bouclier, mettre à jour les propriétés spécifiques
            else if (item instanceof Bouclier) {
                Bouclier bouclier = (Bouclier) item;
                String sqlBouclier = "UPDATE boucliers SET defense = ? WHERE item_id = ?";
                try (PreparedStatement statementBouclier = connection.prepareStatement(sqlBouclier)) {
                    statementBouclier.setDouble(1, bouclier.getDefense());
                    statementBouclier.setInt(2, bouclier.getId());
                    statementBouclier.executeUpdate();
                }
            }
            // Si c'est une potion, mettre à jour les propriétés spécifiques
            else if (item instanceof Potion) {
                Potion potion = (Potion) item;
                String sqlPotion = "UPDATE potions SET degats = ?, soin = ? WHERE item_id = ?";
                try (PreparedStatement statementPotion = connection.prepareStatement(sqlPotion)) {
                    statementPotion.setDouble(1, potion.getDegats());
                    statementPotion.setDouble(2, potion.getSoin());
                    statementPotion.setInt(3, potion.getId());
                    statementPotion.executeUpdate();
                }
            }
            // Ajouter d'autres types d'items ici
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la mise à jour de l'item: " + e.getMessage());
        }
    }

    /**
     * Supprime un item de la base de données.
     * Supprime également les propriétés spécifiques associées (arme, bouclier, potion).
     *
     * @param id L'identifiant de l'item à supprimer
     * @throws RuntimeException Si la connexion n'est pas établie ou si une erreur survient lors de la suppression
     */
    @Override
    public void supprimerItem(int id) {
        // Si la connexion n'est pas établie, on ne peut pas supprimer d'item
        if (connection == null) {
            throw new RuntimeException("La connexion à la base de données n'est pas établie");
        }

        // Supprimer d'abord les propriétés spécifiques
        String sqlArme = "DELETE FROM armes WHERE item_id = ?";
        try (PreparedStatement statementArme = connection.prepareStatement(sqlArme)) {
            statementArme.setInt(1, id);
            statementArme.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Supprimer les propriétés spécifiques du bouclier
        String sqlBouclier = "DELETE FROM boucliers WHERE item_id = ?";
        try (PreparedStatement statementBouclier = connection.prepareStatement(sqlBouclier)) {
            statementBouclier.setInt(1, id);
            statementBouclier.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Supprimer les propriétés spécifiques de la potion
        String sqlPotion = "DELETE FROM potions WHERE item_id = ?";
        try (PreparedStatement statementPotion = connection.prepareStatement(sqlPotion)) {
            statementPotion.setInt(1, id);
            statementPotion.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Ajouter ici les suppressions pour d'autres types d'items

        // Puis supprimer l'item principal
        String sql = "DELETE FROM items WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de la suppression de l'item: " + e.getMessage());
        }
    }
}