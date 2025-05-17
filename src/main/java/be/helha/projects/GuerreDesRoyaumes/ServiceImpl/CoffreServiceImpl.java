package be.helha.projects.GuerreDesRoyaumes.ServiceImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.ItemDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.ItemMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Coffre;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Slot;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Service.CoffreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.*;

/**
 * Implémentation de l'interface CoffreService pour la gestion des coffres des joueurs.
 */
@Service
public class CoffreServiceImpl implements CoffreService {

    private static CoffreServiceImpl instance;
    private Connection connection;
    private final String tableName = "coffre_items";
    private ItemDAO itemDAO;

    /**
     * Constructeur par défaut
     */
    public CoffreServiceImpl() {
        this.itemDAO = ItemMongoDAOImpl.getInstance();
    }

    /**
     * Obtient l'instance unique de CoffreServiceImpl (Singleton)
     * @return L'instance unique de CoffreServiceImpl
     */
    public static synchronized CoffreServiceImpl getInstance() {
        if (instance == null) {
            instance = new CoffreServiceImpl();
        }
        return instance;
    }

    /**
     * Récupère la connexion actuellement utilisée par ce service
     * @return La connexion à la base de données
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * Définit une connexion SQL externe pour ce service.
     * @param connection La connexion SQL à utiliser
     */
    public void setConnection(Connection connection) {
        this.connection = connection;
        creerTableCoffreItemsSiInexistante();
    }

    /**
     * Configure la source de données (pour Spring)
     * @param dataSource La source de données à utiliser
     */
    @Autowired
    public void setDataSource(DataSource dataSource) {
        try {
            this.connection = dataSource.getConnection();
            creerTableCoffreItemsSiInexistante();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion à la base de données: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Définit le DAO des items à utiliser
     * @param itemDAO L'ItemDAO à utiliser
     */
    public void setItemDAO(ItemDAO itemDAO) {
        this.itemDAO = itemDAO;
    }

    /**
     * Crée la table coffre_items si elle n'existe pas déjà
     */
    private void creerTableCoffreItemsSiInexistante() {
        if (connection == null) {
            System.err.println("Impossible de créer la table coffre_items: connexion nulle");
            return;
        }

        String createTableCoffreItems = """
        IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='coffre_items' AND xtype='U')
        BEGIN
            CREATE TABLE coffre_items (
                id INT PRIMARY KEY IDENTITY(1,1),
                id_joueur INT NOT NULL,
                id_item INT NOT NULL,
                quantite INT NOT NULL,
                FOREIGN KEY (id_joueur) REFERENCES joueur(id_joueur) ON DELETE CASCADE
            )
        END
        """;

        try (PreparedStatement stmt = connection.prepareStatement(createTableCoffreItems)) {
            stmt.executeUpdate();
            System.out.println("Table coffre_items créée ou déjà existante");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la création de la table coffre_items: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean sauvegarderCoffre(Joueur joueur) {
        if (joueur == null || joueur.getCoffre() == null) {
            System.err.println("Impossible de sauvegarder le coffre: joueur ou coffre null");
            return false;
        }

        if (connection == null) {
            System.err.println("Impossible de sauvegarder le coffre: connexion nulle");
            return false;
        }

        try {
            // D'abord, supprimer tous les items existants pour ce joueur
            String deleteSQL = "DELETE FROM " + tableName + " WHERE id_joueur = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteSQL)) {
                stmt.setInt(1, joueur.getId());
                stmt.executeUpdate();
            }

            // Ensuite, insérer les items actuels du coffre
            String insertSQL = "INSERT INTO " + tableName + " (id_joueur, id_item, quantite) VALUES (?, ?, ?)";
            try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
                // Parcourir tous les slots du coffre
                for (Slot slot : joueur.getCoffre().getSlots()) {
                    if (slot != null && slot.getItem() != null && slot.getQuantity() > 0) {
                        stmt.setInt(1, joueur.getId());
                        stmt.setInt(2, slot.getItem().getId());
                        stmt.setInt(3, slot.getQuantity());
                        stmt.executeUpdate();
                    }
                }
            }

            System.out.println("Coffre sauvegardé pour le joueur: " + joueur.getPseudo());
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la sauvegarde du coffre: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean chargerCoffre(Joueur joueur) {
        if (joueur == null) {
            System.err.println("Impossible de charger le coffre: joueur null");
            return false;
        }

        if (connection == null) {
            System.err.println("Impossible de charger le coffre: connexion nulle");
            return false;
        }

        if (itemDAO == null) {
            System.err.println("Impossible de charger le coffre: itemDAO null");
            return false;
        }

        try {
            // Vider le coffre actuel ou créer un nouveau si nécessaire
            if (joueur.getCoffre() == null) {
                joueur.setCoffre(new Coffre());
            } else {
                // Créer un nouveau coffre vide
                joueur.setCoffre(new Coffre());
            }

            String selectSQL = "SELECT id_item, quantite FROM " + tableName + " WHERE id_joueur = ?";
            try (PreparedStatement stmt = connection.prepareStatement(selectSQL)) {
                stmt.setInt(1, joueur.getId());
                ResultSet rs = stmt.executeQuery();

                // Ajouter chaque item au coffre
                while (rs.next()) {
                    int itemId = rs.getInt("id_item");
                    int quantite = rs.getInt("quantite");

                    // Récupérer l'item dans la base de données
                    Item item = itemDAO.obtenirItemParId(itemId);
                    if (item != null) {
                        // Ajouter l'item au coffre
                        joueur.getCoffre().ajouterItem(item, quantite);
                    }
                }
            }

            System.out.println("Coffre chargé pour le joueur: " + joueur.getPseudo());
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement du coffre: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean viderCoffre(Joueur joueur) {
        if (joueur == null) {
            System.err.println("Impossible de vider le coffre: joueur null");
            return false;
        }

        if (connection == null) {
            System.err.println("Impossible de vider le coffre: connexion nulle");
            return false;
        }

        try {
            String deleteSQL = "DELETE FROM " + tableName + " WHERE id_joueur = ?";
            try (PreparedStatement stmt = connection.prepareStatement(deleteSQL)) {
                stmt.setInt(1, joueur.getId());
                stmt.executeUpdate();
            }

            // Vider également l'objet coffre en mémoire
            if (joueur.getCoffre() != null) {
                joueur.setCoffre(new Coffre());
            }

            System.out.println("Coffre vidé pour le joueur: " + joueur.getPseudo());
            return true;
        } catch (SQLException e) {
            System.err.println("Erreur lors du vidage du coffre: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}