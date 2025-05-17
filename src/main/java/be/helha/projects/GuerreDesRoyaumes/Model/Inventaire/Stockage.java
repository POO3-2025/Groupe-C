package be.helha.projects.GuerreDesRoyaumes.Model.Inventaire;

import be.helha.projects.GuerreDesRoyaumes.Model.Items.Arme;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Bouclier;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Potion;

import java.util.ArrayList;
import java.util.List;

public abstract class Stockage {

    private List<Slot> slots;
    private int maxSlots;

    // Constructeur
    public Stockage(int maxSlots) {
        this.maxSlots = maxSlots;
        this.slots = new ArrayList<>(maxSlots);
        // Initialiser les slots avec des valeurs null
        for (int i = 0; i < maxSlots; i++) {
            slots.add(null);
        }
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
        if (item == null || quantite <= 0) {
            System.out.println("Item invalide ou quantité incorrecte.");
            return false;
        }

        // S'assurer que la liste des slots est initialisée
        if (slots == null) {
            slots = new ArrayList<>(maxSlots);
            for (int i = 0; i < maxSlots; i++) {
                slots.add(null);
            }
        }

        // Vérifier si c'est une arme ou un bouclier (ne s'empile pas)
        boolean estArmeOuBouclier = (item instanceof Arme || item instanceof Bouclier);

        // Si c'est une potion, essayer d'ajouter à un slot existant avec le même item
        if (!estArmeOuBouclier) {
        for (int i = 0; i < slots.size(); i++) {
            Slot slot = slots.get(i);
            if (slot != null && slot.getItem() != null && slot.getItem().getId() == item.getId()) {
                int quantitePossible = item.getQuantiteMax() - slot.getQuantity();
                if (quantitePossible <= 0) {
                    continue; // Slot plein, essayer le suivant
                }
                int aAjouter = Math.min(quantitePossible, quantite);
                slot.add(aAjouter);  // Ajouter la quantité à ce slot
                quantite -= aAjouter;
                if (quantite == 0) return true;
                }
            }
        }

        // Pour le reste de la quantité ou si c'est une arme/bouclier, utiliser des slots vides
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i) == null) {
                if (estArmeOuBouclier) {
                    // Pour armes et boucliers, limiter à 1 par slot
                    slots.set(i, new Slot(item, 1));
                    quantite--;
                    if (quantite == 0) return true;
                } else {
                    // Pour les potions et autres items empilables
                int aMettre = Math.min(item.getQuantiteMax(), quantite);
                    slots.set(i, new Slot(item, aMettre));
                quantite -= aMettre;
                if (quantite == 0) return true;
                }
            }
        }

        // Si on arrive ici avec quantite > 0, c'est qu'on n'a pas pu tout stocker
        if (quantite > 0) {
            System.out.println("Impossible de stocker tous les items. " + quantite + " items n'ont pas pu être ajoutés.");
        return false;
        }

        return true;
    }

    // Méthode pour afficher le contenu du stockage
    public void afficherContenu() {
        StringBuilder sb = new StringBuilder("Contenu du stockage : \n");
        for (Slot slot : slots) {
            sb.append(slot != null ? slot.toString() : "Vide").append("\n");
        }
        System.out.println(sb.toString());
    }

    // Méthode pour retirer un item du stockage
    public boolean enleverItem(Item item, int quantite) {
        for (int i = 0; i < slots.size(); i++) {
            Slot slot = slots.get(i);
            if (slot != null && slot.getItem() != null && slot.getItem().getId() == item.getId()) {
                if (slot.getQuantity() >= quantite) {
                    slot.remove(quantite);
                    if (slot.getQuantity() == 0) {
                        slots.set(i, null); // Libérer le slot s'il est vide
                    }
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
