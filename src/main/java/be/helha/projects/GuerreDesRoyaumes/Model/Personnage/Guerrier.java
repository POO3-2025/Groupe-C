package be.helha.projects.GuerreDesRoyaumes.Model.Personnage;

import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;

/**
 * Classe représentant le personnage Guerrier dans le jeu Guerre des Royaumes.
 * <p>
 * Le Guerrier est un personnage spécialisé dans les dégâts élevés.
 * Il possède des attaques standards et spéciales, ainsi que des capacités de défense et d'utilisation d'objets.
 * </p>
 * <p>
 * Les dégâts subis sont réduits en fonction de sa résistance.
 * </p>
 */
public class Guerrier extends Personnage {

    /**
     * Constructeur par défaut.
     * Initialise un Guerrier avec une vie de 100, 40 points de dégâts, 10 de résistance, et un inventaire vide.
     */
    public Guerrier() {
        super("Guerrier", 100, 40, 10, new Inventaire());
    }

    /**
     * Attaque standard du Guerrier.
     * Affiche un message indiquant que le Guerrier attaque.
     */
    @Override
    public void attaquer() {
        System.out.println(getNom() + " fait une attaque");
    }

    /**
     * Attaque spéciale du Guerrier.
     * Affiche un message indiquant que le Guerrier réalise une attaque spéciale.
     */
    @Override
    public void attaquerSpecial() {
        System.out.println(getNom() + " fait une attaque spécial !");
    }

    /**
     * Défense du Guerrier.
     * Affiche un message indiquant que le Guerrier se défend.
     */
    @Override
    public void defense() {
        System.out.println(getNom() + " se défend");
    }

    /**
     * Utilisation d'un objet par le Guerrier.
     * Affiche un message indiquant que le Guerrier utilise un objet.
     */
    @Override
    public void UtilisationObjet() {
        System.out.println(getNom() + " utilise un objet");
    }

    /**
     * Le Guerrier subit des dégâts réduits en fonction de sa résistance.
     * Calcule les dégâts réels subis et met à jour la vie restante.
     * Affiche les dégâts reçus et la vie restante.
     *
     * @param degatsSubis Dégâts initiaux subis.
     */
    @Override
    public void subirDegats(double degatsSubis) {
        double degatsReels = degatsSubis / (100 / getResistance());
        // Alternative possible :
        // double degatsReels = degatsSubis * (1 - (getResistance() / 100.0));
        setVie(getVie() - degatsReels);
        System.out.println(getNom() + " subit " + degatsReels + " points de dégâts. Vie restante : " + getVie());
    }

    /**
     * Le Guerrier se soigne en augmentant sa vie.
     * Affiche les points de vie restaurés et la vie actuelle.
     *
     * @param pointsSoin Points de vie à restaurer.
     */
    @Override
    public void soigner(double pointsSoin) {
        setVie(getVie() + pointsSoin);
        System.out.println(getNom() + " se soigne de " + pointsSoin + " points de vie. Vie actuelle : " + getVie());
    }
}
