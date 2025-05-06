package be.helha.projects.GuerreDesRoyaumes.Model.Inventaire;

import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import java.util.ArrayList;
import java.util.List;

public class Inventaire {

    private List<Slots> slots;   // Liste des slots d'inventaire
    private int maxSlots;        // Nombre maximum de slots dans l'inventaire

    // Constructeur
    public Inventaire() {
        this.maxSlots = 5;  // 5 slots pour l'inventaire
        this.slots = new ArrayList<>();
        // Initialisation des slots vides
        for (int i = 0; i < maxSlots; i++) {
            slots.add(null);  // Slot vide
        }
    }

    // Méthode pour ajouter un item dans l'inventaire
    public boolean ajouterItem(Item item, int quantite) {
// Vérifier si l'item existe déjà dans l'inventaire
        for (int i = 0; i < slots.size(); i++) {
            Slots slot = slots.get(i);
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
                slots.set(i, new Slots(item, aMettre));  // Ajouter un nouvel item dans le slot
                quantite -= aMettre;
                if (quantite == 0) return true;
            }
        }

        System.out.println("Inventaire plein ou quantité trop élevée.");
        return false;
    }

    // Méthode pour afficher l'inventaire
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
    // Méthode pour choisir un item pour l'utiliser dans un combat (par exemple, une arme)
    public void choisirItem(int index) {
        if (index >= 0 && index < slots.size()) {
            Slots slotChoisi = slots.get(index);
            if (slotChoisi != null) {
                Item itemChoisi = slotChoisi.getItem();
                System.out.println("Vous avez choisi : " + itemChoisi);
                // Appliquer l'effet de l'item choisi (exemple : augmenter l'attaque avec une arme)
                itemChoisi.use();
                slotChoisi.remove(1);  // Utilisation de l'item (réduction de la quantité)
            } else {
                System.out.println("Le slot choisi est vide.");
            }
        } else {
            System.out.println("Numéro de slot invalide.");
        }
    }
}
