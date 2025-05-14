package be.helha.projects.GuerreDesRoyaumes.DAOImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.CombatDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Combat.Combat;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;

@Repository
public class CombatDAOImpl implements CombatDAO {

    private Connection connection;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        try {
            this.connection = dataSource.getConnection();
            // Ici on pourrait ajouter une méthode pour créer la table si elle n'existe pas
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la connexion à la base de données", e);
        }
    }

    // Constructeur par défaut requis par Spring
    public CombatDAOImpl() {
    }

    @Override
    public void enregistrerCombat(Combat combat) {
        // Insérer le combat dans la base de données
        String sql = "INSERT INTO combats (id_combat, joueur1_id, joueur2_id, gagnant, tours_final) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, combat.getId());
            stmt.setInt(2, combat.getJoueur1().getId());
            stmt.setInt(3, combat.getJoueur2().getId());
            stmt.setObject(4, combat.getVainqueur() != null ? combat.getVainqueur().getId() : null);
            stmt.setInt(5, combat.getNombreTours());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void enregistrerVictoire(Joueur joueur) {
        // Enregistrer une victoire du joueur dans la base de données
        String sql = "UPDATE joueurs SET victoires = victoires + 1 WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, joueur.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    @Override
    public Combat obtenirCombatParId(int id) {
        Combat combat = null;
        String sql = "SELECT * FROM combats WHERE id_combat = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return combat;
    }

    @Override
    public List<Combat> obtenirTousLesCombats() {
        List<Combat> combats = new ArrayList<>();
        String sql = "SELECT c.*, j.id as joueur_id FROM combats c LEFT JOIN joueurs j ON c.joueur_id = j.id";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return combats;
    }

    @Override
    public List<Combat> obtenirCombatsParJoueurId(int joueurId) {
        List<Combat> combats = new ArrayList<>();
        String sql = "SELECT c.*, j.id as joueur_id FROM combats c LEFT JOIN joueurs j ON c.joueur_id = j.id WHERE j.id = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, joueurId);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return combats;
    }

    @Override
    public void enregistrerDefaite(Joueur joueur) {
        // Enregistrer une défaite du joueur dans la base de données
        String sql = "UPDATE joueurs SET defaites = defaites + 1 WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, joueur.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public List<Joueur> getClassementParVictoires() {
        // Implémentation de la méthode pour obtenir le classement par victoires
        String sql = "SELECT * FROM joueurs ORDER BY victoires DESC";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            List<Joueur> classement = new ArrayList<>();
            while (resultSet.next()) {
                Joueur joueur = new Joueur();
                joueur.setId(resultSet.getInt("id"));
                joueur.setPseudo(resultSet.getString("pseudo"));
                joueur.setVictoires(resultSet.getInt("victoires"));
                classement.add(joueur);
            }
            return classement;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return List.of();
    }

    @Override
    public List<Joueur> getClassementParDefaites() {
        // Implémentation de la méthode pour obtenir le classement par défaites
        String sql = "SELECT * FROM joueurs ORDER BY defaites DESC";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            List<Joueur> classement = new ArrayList<>();
            while (resultSet.next()) {
                Joueur joueur = new Joueur();
                joueur.setId(resultSet.getInt("id"));
                joueur.setPseudo(resultSet.getString("pseudo"));
                joueur.setDefaites(resultSet.getInt("defaites"));
                classement.add(joueur);
            }
            return classement;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return List.of();
    }


}


