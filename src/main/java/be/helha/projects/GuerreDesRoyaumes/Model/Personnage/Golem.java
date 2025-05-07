package be.helha.projects.GuerreDesRoyaumes.Model.Personnage;

public class Golem extends Personnage {
    public Golem(String nom, int vie, int degats, int resistance) {
        super("Golem",120, 18, 50);
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