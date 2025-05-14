package be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat;

import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;

public interface AppliquerEffet {
    void appliquerEffet(Personnage personnage);  // Chaque compétence doit implémenter cette méthode pour affecter l'effet
}
