package be.helha.projects.GuerreDesRoyaumes.Service;

import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;

import java.util.List;

public interface ClassementService {
    List<Joueur> listerTopVictoires();
    List<Joueur> listerTopDefaites();
}
