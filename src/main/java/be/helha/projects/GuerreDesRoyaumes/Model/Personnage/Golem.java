package be.helha.projects.GuerreDesRoyaumes.Model.Personnage;

import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;

/**
 * Classe représentant le personnage Golem dans le jeu Guerre des Royaumes.
 * <p>
 * Le Golem est un personnage résistant avec une bonne défense et une vie supérieure.
 * Il possède des attaques standards et spéciales, ainsi que des capacités de défense et d'utilisation d'objets.
 * </p>
 * <p>
 * Les dégâts subis sont réduits en fonction de sa résistance (formule différente des autres personnages).
 * </p>
 */
public class Golem extends Personnage {

    /**
     * Constructeur par défaut.
     * Initialise un Golem avec une vie de 120, 18 points de dégâts, 25 de résistance, et un inventaire vide.
     */
    public Golem() {
        super("Golem", 120, 18, 25, new Inventaire());
    }

    /**
     * Attaque standard du Golem.
     * Affiche un message indiquant que le Golem attaque.
     */
    @Override
    public void attaquer() {
        System.out.println(getNom() + " fait une attaque");
    }

    /**
     * Attaque spéciale du Golem.
     * Affiche un message indiquant que le Golem réalise une attaque spéciale.
     */
    @Override
    public void attaquerSpecial() {
        System.out.println(getNom() + " fait une attaque spécial !");
    }

    /**
     * Défense du Golem.
     * Affiche un message indiquant que le Golem se défend.
     */
    @Override
    public void defense() {
        System.out.println(getNom() + " se défend");
    }

    /**
     * Utilisation d'un objet par le Golem.
     * Affiche un message indiquant que le Golem utilise un objet.
     */
    @Override
    public void UtilisationObjet() {
        System.out.println(getNom() + " utilise un objet");
    }

    /**
     * Le Golem subit des dégâts atténués selon la formule :
     * dégâts réels = dégâts subis * (1 - (résistance / 100))
     * Met à jour la vie restante du Golem.
     * Affiche les dégâts reçus et la vie restante.
     *
     * @param degatsSubis Dégâts initiaux subis.
     */
    @Override
    public void subirDegats(double degatsSubis) {
        double degatsReels = degatsSubis * (1 - (getResistance() / 100.0));
        setVie(getVie() - degatsReels);
        System.out.println(getNom() + " subit " + degatsReels + " points de dégâts. Vie restante : " + getVie());
    }

    /**
     * Le Golem se soigne en augmentant sa vie.
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
