package be.helha.projects.GuerreDesRoyaumes.Model.Perssonnage;

public class Titan extends Personnage {
    public Titan() {
        super("Titan",200, 25, 30);
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