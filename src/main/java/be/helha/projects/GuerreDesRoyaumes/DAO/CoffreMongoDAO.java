package be.helha.projects.GuerreDesRoyaumes.DAO;

import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Coffre;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Slot;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;

import java.util.List;

/**
 * Interface définissant les opérations d'accès aux données pour les coffres dans MongoDB.
 */
public interface CoffreMongoDAO {
    
    /**
     * Sauvegarde le coffre d'un joueur dans MongoDB.
     * 
     * @param joueur Le joueur propriétaire du coffre
     * @param coffre Le coffre à sauvegarder
     * @return true si la sauvegarde a réussi, false sinon
     */
    boolean sauvegarderCoffre(Joueur joueur, Coffre coffre);
    
    /**
     * Récupère le coffre d'un joueur depuis MongoDB.
     * 
     * @param joueurId L'identifiant du joueur
     * @return Le coffre du joueur ou null si aucun coffre n'est trouvé
     */
    Coffre obtenirCoffreParJoueurId(int joueurId);
    
    /**
     * Ajoute un item au coffre d'un joueur.
     * 
     * @param joueurId L'identifiant du joueur
     * @param item L'item à ajouter
     * @param quantite La quantité à ajouter
     * @return true si l'ajout a réussi, false sinon
     */
    boolean ajouterItemAuCoffre(int joueurId, Item item, int quantite);
    
    /**
     * Retire un item du coffre d'un joueur.
     * 
     * @param joueurId L'identifiant du joueur
     * @param itemId L'identifiant de l'item à retirer
     * @param quantite La quantité à retirer
     * @return true si le retrait a réussi, false sinon
     */
    boolean retirerItemDuCoffre(int joueurId, int itemId, int quantite);
    
    /**
     * Vide complètement le coffre d'un joueur.
     * 
     * @param joueurId L'identifiant du joueur
     * @return true si l'opération a réussi, false sinon
     */
    boolean viderCoffre(int joueurId);
    
    /**
     * Obtient la liste des slots du coffre d'un joueur.
     * 
     * @param joueurId L'identifiant du joueur
     * @return La liste des slots du coffre ou une liste vide si aucun coffre n'est trouvé
     */
    List<Slot> obtenirSlotsDuCoffre(int joueurId);

    List<Item> obtenirItemsDuCoffre(String pseudo);

    boolean supprimerItemDuCoffre(String pseudo, int itemId);
}