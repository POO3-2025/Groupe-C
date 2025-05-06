package be.helha.projects.GuerreDesRoyaumes.Model.Inventaire;

import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;

public class Slots {

    private Item item;
    private int quantity;

    //Constructeur
    public Slots(Item item, int quantity) {
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

    //Setteur
    public void setItem(Item item) {
        this.item = item;
    }
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

    @Override
    public String toString() {
        return item + "x" + quantity;
    }
}
