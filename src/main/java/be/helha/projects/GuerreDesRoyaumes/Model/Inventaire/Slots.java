package be.helha.projects.GuerreDesRoyaumes.Model.Inventaire;

import be.helha.projects.GuerreDesRoyaumes.Model.Items.Items;

public class Slots {

    private Items items;
    private int quantity;

    //Constructeur
    public Slots(Items items, int quantity) {
        this.items = items;
        this.quantity = quantity;
    }

    //Getteur
    public Items getItem() {
        return items;
    }
    public int getQuantity() {
        return quantity;
    }

    //Setteur
    public void setItem(Items items) {
        this.items = items;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int add(int quantity) {
        this.quantity += quantity;
        return this.quantity;
    }

    @Override
    public String toString() {
        return items + "x" + quantity;
    }
}
