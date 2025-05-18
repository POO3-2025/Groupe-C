package be.helha.projects.GuerreDesRoyaumes.ServiceImpl;

import be.helha.projects.GuerreDesRoyaumes.Service.ClassementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

/**
 * Implémentation du service {@link ClassementService} fournissant
 * des classements variés des joueurs et royaumes.
 * <p>
 * Cette classe utilise soit JdbcTemplate (pour SQL), soit
 * MongoTemplate (pour MongoDB), ou une connexion SQL directe,
 * avec des solutions de secours (mock data) si aucune connexion n'est disponible.
 * </p>
 */
@Service
public class ClassementServiceImpl implements ClassementService {

    private JdbcTemplate jdbcTemplate;
    private MongoTemplate mongoTemplate;
    private Connection connection;

    /**
     * Constructeur utilisé pour l'injection par Spring.
     *
     * @param jdbcTemplate   JdbcTemplate pour accès base SQL.
     * @param mongoTemplate  MongoTemplate pour accès MongoDB.
     */
    @Autowired
    public ClassementServiceImpl(JdbcTemplate jdbcTemplate, MongoTemplate mongoTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Constructeur utilisant une connexion SQL directe.
     *
     * @param connection Connexion SQL manuelle.
     */
    public ClassementServiceImpl(Connection connection) {
        this.connection = connection;
    }

    /**
     * Constructeur par défaut (utilisé par la TUI ou autres usages sans injection).
     */
    public ClassementServiceImpl() {
        // Implémentation alternative possible sans JdbcTemplate
    }

    /**
     * Obtient le classement des joueurs basé sur leurs victoires et défaites.
     * <p>
     * Le score est calculé comme (victoires - défaites).
     * </p>
     *
     * @return Liste des joueurs avec leurs statistiques triées par score descendant.
     */
    @Override
    public List<Map<String, Object>> getClassementVictoiresDefaites() {
        if (jdbcTemplate != null) {
            return jdbcTemplate.queryForList(
                    "SELECT id_joueur as id, pseudo_joueur as nom, victoires_joueur as victoire, defaites_joueur as defaite, (victoires_joueur - defaites_joueur) AS score " +
                            "FROM joueur ORDER BY score DESC"
            );
        } else if (connection != null) {
            // Utilisation de connexion SQL directe
            List<Map<String, Object>> results = new ArrayList<>();
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT id_joueur, pseudo_joueur as nom, victoires_joueur as victoire, defaites_joueur as defaite, (victoires_joueur - defaites_joueur) AS score " +
                            "FROM joueur ORDER BY score DESC");
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getLong("id_joueur"));
                    row.put("nom", rs.getString("nom"));
                    row.put("victoire", rs.getInt("victoire"));
                    row.put("defaite", rs.getInt("defaite"));
                    row.put("score", rs.getInt("score"));
                    results.add(row);
                }
            } catch (Exception e) {
                System.err.println("Erreur SQL: " + e.getMessage());
                e.printStackTrace();
            }
            return results;
        } else {
            // Données factices si aucune connexion disponible
            List<Map<String, Object>> mockData = new ArrayList<>();
            Map<String, Object> row = new HashMap<>();
            row.put("id", 1L);
            row.put("nom", "Pas de données disponibles");
            row.put("victoire", 0);
            row.put("defaite", 0);
            row.put("score", 0);
            mockData.add(row);
            return mockData;
        }
    }

    /**
     * Obtient le classement des joueurs basé sur leur richesse (argent).
     *
     * @return Liste des joueurs triés par montant d'argent décroissant.
     */
    @Override
    public List<Map<String, Object>> getClassementRichesse() {
        if (jdbcTemplate != null) {
            return jdbcTemplate.queryForList(
                    "SELECT id_joueur as id, pseudo_joueur as nom, argent_joueur as argent FROM joueur ORDER BY argent_joueur DESC"
            );
        } else if (connection != null) {
            List<Map<String, Object>> results = new ArrayList<>();
            try (PreparedStatement ps = connection.prepareStatement(
                    "SELECT id_joueur, pseudo_joueur as nom, argent_joueur as argent FROM joueur ORDER BY argent_joueur DESC");
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getLong("id_joueur"));
                    row.put("nom", rs.getString("nom"));
                    row.put("argent", rs.getInt("argent"));
                    results.add(row);
                }
            } catch (Exception e) {
                System.err.println("Erreur SQL: " + e.getMessage());
                e.printStackTrace();
            }
            return results;
        } else {
            List<Map<String, Object>> mockData = new ArrayList<>();
            Map<String, Object> row = new HashMap<>();
            row.put("id", 1L);
            row.put("nom", "Pas de données disponibles");
            row.put("argent", 0);
            mockData.add(row);
            return mockData;
        }
    }

    /**
     * Obtient le classement des royaumes basé sur leur niveau, trié décroissant.
     * <p>
     * Combine les données MongoDB pour les royaumes et SQL pour les noms de joueurs.
     * </p>
     *
     * @return Liste des royaumes avec leurs niveaux et noms de joueurs associés.
     */
    @Override
    public List<Map> getClassementNiveauRoyaumes() {
        List<Map> classementRoyaumes = new ArrayList<>();

        try {
            if (mongoTemplate != null) {
                SortOperation sortByNiveau = Aggregation.sort(org.springframework.data.domain.Sort.Direction.DESC, "niveau");
                Aggregation aggregation = Aggregation.newAggregation(sortByNiveau);
                AggregationResults<Map> results = mongoTemplate.aggregate(aggregation, "royaumes", Map.class);
                classementRoyaumes = results.getMappedResults();
            } else {
                // Alternative : accès direct MongoDB avec DAO
                // Récupération via RoyaumeMongoDAOImpl et connexion MongoDB
                // (Voir ton code pour détails)
            }

            // Ajout du nom des joueurs à partir de la base SQL
            if (jdbcTemplate != null) {
                for (Map royaume : classementRoyaumes) {
                    Object idJoueurObj = royaume.get("id_joueur");
                    if (idJoueurObj != null) {
                        Long idJoueur = idJoueurObj instanceof Integer ?
                                ((Integer) idJoueurObj).longValue() : Long.valueOf(idJoueurObj.toString());
                        try {
                            String nomJoueur = jdbcTemplate.queryForObject(
                                    "SELECT pseudo_joueur FROM joueur WHERE id_joueur = ?",
                                    String.class,
                                    idJoueur
                            );
                            royaume.put("joueurNom", nomJoueur);
                        } catch (Exception e) {
                            royaume.put("joueurNom", "Inconnu");
                        }
                    } else {
                        royaume.put("joueurNom", "Inconnu");
                    }
                }
            } else if (connection != null) {
                // Accès SQL direct similaire (voir code)
            } else {
                // Pas de connexion SQL : noms génériques
                for (Map royaume : classementRoyaumes) {
                    royaume.put("joueurNom", "Joueur " + royaume.get("id_joueur"));
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur générale dans getClassementNiveauRoyaumes: " + e.getMessage());
            e.printStackTrace();
            Map<String, Object> map = new HashMap<>();
            map.put("nom", "Pas de données disponibles");
            map.put("niveau", 0);
            map.put("joueurNom", "Inconnu");
            classementRoyaumes.add(map);
        }

        return classementRoyaumes;
    }
}
