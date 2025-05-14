package be.helha.projects.GuerreDesRoyaumes.DAO;

import be.helha.projects.GuerreDesRoyaumes.Model.Combat.Combat;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;

import java.util.List;

public interface CombatDAO {
    void enregistrerCombat(Combat combat);
    void enregistrerVictoire(Joueur joueur);

    Combat obtenirCombatParId(int id);

    List<Combat> obtenirTousLesCombats();

    List<Combat> obtenirCombatsParJoueurId(int joueurId);

    void enregistrerDefaite(Joueur joueur);
    List<Joueur> getClassementParVictoires();
    List<Joueur> getClassementParDefaites();
}
