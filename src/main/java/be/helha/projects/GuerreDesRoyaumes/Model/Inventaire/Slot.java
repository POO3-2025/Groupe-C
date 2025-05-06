package be.helha.projects.GuerreDesRoyaumes.Model.Inventaire;

import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;

public class Slot {

    private Item item;
    private int quantity;

    //Constructeur
    public Slot(Item item, int quantity) {
        this.item = item;
        this.quantity = quantity;
    }

    //Getteur
    public Item getItem() {
        return item;
    }
    public int getQuantity() {
        return quantity;
    }

    // Setteur
    public void setQuantity(int quantity) {
        if (quantity >= 0) {  // Vérification de la quantité
            this.quantity = quantity;
        } else {
            throw new IllegalArgumentException("La quantité ne peut pas être négative.");
        }
    }

    // Ajouter des quantités d'items
    public int add(int quantityToAdd) {
        if (quantityToAdd > 0) {  // Ajout d'une validation pour une quantité positive
            this.quantity += quantityToAdd;
        } else {
            throw new IllegalArgumentException("La quantité à ajouter doit être positive.");
        }
        return this.quantity;
    }

    // Retirer des quantités d'items
    public int remove(int quantityToRemove) {
        if (quantityToRemove > 0 && quantityToRemove <= this.quantity) {  // Vérification de la quantité à retirer
            this.quantity -= quantityToRemove;
        } else {
            throw new IllegalArgumentException("La quantité à retirer doit être positive et inférieure ou égale à la quantité actuelle.");
        }
        return this.quantity;
    }
    // Méthode pour afficher le contenu du slot
    @Override
    public String toString() {
        return item + "x" + quantity;
    }
}
