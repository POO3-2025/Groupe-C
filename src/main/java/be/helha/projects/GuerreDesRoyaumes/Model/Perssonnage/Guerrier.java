package be.helha.projects.GuerreDesRoyaumes.Model.Perssonnage;

import be.helha.projects.GuerreDesRoyaumes.Model.Competence;

public class Guerrier extends Personnage {

    public Guerrier(String nom, String description) {
        // Appel du constructeur de la classe parent Personnage
        super(nom, description, 200, 60, 40, 30, 100, new Competence("Guerrier",
                "Fonce dans le combat et inflige de lourds dégâts à l'ennemi.", 20, 10, 5, 0));

        // Définir l'arme du Guerrier
        Item arme = new Item("Épée de base", "Une épée basique infligeant des dégâts modérés", 10, 4, 50, 1, "arme");
        getInventaire().ajouterItem(arme);  // Ajout de l'arme à l'inventaire
    }

    @Override
    public void attaquer() {
        System.out.println(getNom() + " attaque avec une épée !");
    }
}
