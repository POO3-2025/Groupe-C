package be.helha.projects.GuerreDesRoyaumes.Outils;

public class Ressource {
}


/*
package be.helha.labos.crystalclash.Object;

import java.util.ArrayList;
import java.util.List;

public class BackPack {
    private final int CAPACITE_MAX = 5;
    private List<ObjectBase> objets;

    public BackPack() {
        this.objets = new ArrayList<>();
    }

    public boolean AddObjects(ObjectBase objet) {
        if (objets.size() >= CAPACITE_MAX) return false;
        objets.add(objet);
        return true;
    }

    public boolean removeObject(ObjectBase objet) {
        return objets.remove(objet);
    }

    public List<ObjectBase> getObjets() {
        return objets;
    }

    public int getCapaciteMax() {
        return CAPACITE_MAX;
    }
}*/

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

 /*package be.helha.labos.crystalclash.Object;

 import java.util.ArrayList;
 import java.util.List;

 public class CoffreDesJoyaux extends Item {
 private List<Item> contenu;
 private final int CAPACITE_MAX = 10;

 public CoffreDesJoyaux() {
 super("Coffre des Joyaux", 25, 1, 1);


 this.contenu = new ArrayList<>();
 }

 @Override
 public String use() {
 if (!IsUsed()) return "The chest has already been opened.";
 Reducereliability();
 return "You open the Coffre des Joyaux Chest and discover" + contenu.size() + " objets !";
 }

 public boolean AddObjects(Item object) {
 if (contenu.size() >= CAPACITE_MAX) return false;
 contenu.add(object);
 return true;
 }


 public List<Item> getContenu() {
 return contenu;
 }

 public int getMaxCapacity() {
 return CAPACITE_MAX;
 }
 public int setCapaciteMax(int maxCapacity) {
 return this.CAPACITE_MAX;
 }

 public void setContenu(List<Item> contenu) {
 this.contenu = contenu;
 }

 }*/