package be.helha.projects.GuerreDesRoyaumes.Service;

import be.helha.projects.GuerreDesRoyaumes.DAO.CombatDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import org.springframework.stereotype.Service;

/**
 * Interface définissant les services liés au système de combat
 * dans le jeu Guerre des Royaumes.
 * <p>
 * Elle couvre la gestion du déroulement des combats, des actions des joueurs,
 * de la gestion des tours, et des interactions avec les objets et compétences.
 * </p>
 */
@Service
public interface ServiceCombat {

    /**
     * Initialise un combat entre deux joueurs avec les items sélectionnés.
     *
     * @param joueur1 Premier joueur participant.
     * @param joueur2 Second joueur participant.
     */
    void initialiserCombat(Joueur joueur1, Joueur joueur2);

    /**
     * Exécute une action pour un tour de combat.
     *
     * @param joueur     Le joueur qui effectue l'action.
     * @param adversaire L'adversaire.
     * @param typeAction Le type d'action (ex: "attaque", "defense", "special").
     * @param tour       Le numéro du tour actuel.
     * @return Un message décrivant le résultat de l'action.
     */
    String executerAction(Joueur joueur, Joueur adversaire, String typeAction, int tour);

    /**
     * Enregistre une victoire pour un joueur.
     *
     * @param joueur Le joueur gagnant.
     */
    void enregistrerVictoire(Joueur joueur);

    /**
     * Termine le combat et effectue les opérations de clôture nécessaires.
     *
     * @param joueur1  Premier joueur participant.
     * @param joueur2  Second joueur participant.
     * @param vainqueur Le joueur vainqueur (peut être null en cas d'égalité).
     */
    void terminerCombat(Joueur joueur1, Joueur joueur2, Joueur vainqueur);

    /**
     * Vérifie si un combat est terminé.
     * <p>
     * Condition : un joueur a 0 points de vie ou 5 tours sont écoulés.
     * </p>
     *
     * @param joueur1     Premier joueur.
     * @param joueur2     Second joueur.
     * @param tourActuel  Tour actuel du combat.
     * @return true si le combat est terminé, false sinon.
     */
    boolean estCombatTermine(Joueur joueur1, Joueur joueur2, int tourActuel);

    /**
     * Calcule les dégâts infligés par une attaque.
     *
     * @param attaquant  Le joueur attaquant.
     * @param defenseur  Le joueur défenseur.
     * @param typeAttaque Le type d'attaque (ex: normale, spéciale).
     * @return Le montant de dégâts calculé.
     */
    int calculerDegats(Joueur attaquant, Joueur defenseur, String typeAttaque);

    /**
     * Calcule la réduction de dégâts grâce à une défense.
     *
     * @param defenseur Le joueur en défense.
     * @param degats    Dégâts initiaux reçus.
     * @return Dégâts après réduction par la défense.
     */
    int calculerDefense(Joueur defenseur, int degats);

    /**
     * Vérifie si c'est le tour du joueur spécifié.
     *
     * @param joueur     Le joueur à vérifier.
     * @param adversaire L'adversaire.
     * @return true si c'est au tour du joueur de jouer, false sinon.
     */
    boolean estTourDuJoueur(Joueur joueur, Joueur adversaire);

    /**
     * Obtient le résultat de l'action adverse pour un joueur donné.
     *
     * @param joueur     Le joueur recevant le résultat.
     * @param adversaire L'adversaire qui a effectué l'action.
     * @param tour       Le numéro du tour.
     * @return Un message décrivant le résultat de l'action adverse.
     */
    String obtenirResultatActionAdverse(Joueur joueur, Joueur adversaire, int tour);

    /**
     * Force le changement de tour en mode développement.
     * <p>
     * Permet de débloquer une situation où deux joueurs attendent mutuellement.
     * </p>
     *
     * @param joueur     Le joueur qui doit recevoir le tour de jeu.
     * @param adversaire L'adversaire.
     * @return true si le changement a réussi, false sinon.
     */
    boolean forcerChangementTour(Joueur joueur, Joueur adversaire);

    /**
     * Obtient le numéro du tour actuel pour un combat.
     *
     * @param joueur     Joueur participant au combat.
     * @param adversaire Adversaire participant au combat.
     * @return Le numéro du tour actuel.
     */
    int getTourActuel(Joueur joueur, Joueur adversaire);

    /**
     * Obtient l'instance de {@link CombatDAO} utilisée par ce service.
     *
     * @return L'instance de CombatDAO.
     */
    CombatDAO getCombatDAO();

    /**
     * Transfère un item du coffre du joueur vers son inventaire de combat.
     *
     * @param joueur   Le joueur concerné.
     * @param item     L'item à transférer.
     * @param quantite La quantité à transférer.
     * @return true si le transfert a réussi, false sinon.
     */
    boolean transfererItemsCoffreVersInventaire(Joueur joueur, Item item, int quantite);

    /**
     * Vérifie si les deux joueurs sont prêts à combattre.
     *
     * @param joueur1 Premier joueur.
     * @param joueur2 Second joueur.
     * @return true si les deux joueurs sont prêts, false sinon.
     */
    boolean sontJoueursPrets(Joueur joueur1, Joueur joueur2);

    /**
     * Obtient l'identifiant du combat en cours pour un joueur donné.
     *
     * @param idJoueur Identifiant du joueur.
     * @return L'ID du combat en cours ou null si aucun combat trouvé.
     */
    String obtenirIdCombatEnCours(int idJoueur);

    /**
     * Met à jour les points de vie d'un joueur.
     *
     * @param idJoueur    Identifiant du joueur.
     * @param pointsDeVie Nouveaux points de vie.
     * @return true si la mise à jour a réussi, false sinon.
     */
    boolean mettreAJourPointsDeVie(int idJoueur, double pointsDeVie);

    /**
     * Passe le tour au joueur suivant.
     *
     * @param idCombat         Identifiant du combat.
     * @param idJoueurSuivant  Identifiant du joueur qui doit jouer ensuite.
     * @return true si le changement a réussi, false sinon.
     */
    boolean passerAuJoueurSuivant(String idCombat, int idJoueurSuivant);

    /**
     * Calcule les dégâts totaux d'un joueur en fonction de son personnage
     * et de ses armes équipées.
     *
     * @param joueur Le joueur dont on veut calculer les dégâts.
     * @return Le total des dégâts.
     */
    double calculerDegatsJoueur(Joueur joueur);

    /**
     * Calcule la défense totale d'un joueur en fonction de son personnage
     * et de ses équipements.
     *
     * @param joueur Le joueur dont on veut calculer la défense.
     * @return Le total de défense.
     */
    double calculerDefenseJoueur(Joueur joueur);
}
