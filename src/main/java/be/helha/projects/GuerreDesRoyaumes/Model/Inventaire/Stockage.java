package be.helha.projects.GuerreDesRoyaumes.Model.Inventaire;

import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;

import java.util.ArrayList;
import java.util.List;

public abstract class Stockage {

    private List<Slot> slots;  // Liste des slots dans le stockage
    private int maxSlots;      // Nombre maximum de slots dans le stockage

    // Constructeur
    public Stockage(int maxSlots) {
        this.maxSlots = maxSlots;
        this.slots = new ArrayList<>();
    }

    // Getters
    public List<Slot> getSlots() {
        return slots;
    }
    public int getMaxSlots() {
        return maxSlots;
    }

    // Setters
    public void setSlots(List<Slot> slots) {
        this.slots = slots;
    }
    public void setMaxSlots(int maxSlots) {
        this.maxSlots = maxSlots;
    }


    // Méthode pour ajouter un item dans le stockage
    public boolean ajouterItem(Item item, int quantite) {
        // Vérifier si l'item existe déjà dans le stockage
        for (Slot slot : slots) {
            if (slot != null && slot.getItem().getId() == item.getId()) {
                int quantitePossible = item.getQuantiteMax() - slot.getQuantity();
                int aAjouter = Math.min(quantitePossible, quantite);
                if (aAjouter > 0) {
                    slot.add(aAjouter);  // Ajouter la quantité à ce slot
                    quantite -= aAjouter;
                    if (quantite == 0) return true;
                }
            }
        }

        // Si l'item n'est pas trouvé, ajouter dans un slot vide
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i) == null) {
                int aMettre = Math.min(item.getQuantiteMax(), quantite);
                slots.set(i, new Slot(item, aMettre));  // Ajouter un nouvel item dans le slot
                quantite -= aMettre;
                if (quantite == 0) return true;
            }
        }

        System.out.println("Stockage plein ou quantité trop élevée.");
        return false;
    }

    // Méthode pour afficher le contenu du stockage
    public void afficherContenu() {
        StringBuilder sb = new StringBuilder("Contenu du stockage : \n");
        for (Slot slot : slots) {
            sb.append(slot).append("\n");
        }
        System.out.println(sb.toString());
    }

    // Méthode pour retirer un item du stockage
    public boolean enleverItem(Item item, int quantite) {
        for (Slot slot : slots) {
            if (slot.getItem().getId() == item.getId()) {
                if (slot.getQuantity() >= quantite) {
                    slot.remove(quantite);
                    return true;
                } else {
                    System.out.println("Pas assez d'items dans le stockage.");
                    return false;
                }
            }
        }

        System.out.println("L'item n'est pas présent dans le stockage.");
        return false;
    }
}
