package be.helha.projects.GuerreDesRoyaumes.Model.Inventaire;

import be.helha.projects.GuerreDesRoyaumes.Model.Items.Arme;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Bouclier;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe abstraite représentant un stockage d'items dans le jeu Guerre des Royaumes.
 * <p>
 * Le stockage contient un nombre limité de slots,
 * chaque slot pouvant contenir un item et une quantité associée.
 * Les armes et boucliers ne peuvent pas s'empiler (un par slot),
 * tandis que les autres items (comme les potions) peuvent s'empiler jusqu'à une quantité maximale.
 * </p>
 * <p>
 * Cette classe fournit les fonctionnalités de base pour ajouter, retirer
 * et afficher des items dans le stockage.
 * </p>
 */
public abstract class Stockage {

    private List<Slot> slots;
    private int maxSlots;

    /**
     * Constructeur initialisant un stockage avec un nombre maximal de slots.
     * Chaque slot est initialisé à null (vide).
     *
     * @param maxSlots Nombre maximal de slots dans le stockage.
     */
    public Stockage(int maxSlots) {
        this.maxSlots = maxSlots;
        this.slots = new ArrayList<>(maxSlots);
        for (int i = 0; i < maxSlots; i++) {
            slots.add(null);
        }
    }

    // --- Getters ---

    /**
     * Obtient la liste des slots du stockage.
     *
     * @return Liste des slots, certains pouvant être null (vides).
     */
    public List<Slot> getSlots() {
        return slots;
    }

    /**
     * Obtient la capacité maximale (nombre de slots) du stockage.
     *
     * @return Nombre maximal de slots.
     */
    public int getMaxSlots() {
        return maxSlots;
    }

    // --- Setters ---

    /**
     * Définit la liste des slots du stockage.
     *
     * @param slots Nouvelle liste de slots.
     */
    public void setSlots(List<Slot> slots) {
        this.slots = slots;
    }

    /**
     * Définit la capacité maximale du stockage.
     *
     * @param maxSlots Nouveau nombre maximal de slots.
     */
    public void setMaxSlots(int maxSlots) {
        this.maxSlots = maxSlots;
    }

    /**
     * Ajoute une quantité donnée d'un item dans le stockage.
     * <p>
     * - Les armes et boucliers ne s'empilent pas (1 par slot).
     * - Les autres items (ex : potions) s'empilent jusqu'à leur quantité maximale par slot.
     * <p>
     * Si la quantité ne peut pas être entièrement ajoutée, affiche un message d'erreur.
     *
     * @param item     Item à ajouter.
     * @param quantite Quantité à ajouter (doit être > 0).
     * @return true si toute la quantité a été ajoutée, false sinon.
     */
    public boolean ajouterItem(Item item, int quantite) {
        if (item == null || quantite <= 0) {
            System.out.println("Item invalide ou quantité incorrecte.");
            return false;
        }

        if (slots == null) {
            slots = new ArrayList<>(maxSlots);
            for (int i = 0; i < maxSlots; i++) {
                slots.add(null);
            }
        }

        boolean estArmeOuBouclier = (item instanceof Arme || item instanceof Bouclier);

        if (!estArmeOuBouclier) {
            // Essayer d'empiler dans les slots existants
            for (int i = 0; i < slots.size(); i++) {
                Slot slot = slots.get(i);
                if (slot != null && slot.getItem() != null && slot.getItem().getId() == item.getId()) {
                    int quantitePossible = item.getQuantiteMax() - slot.getQuantity();
                    if (quantitePossible <= 0) {
                        continue; // Slot plein, passer au suivant
                    }
                    int aAjouter = Math.min(quantitePossible, quantite);
                    slot.add(aAjouter);
                    quantite -= aAjouter;
                    if (quantite == 0) return true;
                }
            }
        }

        // Utiliser des slots vides pour le reste
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i) == null) {
                if (estArmeOuBouclier) {
                    slots.set(i, new Slot(item, 1));
                    quantite--;
                    if (quantite == 0) return true;
                } else {
                    int aMettre = Math.min(item.getQuantiteMax(), quantite);
                    slots.set(i, new Slot(item, aMettre));
                    quantite -= aMettre;
                    if (quantite == 0) return true;
                }
            }
        }

        if (quantite > 0) {
            System.out.println("Impossible de stocker tous les items. " + quantite + " items n'ont pas pu être ajoutés.");
            return false;
        }
        return true;
    }

    /**
     * Affiche le contenu actuel du stockage, slot par slot.
     * Les slots vides sont indiqués comme "Vide".
     */
    public void afficherContenu() {
        StringBuilder sb = new StringBuilder("Contenu du stockage : \n");
        for (Slot slot : slots) {
            sb.append(slot != null ? slot.toString() : "Vide").append("\n");
        }
        System.out.println(sb.toString());
    }

    /**
     * Retire une quantité donnée d'un item du stockage.
     * <p>
     * Recherche le premier slot contenant l'item et retire la quantité demandée.
     * Si la quantité dans le slot devient zéro, libère le slot (le met à null).
     * <p>
     * Affiche un message d'erreur si la quantité est insuffisante ou si l'item n'est pas présent.
     *
     * @param item     Item à retirer.
     * @param quantite Quantité à retirer.
     * @return true si la quantité a été retirée avec succès, false sinon.
     */
    public boolean enleverItem(Item item, int quantite) {
        for (int i = 0; i < slots.size(); i++) {
            Slot slot = slots.get(i);
            if (slot != null && slot.getItem() != null && slot.getItem().getId() == item.getId()) {
                if (slot.getQuantity() >= quantite) {
                    slot.remove(quantite);
                    if (slot.getQuantity() == 0) {
                        slots.set(i, null);
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
