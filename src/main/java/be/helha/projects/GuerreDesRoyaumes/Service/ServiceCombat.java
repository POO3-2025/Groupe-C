package be.helha.projects.GuerreDesRoyaumes.Service;

import be.helha.projects.GuerreDesRoyaumes.DAO.CombatDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import org.springframework.stereotype.Service;

@Service
public interface ServiceCombat {
    /**
     * Initialise un combat entre deux joueurs avec les items sélectionnés
     */
    void initialiserCombat(Joueur joueur1, Joueur joueur2);

    /**
     * Exécute une action pour un tour de combat
     * @param joueur Le joueur qui effectue l'action
     * @param adversaire L'adversaire
     * @param typeAction Le type d'action (attaque, defense, special)
     * @param tour Le numéro du tour actuel
     * @return Un message décrivant le résultat de l'action
     */
    String executerAction(Joueur joueur, Joueur adversaire, String typeAction, int tour);

    /**
     * Enregistre une victoire pour un joueur
     */
    void enregistrerVictoire(Joueur joueur);

    /**
     * Termine le combat et effectue les opérations de clôture
     */
    void terminerCombat(Joueur joueur1, Joueur joueur2, Joueur vainqueur);

    /**
     * Vérifie si un combat est terminé (un joueur à 0 PV ou 5 tours écoulés)
     */
    boolean estCombatTermine(Joueur joueur1, Joueur joueur2, int tourActuel);

    /**
     * Calcule les dégâts infligés par une attaque
     */
    int calculerDegats(Joueur attaquant, Joueur defenseur, String typeAttaque);

    /**
     * Calcule la réduction de dégâts grâce à une défense
     */
    int calculerDefense(Joueur defenseur, int degats);

    /**
     * Vérifie si c'est le tour du joueur spécifié
     * @param joueur Le joueur dont on veut vérifier si c'est son tour
     * @param adversaire L'adversaire
     * @return true si c'est au tour du joueur de jouer, false sinon
     */
    boolean estTourDuJoueur(Joueur joueur, Joueur adversaire);

    /**
     * Obtient le résultat de l'action adverse pour le joueur spécifié
     * @param joueur Le joueur pour lequel on veut obtenir le résultat
     * @param adversaire L'adversaire qui a effectué l'action
     * @param tour Le numéro du tour
     * @return Un message décrivant le résultat de l'action adverse
     */
    String obtenirResultatActionAdverse(Joueur joueur, Joueur adversaire, int tour);

    /**
     * Force le changement de tour en mode développement
     * Cette méthode permet de débloquer une situation où deux joueurs
     * attendent mutuellement
     *
     * @param joueur Le joueur qui doit recevoir le tour de jeu
     * @param adversaire L'adversaire
     * @return true si le changement a réussi, false sinon
     */
    boolean forcerChangementTour(Joueur joueur, Joueur adversaire);

    /**
     * Obtient le numéro du tour actuel pour un combat
     * @param joueur Le joueur participant au combat
     * @param adversaire L'adversaire participant au combat
     * @return Le numéro du tour actuel
     */
    int getTourActuel(Joueur joueur, Joueur adversaire);
    
    /**
     * Obtient l'instance de CombatDAO utilisée par ce service
     * @return L'instance de CombatDAO
     */
    CombatDAO getCombatDAO();
    
    /**
     * Transfère un item du coffre du joueur vers son inventaire de combat
     * @param joueur Le joueur concerné
     * @param item L'item à transférer
     * @param quantite La quantité à transférer
     * @return true si le transfert a réussi, false sinon
     */
    boolean transfererItemsCoffreVersInventaire(Joueur joueur, Item item, int quantite);

    /**
     * Vérifie si les deux joueurs sont prêts à combattre
     * 
     * @param joueur1 Le premier joueur
     * @param joueur2 Le second joueur
     * @return true si les deux joueurs sont prêts, false sinon
     */
    boolean sontJoueursPrets(Joueur joueur1, Joueur joueur2);

    /**
     * Obtient l'ID du combat en cours pour un joueur donné
     * 
     * @param idJoueur L'identifiant du joueur
     * @return L'ID du combat en cours ou null si aucun combat trouvé
     */
    String obtenirIdCombatEnCours(int idJoueur);
    
    /**
     * Met à jour les points de vie d'un joueur
     * 
     * @param idJoueur L'identifiant du joueur
     * @param pointsDeVie Les nouveaux points de vie
     * @return true si la mise à jour a réussi, false sinon
     */
    boolean mettreAJourPointsDeVie(int idJoueur, double pointsDeVie);
    
    /**
     * Passe le tour au joueur suivant
     * 
     * @param idCombat L'identifiant du combat
     * @param idJoueurSuivant L'identifiant du joueur qui doit jouer ensuite
     * @return true si le changement a réussi, false sinon
     */
    boolean passerAuJoueurSuivant(String idCombat, int idJoueurSuivant);
    
    /**
     * Calcule les dégâts totaux d'un joueur en fonction de son personnage et de ses armes équipées
     *
     * @param joueur Le joueur dont on veut calculer les dégâts
     * @return Le total des dégâts
     */
    double calculerDegatsJoueur(Joueur joueur);
    
    /**
     * Calcule la défense totale d'un joueur en fonction de son personnage et de ses équipements
     * 
     * @param joueur Le joueur dont on veut calculer la défense
     * @return Le total de défense
     */
    double calculerDefenseJoueur(Joueur joueur);
}
