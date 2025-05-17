package be.helha.projects.GuerreDesRoyaumes.TUI;

import be.helha.projects.GuerreDesRoyaumes.Config.ConnexionConfig.ConfigInit;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.ItemMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Arme;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Bouclier;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Potion;
import be.helha.projects.GuerreDesRoyaumes.Outils.ItemIdGenerator;

import java.util.ArrayList;
import java.util.List;

public class TestAjoueItemMongo {

    public static void main(String[] args) {
        // Initialisation centralisée des configs
        ConfigInit.initAll();

        ItemMongoDAOImpl itemMongoDAO = ItemMongoDAOImpl.getInstance();

        // Supprimer tous les items de la base MongoDB avant de les ajouter
        itemMongoDAO.supprimerTousLesItems();


        // ARMES //

        List<Arme> listeArme = new ArrayList<>();
        // Ajouter des armes à la liste
        listeArme.add(new Arme(ItemIdGenerator.generateId(),"épée en bois", 1, 10, 10));
        listeArme.add(new Arme(ItemIdGenerator.generateId(),"épée en pierre", 1, 20, 20));
        listeArme.add(new Arme(ItemIdGenerator.generateId(),"épée en fer", 1, 30, 30));
        listeArme.add(new Arme(ItemIdGenerator.generateId(),"épée en or", 1, 40, 40));
        listeArme.add(new Arme(ItemIdGenerator.generateId(),"épée en diamant", 1, 50, 50));
        listeArme.add(new Arme(ItemIdGenerator.generateId(),"épée en netherite", 1, 60, 60));

        listeArme.add(new Arme(ItemIdGenerator.generateId(),"AK-47", 1, 1000, 5000));

        // Insérer chaque arme dans la base Mongo
        for (Arme arme : listeArme) {
            itemMongoDAO.ajouterItem(arme);
            System.out.println("Ajouté : " + arme.getNom());
        }



        // Boucliers //

        List<Bouclier> listeBouclier = new ArrayList<>();
        // Ajouter des boucliers à la liste
        listeBouclier.add(new Bouclier(ItemIdGenerator.generateId(),"bouclier en bois", 1, 10, 10));
        listeBouclier.add(new Bouclier(ItemIdGenerator.generateId(),"bouclier en pierre", 1, 20, 20));
        listeBouclier.add(new Bouclier(ItemIdGenerator.generateId(),"bouclier en fer", 1, 30, 30));
        listeBouclier.add(new Bouclier(ItemIdGenerator.generateId(),"bouclier en or", 1, 40, 40));
        listeBouclier.add(new Bouclier(ItemIdGenerator.generateId(),"bouclier en diamant", 1, 50, 50));
        listeBouclier.add(new Bouclier(ItemIdGenerator.generateId(),"bouclier en netherite", 1, 60, 60));

        listeBouclier.add(new Bouclier(ItemIdGenerator.generateId(),"bouclier captain-Americain", 1, 5000, 1000));

        // Insérer chaque bouclier dans la base Mongo
        for (Bouclier bouclier : listeBouclier) {
            itemMongoDAO.ajouterItem(bouclier);
            System.out.println("Ajouté : " + bouclier.getNom());
        }




        // Potions //

        List<Potion> listePotion = new ArrayList<>();
        // Ajouter des potions à la liste
        listePotion.add(new Potion(ItemIdGenerator.generateId(),"petite potion de soin", 5, 10, 0, 10));
        listePotion.add(new Potion(ItemIdGenerator.generateId(),"moyen potion de soin", 3, 20, 0, 20));
        listePotion.add(new Potion(ItemIdGenerator.generateId(),"grande potion de soin", 1, 30, 0, 30));
        listePotion.add(new Potion(ItemIdGenerator.generateId(),"petite potion de dégats", 5, 10, 10, 0));
        listePotion.add(new Potion(ItemIdGenerator.generateId(),"moyen potion de dégats", 3, 20, 20, 0));
        listePotion.add(new Potion(ItemIdGenerator.generateId(),"grande potion de dégats", 1, 30, 30, 0));

        listePotion.add(new Potion(ItemIdGenerator.generateId(),"potion COCA-COLA", 1, 5000, 0, 1000));
        listePotion.add(new Potion(ItemIdGenerator.generateId(),"potion COVID-19", 1, 5000, 1000, 0));

        // Insérer chaque potion dans la base Mongo
        for (Potion potion : listePotion) {
            itemMongoDAO.ajouterItem(potion);
            System.out.println("Ajouté : " + potion.getNom());
        }
    }
}
