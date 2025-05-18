package be.helha.projects.GuerreDesRoyaumes.Model.Combat;

import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Représente une session de combat entre deux joueurs.
 * Cette classe est persistée dans MongoDB.
 */
public class CombatSession {
    private String id;
    private int joueur1Id;
    private int joueur2Id;
    private String joueur1Pseudo;
    private String joueur2Pseudo;
    private int tourActuel;
    private CombatStatus status;
    private int joueurActifId;
    private Map<Integer, Map<Integer, Map<String, Object>>> actions; // tour -> joueurId -> action
    private Map<Integer, Map<String, Object>> resultats; // tour -> résultats
    private LocalDateTime dateDebut;
    private LocalDateTime dateFin;
    private int vainqueurId; // 0 si pas de vainqueur (match nul)
    private double pvJoueur1;
    private double pvJoueur2;
    
    /**
     * Constructeur par défaut.
     */
    public CombatSession() {
        this.id = new ObjectId().toString();
        this.tourActuel = 1;
        this.status = CombatStatus.INITIALISATION;
        this.actions = new HashMap<>();
        this.resultats = new HashMap<>();
        this.dateDebut = LocalDateTime.now();
        this.vainqueurId = 0;
        this.pvJoueur1 = 100.0;
        this.pvJoueur2 = 100.0;
    }
    
    /**
     * Constructeur avec les joueurs.
     * 
     * @param joueur1 Le premier joueur du combat
     * @param joueur2 Le second joueur du combat
     */
    public CombatSession(Joueur joueur1, Joueur joueur2) {
        this();
        this.joueur1Id = joueur1.getId();
        this.joueur2Id = joueur2.getId();
        this.joueur1Pseudo = joueur1.getPseudo();
        this.joueur2Pseudo = joueur2.getPseudo();
        this.joueurActifId = joueur1Id; // Le joueur 1 commence
    }

    // Getters et Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getJoueur1Id() {
        return joueur1Id;
    }

    public void setJoueur1Id(int joueur1Id) {
        this.joueur1Id = joueur1Id;
    }

    public int getJoueur2Id() {
        return joueur2Id;
    }

    public void setJoueur2Id(int joueur2Id) {
        this.joueur2Id = joueur2Id;
    }
    
    public String getJoueur1Pseudo() {
        return joueur1Pseudo;
    }
    
    public void setJoueur1Pseudo(String joueur1Pseudo) {
        this.joueur1Pseudo = joueur1Pseudo;
    }
    
    public String getJoueur2Pseudo() {
        return joueur2Pseudo;
    }
    
    public void setJoueur2Pseudo(String joueur2Pseudo) {
        this.joueur2Pseudo = joueur2Pseudo;
    }

    public int getTourActuel() {
        return tourActuel;
    }

    public void setTourActuel(int tourActuel) {
        this.tourActuel = tourActuel;
    }

    public CombatStatus getStatus() {
        return status;
    }

    public void setStatus(CombatStatus status) {
        this.status = status;
    }

    public int getJoueurActifId() {
        return joueurActifId;
    }

    public void setJoueurActifId(int joueurActifId) {
        this.joueurActifId = joueurActifId;
    }

    public Map<Integer, Map<Integer, Map<String, Object>>> getActions() {
        return actions;
    }

    public void setActions(Map<Integer, Map<Integer, Map<String, Object>>> actions) {
        this.actions = actions;
    }

    public Map<Integer, Map<String, Object>> getResultats() {
        return resultats;
    }

    public void setResultats(Map<Integer, Map<String, Object>> resultats) {
        this.resultats = resultats;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public int getVainqueurId() {
        return vainqueurId;
    }

    public void setVainqueurId(int vainqueurId) {
        this.vainqueurId = vainqueurId;
    }
    
    public double getPvJoueur1() {
        return pvJoueur1;
    }
    
    public void setPvJoueur1(double pvJoueur1) {
        this.pvJoueur1 = pvJoueur1;
    }
    
    public double getPvJoueur2() {
        return pvJoueur2;
    }
    
    public void setPvJoueur2(double pvJoueur2) {
        this.pvJoueur2 = pvJoueur2;
    }
    
    // Méthodes utilitaires
    
    /**
     * Ajoute une action pour un joueur à un tour spécifique.
     * 
     * @param tour Le numéro du tour
     * @param joueurId L'ID du joueur
     * @param action Les détails de l'action
     */
    public void ajouterAction(int tour, int joueurId, Map<String, Object> action) {
        if (!actions.containsKey(tour)) {
            actions.put(tour, new HashMap<>());
        }
        actions.get(tour).put(joueurId, action);
    }
    
    /**
     * Ajoute un résultat pour un tour spécifique.
     * 
     * @param tour Le numéro du tour
     * @param resultat Les détails du résultat
     */
    public void ajouterResultat(int tour, Map<String, Object> resultat) {
        resultats.put(tour, resultat);
    }
    
    /**
     * Vérifie si les deux joueurs ont soumis leurs actions pour un tour spécifique.
     * 
     * @param tour Le numéro du tour
     * @return true si les deux joueurs ont soumis leurs actions, false sinon
     */
    public boolean actionsCompletes(int tour) {
        if (!actions.containsKey(tour)) {
            return false;
        }
        Map<Integer, Map<String, Object>> actionsParJoueur = actions.get(tour);
        return actionsParJoueur.containsKey(joueur1Id) && actionsParJoueur.containsKey(joueur2Id);
    }
    
    /**
     * Passe au tour suivant.
     */
    public void passerAuTourSuivant() {
        tourActuel++;
        // Alterner le joueur actif
        joueurActifId = (joueurActifId == joueur1Id) ? joueur2Id : joueur1Id;
    }
    
    /**
     * Vérifie si le combat est terminé.
     * 
     * @return true si le combat est terminé, false sinon
     */
    public boolean estTermine() {
        return status == CombatStatus.TERMINE || tourActuel > 5 || pvJoueur1 <= 0 || pvJoueur2 <= 0;
    }
    
    /**
     * Termine le combat et détermine le vainqueur.
     */
    public void terminer() {
        status = CombatStatus.TERMINE;
        dateFin = LocalDateTime.now();
        
        // Déterminer le vainqueur
        if (pvJoueur1 <= 0 && pvJoueur2 <= 0) {
            vainqueurId = 0; // Match nul
        } else if (pvJoueur1 <= 0) {
            vainqueurId = joueur2Id;
        } else if (pvJoueur2 <= 0) {
            vainqueurId = joueur1Id;
        } else if (pvJoueur1 > pvJoueur2) {
            vainqueurId = joueur1Id;
        } else if (pvJoueur2 > pvJoueur1) {
            vainqueurId = joueur2Id;
        } else {
            vainqueurId = 0; // Match nul
        }
    }
}