package be.helha.projects.GuerreDesRoyaumes.Config;

import be.helha.projects.GuerreDesRoyaumes.Config.ConnexionConfig.ConfigInit;
import be.helha.projects.GuerreDesRoyaumes.Config.ConnexionConfig.ConnexionManager;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.ItemMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.MongoDBConnectionException;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.SQLConnectionException;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Arme;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Bouclier;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Potion;
import be.helha.projects.GuerreDesRoyaumes.Outils.ItemIdGenerator;
import com.mongodb.client.MongoDatabase;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InitialiserAPP {
    private static boolean isInitialized = false;
    
    // Méthode d'initialisation commune pour éviter la duplication
    private static synchronized void siNonInit() {
        if (!isInitialized) {
            ConfigInit.initAll();
            isInitialized = true;
        }
    }

    public static Connection getSQLConnexion() throws SQLConnectionException {
        try {
            siNonInit();
            Connection SQLDB = ConnexionManager.getInstance().getSQLConnection();

            verifySQLConnection(SQLDB); // Appel de la vérification

            return SQLDB;

        } catch (Exception e) {
            throw new SQLConnectionException("Échec de la connexion SQL: " + e.getMessage(), e);
        }
    }

    public static MongoDatabase getMongoConnexion() throws MongoDBConnectionException {
        try {
            siNonInit();
            MongoDatabase MongoDB = ConnexionManager.getInstance().getMongoDatabase();

            verifyMongoConnection(MongoDB); // Appel de la vérification

            return MongoDB;

        } catch (Exception e) {
            throw new MongoDBConnectionException("Échec de la connexion MongoDB: " + e.getMessage(), e);
        }
    }

    private static void verifySQLConnection(Connection sql) {
        if(sql == null)
            throw new IllegalStateException("Connexion SQL non initialisée");

        try {
            if(sql.isClosed() || !sql.isValid(2)) {
                throw new IllegalStateException("Connexion SQL invalide ou fermée");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erreur de validation SQL", e);
        }
    }

    private static void verifyMongoConnection(MongoDatabase mongo) {
        if(mongo == null)
            throw new IllegalStateException("Connexion MongoDB non initialisée");

        try {
            // Vérification par ping
            mongo.runCommand(new org.bson.Document("ping", 1));
        } catch (Exception e) {
            throw new IllegalStateException("Échec du ping MongoDB", e);
        }
    }
    
    // Pour les tests ou les cas où on veut réinitialiser
    public static void reset() {
        isInitialized = false;
        ConfigInit.reset();
    }

    // Méthode pour initialiser les items de la boutique
    public static void initialiserItemsBoutique() {
        // Initialisation centralisée des configs
        ConfigInit.initAll();

        ItemMongoDAOImpl itemMongoDAO = ItemMongoDAOImpl.getInstance();

        // Supprimer tous les items avant d'ajouter
        itemMongoDAO.supprimerTousLesItems();

        // ARMES //
        List<Arme> listeArme = new ArrayList<>();
        listeArme.add(new Arme(ItemIdGenerator.generateId(), "épée en bois", 1, 10, 10));
        listeArme.add(new Arme(ItemIdGenerator.generateId(), "épée en pierre", 1, 20, 20));
        listeArme.add(new Arme(ItemIdGenerator.generateId(), "épée en fer", 1, 30, 30));
        listeArme.add(new Arme(ItemIdGenerator.generateId(), "épée en or", 1, 40, 40));
        listeArme.add(new Arme(ItemIdGenerator.generateId(), "épée en diamant", 1, 50, 50));
        listeArme.add(new Arme(ItemIdGenerator.generateId(), "épée en netherite", 1, 60, 60));
        listeArme.add(new Arme(ItemIdGenerator.generateId(), "AK-47", 1, 1000, 5000));

        for (Arme arme : listeArme) {
            itemMongoDAO.ajouterItem(arme);
            System.out.println("Ajouté : " + arme.getNom());
        }

        // BOUCLIERS //
        List<Bouclier> listeBouclier = new ArrayList<>();
        listeBouclier.add(new Bouclier(ItemIdGenerator.generateId(), "bouclier en bois", 1, 10, 10));
        listeBouclier.add(new Bouclier(ItemIdGenerator.generateId(), "bouclier en pierre", 1, 20, 20));
        listeBouclier.add(new Bouclier(ItemIdGenerator.generateId(), "bouclier en fer", 1, 30, 30));
        listeBouclier.add(new Bouclier(ItemIdGenerator.generateId(), "bouclier en or", 1, 40, 40));
        listeBouclier.add(new Bouclier(ItemIdGenerator.generateId(), "bouclier en diamant", 1, 50, 50));
        listeBouclier.add(new Bouclier(ItemIdGenerator.generateId(), "bouclier en netherite", 1, 60, 60));
        listeBouclier.add(new Bouclier(ItemIdGenerator.generateId(), "bouclier captain-Americain", 1, 5000, 1000));

        for (Bouclier bouclier : listeBouclier) {
            itemMongoDAO.ajouterItem(bouclier);
            System.out.println("Ajouté : " + bouclier.getNom());
        }

        // POTIONS //
        List<Potion> listePotion = new ArrayList<>();
        listePotion.add(new Potion(ItemIdGenerator.generateId(), "petite potion de soin", 5, 10, 0, 10));
        listePotion.add(new Potion(ItemIdGenerator.generateId(), "moyen potion de soin", 3, 20, 0, 20));
        listePotion.add(new Potion(ItemIdGenerator.generateId(), "grande potion de soin", 1, 30, 0, 30));
        listePotion.add(new Potion(ItemIdGenerator.generateId(), "petite potion de dégats", 5, 10, 10, 0));
        listePotion.add(new Potion(ItemIdGenerator.generateId(), "moyen potion de dégats", 3, 20, 20, 0));
        listePotion.add(new Potion(ItemIdGenerator.generateId(), "grande potion de dégats", 1, 30, 30, 0));
        listePotion.add(new Potion(ItemIdGenerator.generateId(), "potion COCA-COLA", 1, 5000, 0, 1000));
        listePotion.add(new Potion(ItemIdGenerator.generateId(), "potion COVID-19", 1, 5000, 1000, 0));

        for (Potion potion : listePotion) {
            itemMongoDAO.ajouterItem(potion);
            System.out.println("Ajouté : " + potion.getNom());
        }
    }
}