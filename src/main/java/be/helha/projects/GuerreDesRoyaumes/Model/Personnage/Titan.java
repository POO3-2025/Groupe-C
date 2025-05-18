package be.helha.projects.GuerreDesRoyaumes.Model.Personnage;

import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;

/**
 * Classe représentant le personnage Titan dans le jeu Guerre des Royaumes.
 * <p>
 * Le Titan est un personnage avec une grande vie et une bonne résistance.
 * Il possède des attaques standards et spéciales, ainsi que des capacités de défense et d'utilisation d'objets.
 * </p>
 * <p>
 * Les dégâts subis sont atténués selon la résistance du Titan.
 * </p>
 */
public class Titan extends Personnage {

    /**
     * Constructeur par défaut.
     * Initialise un Titan avec une vie de 200, 25 points de dégâts, 15 de résistance, et un inventaire vide.
     */
    public Titan() {
        super("Titan", 200, 25, 15, new Inventaire());
    }

    /**
     * Attaque standard du Titan.
     * Affiche un message indiquant que le Titan attaque.
     */
    @Override
    public void attaquer() {
        System.out.println(getNom() + " fait une attaque");
    }

    /**
     * Attaque spéciale du Titan.
     * Affiche un message indiquant que le Titan réalise une attaque spéciale.
     */
    @Override
    public void attaquerSpecial() {
        System.out.println(getNom() + " fait une attaque spécial !");
    }

    /**
     * Défense du Titan.
     * Affiche un message indiquant que le Titan se défend.
     */
    @Override
    public void defense() {
        System.out.println(getNom() + " se défend");
    }

    /**
     * Utilisation d'un objet par le Titan.
     * Affiche un message indiquant que le Titan utilise un objet.
     */
    @Override
    public void UtilisationObjet() {
        System.out.println(getNom() + " utilise un objet");
    }

    /**
     * Le Titan subit des dégâts atténués en fonction de sa résistance.
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
     * Le Titan se soigne en augmentant sa vie.
     * Affiche les points de vie restaurés et la vie actuelle.
     *
     * @param pointsSoin Nombre de points de vie restaurés.
     */
    @Override
    public void soigner(double pointsSoin) {
        setVie(getVie() + pointsSoin);
        System.out.println(getNom() + " se soigne de " + pointsSoin + " points de vie. Vie actuelle : " + getVie());
    }
}
