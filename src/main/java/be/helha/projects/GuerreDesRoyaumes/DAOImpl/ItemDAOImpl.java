package be.helha.projects.GuerreDesRoyaumes.DAOImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.ItemDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Arme;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public abstract class ItemDAOImpl implements ItemDAO {

    private Connection connection;

    public ItemDAOImpl(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void ajouterItem(Item item) {
        String sql = "INSERT INTO items (nom, quantiteMax, type, prix) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            statement.setString(1, item.getNom());
            statement.setInt(2, item.getQuantiteMax());
            statement.setString(3, obtenirTypeItem(item));
            statement.setInt(4, obtenirPrixItem(item));
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
            // Ajouter d'autres types d'items ici
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String obtenirTypeItem(Item item) {
        if (item instanceof Arme) {
            return "arme";
        }
        // Ajouter d'autres types ici
        return "item";
    }

    private int obtenirPrixItem(Item item) {
        // Logique pour déterminer le prix d'un item
        return 100; // Prix par défaut
    }

    @Override
    public Item obtenirItemParId(int id) {
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
        double prix  = resultSet.getDouble("prix");

        switch (type) {
            case "arme":
                int degats = resultSet.getInt("degats");
                return new Arme(id, nom, quantiteMax, degats,prix);
            // Ajouter d'autres types ici
            default:
                // Créer une instance d'un item générique
                return null; // Remplacer par une implémentation d'Items
        }
    }
    @Override
    public List<Item> obtenirTousLesItems() {
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
        String sql = "UPDATE items SET nom = ?, quantiteMax = ? WHERE id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, item.getNom());
            statement.setInt(2, item.getQuantiteMax());
            statement.setInt(3, item.getId());
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
            // Ajouter d'autres types d'items ici
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void supprimerItem(int id) {
        // Supprimer d'abord les propriétés spécifiques directement ici
        String sqlArme = "DELETE FROM armes WHERE item_id = ?";
        try (PreparedStatement statementArme = connection.prepareStatement(sqlArme)) {
            statementArme.setInt(1, id);
            statementArme.executeUpdate();
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
        }
    }
}