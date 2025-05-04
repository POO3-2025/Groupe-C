package be.helha.projects.GuerreDesRoyaumes.Model.Perssonnage;

import be.helha.projects.GuerreDesRoyaumes.Model.Competence;

public class Archer extends Personnage {
    public Archer() {
        super("Archer", "L'archer d'élite", 120, 20, 10,50, 500, new Competence("Sort de feu",
                "Lance une flèche enflammée qui inflige des dégâts élevés à l'ennemi.", 0, 15, 0, 0));
    }

    @Override
    public void attaquer() {
        System.out.println(getNom() + " lance une flèche de feu !");
    }
}
