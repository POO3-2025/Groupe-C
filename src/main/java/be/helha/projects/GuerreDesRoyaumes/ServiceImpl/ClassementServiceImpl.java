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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ClassementServiceImpl implements ClassementService {

    private JdbcTemplate jdbcTemplate;
    private MongoTemplate mongoTemplate;
    private Connection connection;

    // Constructeur pour l'injection par Spring
    @Autowired
    public ClassementServiceImpl(JdbcTemplate jdbcTemplate, MongoTemplate mongoTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.mongoTemplate = mongoTemplate;
    }

    // Constructeur pour utilisation directe avec une connexion SQL
    public ClassementServiceImpl(Connection connection) {
        this.connection = connection;
    }

    // Constructeur par défaut (pour la TUI)
    public ClassementServiceImpl() {
        // On utilisera des méthodes alternatives sans JdbcTemplate
    }

    @Override
    public List<Map<String, Object>> getClassementVictoiresDefaites() {
        if (jdbcTemplate != null) {
            return jdbcTemplate.queryForList(
                    "SELECT id_joueur as id, pseudo_joueur as nom, victoires_joueur as victoire, defaites_joueur as defaite, (victoires_joueur - defaites_joueur) AS score " +
                    "FROM joueur ORDER BY score DESC"
            );
        } else if (connection != null) {
            List<Map<String, Object>> results = new ArrayList<>();
            try {
                PreparedStatement ps = connection.prepareStatement(
                        "SELECT id_joueur, pseudo_joueur as nom, victoires_joueur as victoire, defaites_joueur as defaite, (victoires_joueur - defaites_joueur) AS score " +
                        "FROM joueur ORDER BY score DESC"
                );
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getLong("id_joueur"));
                    row.put("nom", rs.getString("nom"));
                    row.put("victoire", rs.getInt("victoire"));
                    row.put("defaite", rs.getInt("defaite"));
                    row.put("score", rs.getInt("score"));
                    results.add(row);
                }
                rs.close();
                ps.close();
            } catch (Exception e) {
                System.err.println("Erreur SQL: " + e.getMessage());
                e.printStackTrace();
            }
            return results;
        } else {
            // Données fictives si aucune connexion disponible
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

    @Override
    public List<Map<String, Object>> getClassementRichesse() {
        if (jdbcTemplate != null) {
            return jdbcTemplate.queryForList(
                    "SELECT id_joueur as id, pseudo_joueur as nom, argent_joueur as argent FROM joueur ORDER BY argent_joueur DESC"
            );
        } else if (connection != null) {
            List<Map<String, Object>> results = new ArrayList<>();
            try {
                PreparedStatement ps = connection.prepareStatement(
                        "SELECT id_joueur, pseudo_joueur as nom, argent_joueur as argent FROM joueur ORDER BY argent_joueur DESC"
                );
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getLong("id_joueur"));
                    row.put("nom", rs.getString("nom"));
                    row.put("argent", rs.getInt("argent"));
                    results.add(row);
                }
                rs.close();
                ps.close();
            } catch (Exception e) {
                System.err.println("Erreur SQL: " + e.getMessage());
                e.printStackTrace();
            }
            return results;
        } else {
            // Données fictives si aucune connexion disponible
            List<Map<String, Object>> mockData = new ArrayList<>();
            Map<String, Object> row = new HashMap<>();
            row.put("id", 1L);
            row.put("nom", "Pas de données disponibles");
            row.put("argent", 0);
            mockData.add(row);
            return mockData;
        }
    }

    @Override
    public List<Map> getClassementNiveauRoyaumes() {
        List<Map> classementRoyaumes = new ArrayList<>();
        
        try {
            if (mongoTemplate != null) {
                // Trier par niveau
                SortOperation sortByNiveau = Aggregation.sort(org.springframework.data.domain.Sort.Direction.DESC, "niveau");
                
                // Créer l'agrégation
                Aggregation aggregation = Aggregation.newAggregation(
                    sortByNiveau
                );
                
                // Exécuter l'agrégation
                AggregationResults<Map> results = mongoTemplate.aggregate(
                    aggregation, "royaumes", Map.class
                );
                
                classementRoyaumes = results.getMappedResults();
            } else {
                // Alternative si mongoTemplate n'est pas disponible
                try {
                    System.out.println("Utilisation de l'alternative pour MongoDB");
                    
                    // Utiliser RoyaumeMongoDAOImpl directement
                    be.helha.projects.GuerreDesRoyaumes.DAOImpl.RoyaumeMongoDAOImpl royaumeDAO = 
                        be.helha.projects.GuerreDesRoyaumes.DAOImpl.RoyaumeMongoDAOImpl.getInstance();
                    
                    // Utiliser MongoDatabase directement via ConnexionManager
                    com.mongodb.client.MongoDatabase mongoDB = 
                        be.helha.projects.GuerreDesRoyaumes.Config.ConnexionConfig.ConnexionManager.getInstance().getMongoDatabase();
                    
                    if (mongoDB != null) {
                        com.mongodb.client.MongoCollection<org.bson.Document> collection = mongoDB.getCollection("royaumes");
                        com.mongodb.client.FindIterable<org.bson.Document> documents = collection.find()
                            .sort(new org.bson.Document("niveau", -1));
                        
                        for (org.bson.Document doc : documents) {
                            Map<String, Object> map = new HashMap<>();
                            map.put("id_joueur", doc.getInteger("id_joueur"));
                            map.put("nom", doc.getString("nom"));
                            map.put("niveau", doc.getInteger("niveau"));
                            classementRoyaumes.add(map);
                        }
                        System.out.println("Récupéré " + classementRoyaumes.size() + " royaumes depuis MongoDB");
                    }
                } catch (Exception e) {
                    System.err.println("Erreur lors de l'accès direct à MongoDB: " + e.getMessage());
                    e.printStackTrace();
                    // Continuer avec une liste vide en cas d'erreur
                }
            }
            
            // Pour chaque royaume, récupérer le nom du joueur depuis SQL
            if (jdbcTemplate != null) {
                for(Map royaume : classementRoyaumes) {
                    if(royaume.containsKey("id_joueur")) {
                        Object idJoueurObj = royaume.get("id_joueur");
                        Long idJoueur;
                        if (idJoueurObj instanceof Integer) {
                            idJoueur = ((Integer)idJoueurObj).longValue();
                        } else {
                            idJoueur = Long.valueOf(idJoueurObj.toString());
                        }
                        
                        try {
                            String nomJoueur = jdbcTemplate.queryForObject(
                                "SELECT pseudo_joueur FROM joueur WHERE id_joueur = ?", 
                                String.class, 
                                idJoueur
                            );
                            royaume.put("joueurNom", nomJoueur);
                        } catch (Exception e) {
                            royaume.put("joueurNom", "Inconnu");
                            System.err.println("Erreur lors de la récupération du nom du joueur via JdbcTemplate: " + e.getMessage());
                        }
                    } else {
                        royaume.put("joueurNom", "Inconnu");
                    }
                }
            } else if (connection != null) {
                for(Map royaume : classementRoyaumes) {
                    if(royaume.containsKey("id_joueur")) {
                        Object idJoueurObj = royaume.get("id_joueur");
                        Long idJoueur;
                        if (idJoueurObj instanceof Integer) {
                            idJoueur = ((Integer)idJoueurObj).longValue();
                        } else {
                            idJoueur = Long.valueOf(idJoueurObj.toString());
                        }
                        
                        try {
                            PreparedStatement ps = connection.prepareStatement(
                                "SELECT pseudo_joueur FROM joueur WHERE id_joueur = ?"
                            );
                            ps.setLong(1, idJoueur);
                            ResultSet rs = ps.executeQuery();
                            if (rs.next()) {
                                royaume.put("joueurNom", rs.getString("pseudo_joueur"));
                            } else {
                                royaume.put("joueurNom", "Inconnu");
                            }
                            rs.close();
                            ps.close();
                        } catch (Exception e) {
                            royaume.put("joueurNom", "Inconnu");
                            System.err.println("Erreur SQL: " + e.getMessage());
                        }
                    } else {
                        royaume.put("joueurNom", "Inconnu");
                    }
                }
            } else {
                // Pas de connexion SQL disponible, utiliser des noms génériques
                for(Map royaume : classementRoyaumes) {
                    royaume.put("joueurNom", "Joueur " + royaume.get("id_joueur"));
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur générale dans getClassementNiveauRoyaumes: " + e.getMessage());
            e.printStackTrace();
            
            // Données fictives si aucune connexion MongoDB disponible
            Map<String, Object> map = new HashMap<>();
            map.put("nom", "Pas de données disponibles");
            map.put("niveau", 0);
            map.put("joueurNom", "Inconnu");
            classementRoyaumes.add(map);
        }
        
        return classementRoyaumes;
    }
} 