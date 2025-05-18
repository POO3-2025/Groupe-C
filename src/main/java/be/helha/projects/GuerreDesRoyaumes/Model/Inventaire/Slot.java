package be.helha.projects.GuerreDesRoyaumes.Model.Inventaire;

import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;

/**
 * Classe représentant un slot dans un stockage d'items.
 * <p>
 * Un slot contient un item ainsi qu'une quantité associée.
 * Cette classe gère l'ajout et le retrait de quantité, avec validation.
 * </p>
 */
public class Slot {

    private Item item;
    private int quantity;

    /**
     * Constructeur initialisant un slot avec un item et une quantité.
     *
     * @param item     Item stocké dans le slot.
     * @param quantity Quantité d'items dans ce slot.
     * @throws IllegalArgumentException si la quantité est négative.
     */
    public Slot(Item item, int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("La quantité ne peut pas être négative.");
        }
        this.item = item;
        this.quantity = quantity;
    }

    /**
     * Obtient l'item contenu dans ce slot.
     *
     * @return L'item du slot.
     */
    public Item getItem() {
        return item;
    }

    /**
     * Obtient la quantité d'items dans ce slot.
     *
     * @return La quantité actuelle.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Définit la quantité d'items dans ce slot.
     *
     * @param quantity Nouvelle quantité.
     * @throws IllegalArgumentException si la quantité est négative.
     */
    public void setQuantity(int quantity) {
        if (quantity >= 0) {
            this.quantity = quantity;
        } else {
            throw new IllegalArgumentException("La quantité ne peut pas être négative.");
        }
    }

    /**
     * Ajoute une quantité d'items à ce slot.
     *
     * @param quantityToAdd Quantité à ajouter (doit être positive).
     * @return La nouvelle quantité après ajout.
     * @throws IllegalArgumentException si la quantité à ajouter est négative ou nulle.
     */
    public int add(int quantityToAdd) {
        if (quantityToAdd > 0) {
            this.quantity += quantityToAdd;
        } else {
            throw new IllegalArgumentException("La quantité à ajouter doit être positive.");
        }
        return this.quantity;
    }

    /**
     * Retire une quantité d'items de ce slot.
     *
     * @param quantityToRemove Quantité à retirer (doit être positive et ≤ quantité actuelle).
     * @return La nouvelle quantité après retrait.
     * @throws IllegalArgumentException si la quantité à retirer est négative, nulle, ou supérieure à la quantité actuelle.
     */
    public int remove(int quantityToRemove) {
        if (quantityToRemove > 0 && quantityToRemove <= this.quantity) {
            this.quantity -= quantityToRemove;
        } else {
            throw new IllegalArgumentException("La quantité à retirer doit être positive et inférieure ou égale à la quantité actuelle.");
        }
        return this.quantity;
    }

    /**
     * Représentation textuelle du slot.
     *
     * @return Une chaîne indiquant l'item et sa quantité, par exemple "Potion x3".
     */
    @Override
    public String toString() {
        return item + "x" + quantity;
    }
}
