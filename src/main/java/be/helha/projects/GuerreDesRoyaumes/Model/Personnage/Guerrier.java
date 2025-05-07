package be.helha.projects.GuerreDesRoyaumes.Model.Personnage;

public class Guerrier extends Personnage {
    public Guerrier(String nom, int vie, int degats, int resistance) {
        super("Guerrier",100, 40, 20);
    }

    @Override
    public void attaquer() {
        System.out.println(getNom() + " fait une attaque");
    }

    @Override
    public void attaquerSpecial() {
        System.out.println(getNom() + " fait une attaque spécial !");
    }

    @Override
    public void defense() {
        System.out.println(getNom() + " se défend");
    }

    @Override
    public void UtilisationObjet() {
        System.out.println(getNom() + " utilise un objet");
    }
}