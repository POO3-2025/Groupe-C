package be.helha.projects.GuerreDesRoyaumes.Model.Combat;

import be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat.Competence;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import org.bson.types.ObjectId;

import java.util.List;

/**
 * Classe représentant une session de combat dans le jeu Guerre des Royaumes,
 * destinée à être stockée dans MongoDB.
 * <p>
 * Cette classe contient les informations des deux joueurs impliqués dans le combat,
 * leurs items et compétences utilisés, ainsi que leur état de préparation.
 * </p>
 */
public class CombatSession {

    /**
     * Identifiant unique de la session MongoDB.
     */
    private ObjectId id;

    /**
     * Identifiant du premier joueur participant à la session.
     */
    private int idJoueur1;

    /**
     * Identifiant du second joueur participant à la session.
     */
    private int idJoueur2;

    /**
     * Liste des items utilisés par le premier joueur durant la session.
     */
    private List<Item> itemsJoueur1;

    /**
     * Liste des items utilisés par le second joueur durant la session.
     */
    private List<Item> itemsJoueur2;

    /**
     * Liste des compétences utilisées par le premier joueur durant la session.
     */
    private List<Competence> competencesJoueur1;

    /**
     * Liste des compétences utilisées par le second joueur durant la session.
     */
    private List<Competence> competencesJoueur2;

    /**
     * Indique si le premier joueur est prêt à démarrer le combat.
     */
    private boolean pretJoueur1;

    /**
     * Indique si le second joueur est prêt à démarrer le combat.
     */
    private boolean pretJoueur2;

    // TODO : Ajouter getters/setters et constructeurs selon besoin
}
