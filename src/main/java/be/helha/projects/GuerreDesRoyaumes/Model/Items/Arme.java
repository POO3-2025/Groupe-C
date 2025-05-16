package be.helha.projects.GuerreDesRoyaumes.Model.Items;

import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;
import be.helha.projects.GuerreDesRoyaumes.Outils.GsonObjectIdAdapter;
import com.google.gson.Gson;

public class Arme extends Item {

    private double degats;

    //Constructeur
    public Arme(int id, String nom, int quantiteMax, int prix, double degats) {
        super(id, nom, quantiteMax, "Arme", prix);
        this.degats = degats;
    }

    //Getteur
    public double getDegats() {
        return degats;
    }

    //Setteur
    public void setDegats(double degats) {
        this.degats = degats;
    }



    @Override
    public void use(Personnage personnage) {
        System.out.println(personnage.getNom() + " équipe l'arme " + getNom() + " qui ajoute " + degats + " dégâts.");
        personnage.setDegats(personnage.getDegats() + degats);
    }

//    @Override
//    public String toString() {
//        return getNom() + " – Dégâts : " + degats;
//    }

//    @Override
//    public String toString() {
//        Gson gson = GsonObjectIdAdapter.getGson();
//        String json = gson.toJson(this);
//        return "\"Sword\": " + json;
//    }

}
