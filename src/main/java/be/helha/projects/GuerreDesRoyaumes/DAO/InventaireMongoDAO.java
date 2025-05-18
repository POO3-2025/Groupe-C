package be.helha.projects.GuerreDesRoyaumes.DAO;

import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Slot;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;

import java.util.List;

/**
 * Interface définissant les opérations d'accès aux données pour les inventaires des personnages dans MongoDB.
 */
public interface InventaireMongoDAO {
    
    /**
     * Sauvegarde l'inventaire d'un personnage dans MongoDB.
     * 
     * @param joueur Le joueur propriétaire du personnage
     * @param inventaire L'inventaire à sauvegarder
     * @return true si la sauvegarde a réussi, false sinon
     */
    boolean sauvegarderInventaire(Joueur joueur, Inventaire inventaire);
    
    /**
     * Récupère l'inventaire d'un personnage depuis MongoDB.
     * 
     * @param joueurId L'identifiant du joueur
     * @return L'inventaire du personnage ou null si aucun inventaire n'est trouvé
     */
    Inventaire obtenirInventaireParJoueurId(int joueurId);
    
    /**
     * Ajoute un item à l'inventaire d'un personnage.
     * 
     * @param joueurId L'identifiant du joueur
     * @param item L'item à ajouter
     * @param quantite La quantité à ajouter
     * @return true si l'ajout a réussi, false sinon
     */
    boolean ajouterItemAInventaire(int joueurId, Item item, int quantite);
    
    /**
     * Retire un item de l'inventaire d'un personnage.
     * 
     * @param joueurId L'identifiant du joueur
     * @param itemId L'identifiant de l'item à retirer
     * @param quantite La quantité à retirer
     * @return true si le retrait a réussi, false sinon
     */
    boolean retirerItemDeInventaire(int joueurId, int itemId, int quantite);
    
    /**
     * Vide complètement l'inventaire d'un personnage.
     * 
     * @param joueurId L'identifiant du joueur
     * @return true si l'opération a réussi, false sinon
     */
    boolean viderInventaire(int joueurId);
    
    /**
     * Obtient la liste des slots de l'inventaire d'un personnage.
     * 
     * @param joueurId L'identifiant du joueur
     * @return La liste des slots de l'inventaire ou une liste vide si aucun inventaire n'est trouvé
     */
    List<Slot> obtenirSlotsInventaire(int joueurId);

    /**
     * Obtient la liste des items dans l'inventaire d'un personnage par le pseudo du joueur.
     * 
     * @param pseudo Le pseudo du joueur
     * @return La liste des items dans l'inventaire
     */
    List<Item> obtenirItemsInventaire(String pseudo);

    /**
     * Supprime un item de l'inventaire d'un personnage par le pseudo du joueur.
     * 
     * @param pseudo Le pseudo du joueur
     * @param itemId L'ID de l'item à supprimer
     * @return true si la suppression a réussi, false sinon
     */
    boolean supprimerItemInventaire(String pseudo, int itemId);
} 