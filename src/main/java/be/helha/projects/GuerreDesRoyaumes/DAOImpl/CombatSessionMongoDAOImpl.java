package be.helha.projects.GuerreDesRoyaumes.DAOImpl;

import be.helha.projects.GuerreDesRoyaumes.Config.InitialiserAPP;
import be.helha.projects.GuerreDesRoyaumes.DAO.CombatSessionMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.MongoDBConnectionException;
import be.helha.projects.GuerreDesRoyaumes.Model.Combat.CombatSession;
import be.helha.projects.GuerreDesRoyaumes.Model.Combat.CombatStatus;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implémentation de l'interface CombatSessionMongoDAO pour la gestion des sessions de combat dans MongoDB.
 */
@Repository
public class CombatSessionMongoDAOImpl implements CombatSessionMongoDAO {

    private final MongoCollection<Document> sessionCollection;
    private final MongoCollection<Document> actionCollection;
    private final MongoCollection<Document> resultatCollection;
    private static final String SESSION_COLLECTION = "combatSessions";
    private static final String ACTION_COLLECTION = "combatActions";
    private static final String RESULTAT_COLLECTION = "combatResultats";

    /**
     * Constructeur pour l'injection de dépendances Spring
     */
    public CombatSessionMongoDAOImpl() {
        MongoDatabase mongoDB;
        try {
            mongoDB = InitialiserAPP.getMongoConnexion();
        } catch (MongoDBConnectionException ex) {
            throw new RuntimeException(ex);
        }
        this.sessionCollection = mongoDB.getCollection(SESSION_COLLECTION);
        this.actionCollection = mongoDB.getCollection(ACTION_COLLECTION);
        this.resultatCollection = mongoDB.getCollection(RESULTAT_COLLECTION);
    }

