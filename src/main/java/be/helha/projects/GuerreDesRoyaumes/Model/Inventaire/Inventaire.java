package be.helha.projects.GuerreDesRoyaumes.Model.Inventaire;

import java.util.ArrayList;
import java.util.List;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Items;

public class Inventaire {
    private List<Slots> slots;
    private int maxSlots;

    public Inventaire(List<Slots> slots, int maxSlots) {
        this.maxSlots = 5;
        this.slots = new ArrayList<>();
        // Initialiser des slots vides
        for (int i = 0; i < maxSlots; i++) {
            slots.add(null); // slot vide
        }
    }

    public boolean ajouterItem(Items items, int quantite) {
        // Cherche un slot compatible (même item et quantité < max)
        for (int i = 0; i < slots.size(); i++) {
            Slots slot = slots.get(i);
            if (slot != null && slot.getItem().getId()) {
                int quantitePossible = items.getQuantiteMax() - slot.getQuantity();
                int aAjouter = Math.min(quantitePossible, quantite);
                if (aAjouter > 0) {
                    slot.add(aAjouter);
                    quantite -= aAjouter;
                    if (quantite == 0) return true;
                }
            }
        }

        // Ajoute dans un slot vide si possible
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i) == null) {
                int aMettre = Math.min(items.getQuantiteMax(), quantite);
                slots.set(i, new Slots(items, aMettre));
                quantite -= aMettre;
                if (quantite == 0) return true;
            }
        }
        System.out.println("Inventaire plein ou quantité trop élevée.");
        return false;
    }

    public void afficherInventaire() {
        System.out.println("Inventaire :");
        for (int i = 0; i < slots.size(); i++) {
            Slots slot = slots.get(i);
            if (slot == null) {
                System.out.println("Slot " + (i + 1) + " : vide");
            } else {
                System.out.println("Slot " + (i + 1) + " : " + slot.getItem().getNom() + " x" + slot.getQuantity());
            }
        }
    }
}
