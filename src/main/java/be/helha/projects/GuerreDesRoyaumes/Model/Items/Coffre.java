package be.helha.projects.GuerreDesRoyaumes.Model.Items;

import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Slots;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Items;

import java.util.ArrayList;
import java.util.List;

/**
 * The type Coffre.
 */
public class Coffre extends Items {

    private List<Slots> slots;
    private int maxSlots;

    //Constructeur
    public Coffre(int id, String nom, int quantiteMax, int maxSlots, List<Slots> slots) {
        super(id, nom, quantiteMax);
        this.maxSlots = 10;
        this.slots = new ArrayList<>();
        // Initialiser des slots vides
        for (int i = 0; i < maxSlots; i++) {
            slots.add(null); // slot vide
        };
    }

    //Getteur
    public List<Slots> getSlots() {
        return slots;
    }
    public int getMaxSlots() {
        return maxSlots;
    }

    //Setteur
    public void setSlots(List<Slots> slots) {
        this.slots = slots;
    }
    public void setMaxSlots(int maxSlots) {
        this.maxSlots = maxSlots;
    }

    @Override
    public String toString() {
        return slots + "";
    }
}