    @Override
    public String creerSession(Joueur joueur1, Joueur joueur2) {
        try {
            CombatSession session = new CombatSession(joueur1, joueur2);
            session.setStatus(CombatStatus.INITIALISATION);
            session.setPvJoueur1(joueur1.getPersonnage().getPointsDeVie());
            session.setPvJoueur2(joueur2.getPersonnage().getPointsDeVie());
            
            Document sessionDoc = new Document("_id", session.getId())
                    .append("joueur1Id", session.getJoueur1Id())
                    .append("joueur2Id", session.getJoueur2Id())
                    .append("joueur1Pseudo", session.getJoueur1Pseudo())
                    .append("joueur2Pseudo", session.getJoueur2Pseudo())
                    .append("tourActuel", session.getTourActuel())
                    .append("status", session.getStatus().toString())
                    .append("joueurActifId", session.getJoueurActifId())
                    .append("dateDebut", session.getDateDebut().toString())
                    .append("pvJoueur1", session.getPvJoueur1())
                    .append("pvJoueur2", session.getPvJoueur2())
                    .append("vainqueurId", 0);
            
            sessionCollection.insertOne(sessionDoc);
            System.out.println("Session de combat créée avec l'ID: " + session.getId());
            return session.getId();
        } catch (Exception e) {
            System.err.println("Erreur lors de la création de la session de combat: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean sauvegarderSession(CombatSession session) {
        try {
            Bson filter = Filters.eq("_id", session.getId());
            
            Document setDoc = new Document()
                    .append("tourActuel", session.getTourActuel())
                    .append("status", session.getStatus().toString())
                    .append("joueurActifId", session.getJoueurActifId())
                    .append("pvJoueur1", session.getPvJoueur1())
                    .append("pvJoueur2", session.getPvJoueur2());
            
            if (session.getDateFin() != null) {
                setDoc.append("dateFin", session.getDateFin().toString());
            }
            
            if (session.getVainqueurId() > 0) {
                setDoc.append("vainqueurId", session.getVainqueurId());
            }
            
            Document update = new Document("$set", setDoc);
            
            UpdateResult result = sessionCollection.updateOne(filter, update);
            return result.getModifiedCount() > 0;
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde de la session de combat: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public CombatSession chargerSession(String combatId) {
        try {
            Document sessionDoc = sessionCollection.find(Filters.eq("_id", combatId)).first();
            
            if (sessionDoc == null) {
                return null;
            }
            
            CombatSession session = new CombatSession();
            session.setId(sessionDoc.getString("_id"));
            session.setJoueur1Id(sessionDoc.getInteger("joueur1Id"));
            session.setJoueur2Id(sessionDoc.getInteger("joueur2Id"));
            session.setJoueur1Pseudo(sessionDoc.getString("joueur1Pseudo"));
            session.setJoueur2Pseudo(sessionDoc.getString("joueur2Pseudo"));
            session.setTourActuel(sessionDoc.getInteger("tourActuel"));
            session.setStatus(CombatStatus.valueOf(sessionDoc.getString("status")));
            session.setJoueurActifId(sessionDoc.getInteger("joueurActifId"));
            session.setDateDebut(LocalDateTime.parse(sessionDoc.getString("dateDebut")));
            session.setPvJoueur1(sessionDoc.getDouble("pvJoueur1"));
            session.setPvJoueur2(sessionDoc.getDouble("pvJoueur2"));
            session.setVainqueurId(sessionDoc.getInteger("vainqueurId", 0));
            
            if (sessionDoc.containsKey("dateFin")) {
                session.setDateFin(LocalDateTime.parse(sessionDoc.getString("dateFin")));
            }
            
            // Charger les actions
            Map<Integer, Map<Integer, Map<String, Object>>> actions = new HashMap<>();
            MongoCursor<Document> actionCursor = actionCollection.find(Filters.eq("combatId", combatId)).iterator();
            
            while (actionCursor.hasNext()) {
                Document actionDoc = actionCursor.next();
                int tour = actionDoc.getInteger("tour");
                int joueurId = actionDoc.getInteger("joueurId");
                
                if (!actions.containsKey(tour)) {
                    actions.put(tour, new HashMap<>());
                }
                
                Map<String, Object> actionDetails = new HashMap<>();
                actionDetails.put("typeAction", actionDoc.getString("typeAction"));
                
                Document parametres = (Document) actionDoc.get("parametres");
                if (parametres != null) {
                    for (String key : parametres.keySet()) {
                        actionDetails.put(key, parametres.get(key));
                    }
                }
                
                actions.get(tour).put(joueurId, actionDetails);
            }
            
            session.setActions(actions);
            
            // Charger les résultats
            Map<Integer, Map<String, Object>> resultats = new HashMap<>();
            MongoCursor<Document> resultatCursor = resultatCollection.find(Filters.eq("combatId", combatId)).iterator();
            
            while (resultatCursor.hasNext()) {
                Document resultatDoc = resultatCursor.next();
                int tour = resultatDoc.getInteger("tour");
                
                Map<String, Object> resultatDetails = new HashMap<>();
                Document details = (Document) resultatDoc.get("details");
                
                if (details != null) {
                    for (String key : details.keySet()) {
                        resultatDetails.put(key, details.get(key));
                    }
                }
                
                resultats.put(tour, resultatDetails);
            }
            
            session.setResultats(resultats);
            
            return session;
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de la session de combat: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String trouverSessionId(int joueur1Id, int joueur2Id) {
        try {
            // Vérification de la validité des IDs des joueurs
            if (joueur1Id <= 0 || joueur2Id <= 0) {
                System.out.println("trouverSessionId: Identifiants de joueurs invalides - J1: " + joueur1Id + ", J2: " + joueur2Id);
                return null;
            }
            
            // Chercher d'abord avec joueur1Id et joueur2Id
            Bson filter1 = Filters.and(
                    Filters.eq("joueur1Id", joueur1Id),
                    Filters.eq("joueur2Id", joueur2Id),
                    Filters.ne("status", CombatStatus.TERMINE.toString()),
                    Filters.ne("status", CombatStatus.ABANDONNE.toString())
            );
            
            Document session = sessionCollection.find(filter1).first();
            
            if (session != null) {
                return session.getString("_id");
            }
            
            // Chercher avec joueur2Id et joueur1Id (ordre inversé)
            Bson filter2 = Filters.and(
                    Filters.eq("joueur1Id", joueur2Id),
                    Filters.eq("joueur2Id", joueur1Id),
                    Filters.ne("status", CombatStatus.TERMINE.toString()),
                    Filters.ne("status", CombatStatus.ABANDONNE.toString())
            );
            
            session = sessionCollection.find(filter2).first();
            
            if (session != null) {
                return session.getString("_id");
            }
            
            return null;
        } catch (Exception e) {
            System.err.println("Erreur lors de la recherche de session: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean sauvegarderAction(String combatId, int joueurId, int tour, String typeAction, Map<String, Object> parametres) {
        try {
            // Vérifier si une action existe déjà pour ce joueur à ce tour
            Bson filter = Filters.and(
                    Filters.eq("combatId", combatId),
                    Filters.eq("joueurId", joueurId),
                    Filters.eq("tour", tour)
            );
            
            Document existingAction = actionCollection.find(filter).first();
            
            if (existingAction != null) {
                // Mettre à jour l'action existante
                Document parametresDoc = new Document();
                if (parametres != null) {
                    for (Map.Entry<String, Object> entry : parametres.entrySet()) {
                        parametresDoc.append(entry.getKey(), entry.getValue());
                    }
                }
                
                Document update = new Document("$set", new Document()
                        .append("typeAction", typeAction)
                        .append("parametres", parametresDoc)
                        .append("dateMAJ", LocalDateTime.now().toString()));
                
                UpdateResult result = actionCollection.updateOne(filter, update);
                return result.getModifiedCount() > 0;
            } else {
                // Insérer une nouvelle action
                Document parametresDoc = new Document();
                if (parametres != null) {
                    for (Map.Entry<String, Object> entry : parametres.entrySet()) {
                        parametresDoc.append(entry.getKey(), entry.getValue());
                    }
                }
                
                Document actionDoc = new Document()
                        .append("combatId", combatId)
                        .append("joueurId", joueurId)
                        .append("tour", tour)
                        .append("typeAction", typeAction)
                        .append("parametres", parametresDoc)
                        .append("dateCreation", LocalDateTime.now().toString());
                
                actionCollection.insertOne(actionDoc);
                
                // Mettre à jour la session de combat pour indiquer qu'une action a été soumise
                mettreAJourSessionAction(combatId, joueurId, tour);
                
                return true;
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde de l'action: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private void mettreAJourSessionAction(String combatId, int joueurId, int tour) {
        try {
            CombatSession session = chargerSession(combatId);
            
            if (session != null) {
                // Si les deux joueurs ont soumis leurs actions, changer le statut en RESOLUTION
                if (actionsCompletes(combatId, tour)) {
                    session.setStatus(CombatStatus.RESOLUTION);
                } else {
                    session.setStatus(CombatStatus.ATTENTE_ACTIONS);
                }
                
                sauvegarderSession(session);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour de la session après action: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Map<Integer, Map<String, Object>> obtenirActions(String combatId, int tour) {
        try {
            Map<Integer, Map<String, Object>> actions = new HashMap<>();
            
            Bson filter = Filters.and(
                    Filters.eq("combatId", combatId),
                    Filters.eq("tour", tour)
            );
            
            MongoCursor<Document> cursor = actionCollection.find(filter).iterator();
            
            while (cursor.hasNext()) {
                Document actionDoc = cursor.next();
                int joueurId = actionDoc.getInteger("joueurId");
                
                Map<String, Object> actionDetails = new HashMap<>();
                actionDetails.put("typeAction", actionDoc.getString("typeAction"));
                
                Document parametres = (Document) actionDoc.get("parametres");
                if (parametres != null) {
                    for (String key : parametres.keySet()) {
                        actionDetails.put(key, parametres.get(key));
                    }
                }
                
                actions.put(joueurId, actionDetails);
            }
            
            return actions;
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des actions: " + e.getMessage());
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    @Override
    public boolean actionsCompletes(String combatId, int tour) {
        try {
            CombatSession session = chargerSession(combatId);
            
            if (session == null) {
                return false;
            }
            
            Bson filter = Filters.and(
                    Filters.eq("combatId", combatId),
                    Filters.eq("tour", tour)
            );
            
            List<Document> actions = new ArrayList<>();
            actionCollection.find(filter).into(actions);
            
            // Vérifier si nous avons les actions des deux joueurs
            boolean joueur1ActionPresente = false;
            boolean joueur2ActionPresente = false;
            
            for (Document actionDoc : actions) {
                int joueurId = actionDoc.getInteger("joueurId");
                
                if (joueurId == session.getJoueur1Id()) {
                    joueur1ActionPresente = true;
                } else if (joueurId == session.getJoueur2Id()) {
                    joueur2ActionPresente = true;
                }
            }
            
            return joueur1ActionPresente && joueur2ActionPresente;
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification des actions complètes: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean sauvegarderResultatTour(String combatId, int tour, Map<String, Object> resultats) {
        try {
            // Vérifier si un résultat existe déjà pour ce tour
            Bson filter = Filters.and(
                    Filters.eq("combatId", combatId),
                    Filters.eq("tour", tour)
            );
            
            Document existingResultat = resultatCollection.find(filter).first();
            
            Document detailsDoc = new Document();
            if (resultats != null) {
                for (Map.Entry<String, Object> entry : resultats.entrySet()) {
                    detailsDoc.append(entry.getKey(), entry.getValue());
                }
            }
            
            if (existingResultat != null) {
                // Mettre à jour le résultat existant
                Document update = new Document("$set", new Document()
                        .append("details", detailsDoc)
                        .append("dateMAJ", LocalDateTime.now().toString()));
                
                UpdateResult result = resultatCollection.updateOne(filter, update);
                return result.getModifiedCount() > 0;
            } else {
                // Insérer un nouveau résultat
                Document resultatDoc = new Document()
                        .append("combatId", combatId)
                        .append("tour", tour)
                        .append("details", detailsDoc)
                        .append("dateCreation", LocalDateTime.now().toString());
                
                resultatCollection.insertOne(resultatDoc);
                
                // Mettre à jour la session de combat pour indiquer qu'un résultat a été soumis
                mettreAJourSessionResultat(combatId, tour);
                
                return true;
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la sauvegarde du résultat: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    private void mettreAJourSessionResultat(String combatId, int tour) {
        try {
            CombatSession session = chargerSession(combatId);
            
            if (session != null) {
                // Mettre à jour les points de vie et le statut si présents dans le résultat
                Map<String, Object> resultat = obtenirResultatTour(combatId, tour);
                
                if (resultat.containsKey("pvJoueur1")) {
                    session.setPvJoueur1((Double) resultat.get("pvJoueur1"));
                }
                
                if (resultat.containsKey("pvJoueur2")) {
                    session.setPvJoueur2((Double) resultat.get("pvJoueur2"));
                }
                
                // Vérifier si le combat est terminé
                if (session.estTermine()) {
                    session.terminer();
                } else {
                    // Passer au tour suivant
                    session.passerAuTourSuivant();
                    session.setStatus(CombatStatus.EN_COURS);
                }
                
                sauvegarderSession(session);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour de la session après résultat: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Object> obtenirResultatTour(String combatId, int tour) {
        try {
            Bson filter = Filters.and(
                    Filters.eq("combatId", combatId),
                    Filters.eq("tour", tour)
            );
            
            Document resultatDoc = resultatCollection.find(filter).first();
            
            if (resultatDoc == null) {
                return new HashMap<>();
            }
            
            Map<String, Object> resultats = new HashMap<>();
            Document details = (Document) resultatDoc.get("details");
            
            if (details != null) {
                for (String key : details.keySet()) {
                    resultats.put(key, details.get(key));
                }
            }
            
            return resultats;
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du résultat: " + e.getMessage());
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    @Override
    public boolean mettreAJourStatus(String combatId, CombatStatus status) {
        try {
            Bson filter = Filters.eq("_id", combatId);
            Bson update = Updates.set("status", status.toString());
            
            UpdateResult result = sessionCollection.updateOne(filter, update);
            return result.getModifiedCount() > 0;
        } catch (Exception e) {
            System.err.println("Erreur lors de la mise à jour du statut: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean estTourDuJoueur(String combatId, int joueurId) {
        try {
            Document sessionDoc = sessionCollection.find(Filters.eq("_id", combatId)).first();
            
            if (sessionDoc == null) {
                return false;
            }
            
            int joueurActifId = sessionDoc.getInteger("joueurActifId");
            return joueurActifId == joueurId;
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification du tour du joueur: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int obtenirTourActuel(String combatId) {
        try {
            Document sessionDoc = sessionCollection.find(Filters.eq("_id", combatId)).first();
            
            if (sessionDoc == null) {
                return 0;
            }
            
            return sessionDoc.getInteger("tourActuel");
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du tour actuel: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public boolean archiverSession(String combatId) {
        try {
            CombatSession session = chargerSession(combatId);
            
            if (session == null) {
                return false;
            }
            
            session.terminer();
            return sauvegarderSession(session);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'archivage de la session: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}