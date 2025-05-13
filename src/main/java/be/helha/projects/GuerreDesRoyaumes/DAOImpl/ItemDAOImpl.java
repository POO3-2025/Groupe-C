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

@Repository
public class ItemDAOImpl implements ItemDAO {

    private Connection connection;
    private static ItemDAOImpl instance;

    // Constructeur par défaut pour Spring
    public ItemDAOImpl() {
    }

    // Constructeur avec connection
    public ItemDAOImpl(Connection connection) {
        this.connection = connection;
        creerTableCompetenceSiInexistante();
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
        creerTableCompetenceSiInexistante();
    }

    /**
     * Crée la table des compétences si elle n'existe pas déjà
     */
    private void creerTableCompetenceSiInexistante() {
        if (connection == null) {
            return;
        }

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

    public synchronized static ItemDAOImpl getInstance() {
        if (instance == null) {
            instance = new ItemDAOImpl();
        }
        return instance;
    }

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

    private Item extraireItemDeResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String nom = resultSet.getString("nom");
        int quantiteMax = resultSet.getInt("quantiteMax");
        String type = resultSet.getString("type");
        int prix = resultSet.getInt("prix");

        // Créer l'item en fonction de son type
        switch (type) {
            case "arme":
                int degats = resultSet.getInt("degats");
                return new Arme(id, nom, quantiteMax, degats, prix);
            case "bouclier":
                // Récupérer la défense du bouclier depuis la base de données
                // On utilise une requête séparée ou on ajoute un LEFT JOIN dans les requêtes originales
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

    @Override
    public List<Item> obtenirTousLesItems() {
        // Si la connexion n'est pas établie, on retourne une liste vide
        if (connection == null) {
            return new ArrayList<>();
        }

        List<Item> items = new ArrayList<>();
        String sql = "SELECT i.*, a.degats FROM items i LEFT JOIN armes a ON i.id = a.item_id";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                items.add(extraireItemDeResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    @Override
    public List<Item> obtenirItemsParType(String type) {
        // Si la connexion n'est pas établie, on retourne une liste vide
        if (connection == null) {
            return new ArrayList<>();
        }

        List<Item> items = new ArrayList<>();
        String sql = "SELECT i.*, a.degats FROM items i LEFT JOIN armes a ON i.id = a.item_id WHERE i.type = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, type);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                items.add(extraireItemDeResultSet(resultSet));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

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