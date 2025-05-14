package be.helha.projects.GuerreDesRoyaumes.Model.Personnage;

import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;

public class Guerrier extends Personnage {
    public Guerrier() {
        super("Guerrier",100, 40, 20, new Inventaire());
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

    @Override
    public void subirDegats(double degatsSubis) {
        double degatsReels = degatsSubis / (100 / getResistance());
        //    double degatsReels = degatsSubis * (1 - (getResistance() / 100.0));
        setVie(getVie() - degatsReels);
        System.out.println(getNom() + " subit " + degatsReels + " points de dégâts. Vie restante : " + getVie());
    }

    @Override
    public void soigner(double pointsSoin) {
        setVie(getVie() + pointsSoin);
        System.out.println(getNom() + " se soigne de " + pointsSoin + " points de vie. Vie actuelle : " + getVie());
    }
}