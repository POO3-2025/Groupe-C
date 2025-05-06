package be.helha.projects.GuerreDesRoyaumes.Model.Items;

import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Slots;
import java.util.ArrayList;
import java.util.List;


public class Coffre {

    private List<Slots> slots;   // Liste des slots dans le coffre
    private int maxSlots;        // Nombre maximum de slots dans le coffre

    // Constructeur
    public Coffre() {
        this.maxSlots = 15;  // 15 slots pour le coffre
        this.slots = new ArrayList<>();
        // Initialisation des slots vides
        for (int i = 0; i < maxSlots; i++) {
            slots.add(null); // Slot vide
        }
    }

    // Getters
    public List<Slots> getSlots() {
        return slots;
    }

    public int getMaxSlots() {
        return maxSlots;
    }

    // Setters
    public void setSlots(List<Slots> slots) {
        this.slots = slots;
    }

    public void setMaxSlots(int maxSlots) {
        this.maxSlots = maxSlots;
    }

    // Méthode pour afficher le contenu du coffre
    public void afficherContenu() {
        StringBuilder sb = new StringBuilder("Contenu du coffre : \n");
        for (int i = 0; i < slots.size(); i++) {
            Slots slot = slots.get(i);
            if (slot != null) {
                sb.append("Slot ").append(i + 1).append(": ").append(slot.getItem().getNom())
                        .append(" x").append(slot.getQuantity()).append("\n");
            } else {
                sb.append("Slot ").append(i + 1).append(": vide\n");
            }
        }
        System.out.println(sb.toString());
    }

    // Ajout d'un item dans un slot spécifique du coffre
    public boolean ajouterItem(Item item, int quantite) {
        for (int i = 0; i < slots.size(); i++) {
            Slots slot = slots.get(i);
            if (slot != null && slot.getItem().getId() == item.getId()) {
                int quantitePossible = item.getQuantiteMax() - slot.getQuantity();
                int aAjouter = Math.min(quantitePossible, quantite);
                if (aAjouter > 0) {
                    slot.add(aAjouter);
                    quantite -= aAjouter;
                    if (quantite == 0) return true;
                }
            }
        }

        // Si on n'a pas pu ajouter l'item, chercher un slot vide
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i) == null) {
                int aMettre = Math.min(item.getQuantiteMax(), quantite);
                slots.set(i, new Slots(item, aMettre));
                quantite -= aMettre;
                if (quantite == 0) return true;
            }
        }

        System.out.println("Le coffre est plein ou la quantité dépasse la capacité.");
        return false;
    }

    // Retirer un item du coffre
    public boolean enleverItem(Item item, int quantite) {
        for (int i = 0; i < slots.size(); i++) {
            Slots slot = slots.get(i);
            if (slot != null && slot.getItem().getId() == item.getId()) {
                if (slot.getQuantity() >= quantite) {
                    slot.setQuantity(slot.getQuantity() - quantite);
                    if (slot.getQuantity() == 0) {
                        slots.set(i, null);
                    }
                    return true;
                } else {
                    System.out.println("Pas assez d'items dans le coffre.");
                    return false;
                }
            }
        }

        System.out.println("L'item n'est pas présent dans le coffre.");
        return false;
    }
}
