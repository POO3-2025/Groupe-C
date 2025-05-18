package be.helha.projects.GuerreDesRoyaumes.Model.Combat;

import be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat.Competence;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import org.bson.types.ObjectId;

import java.util.List;

//Integrer dans mongoDB
public class CombatSession {
    private ObjectId id;
    private int idJoueur1;
    private int idJoueur2;
    private List<Item> itemsJoueur1;
    private List<Item> itemsJoueur2;
    private List<Competence> competencesJoueur1;
    private List<Competence> competencesJoueur2;
    private boolean pretJoueur1;
    private boolean pretJoueur2;
}