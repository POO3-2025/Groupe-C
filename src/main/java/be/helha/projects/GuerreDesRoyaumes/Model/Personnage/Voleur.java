package be.helha.projects.GuerreDesRoyaumes.Model.Personnage;

public class Voleur extends Personnage {
    public Voleur() {
        super("Voleur",90, 15, 15);
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

// TODO Le voleur doit gagner 2x plus d'argent à la fin d'un combat