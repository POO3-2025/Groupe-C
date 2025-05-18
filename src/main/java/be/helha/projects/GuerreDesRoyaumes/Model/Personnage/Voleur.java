package be.helha.projects.GuerreDesRoyaumes.Model.Personnage;

import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;

/**
 * Classe représentant le personnage Voleur dans le jeu Guerre des Royaumes.
 * <p>
 * Le Voleur possède des caractéristiques spécifiques héritées de la classe Personnage.
 * Il dispose d'attaques standards et spéciales, d'une défense, et peut utiliser des objets.
 * </p>
 * <p>
 * Les dégâts subis sont réduits en fonction de sa résistance.
 * </p>
 */
public class Voleur extends Personnage {

    /**
     * Constructeur par défaut.
     * Initialise un Voleur avec une vie de 100, 0 dégâts, 10 de résistance et un inventaire vide.
     */
    public Voleur() {
        super("Voleur", 100, 0, 10, new Inventaire());
    }

    /**
     * Attaque standard du Voleur.
     * Affiche un message indiquant que le Voleur attaque.
     */
    @Override
    public void attaquer() {
        System.out.println(getNom() + " fait une attaque");
    }

    /**
     * Attaque spéciale du Voleur.
     * Affiche un message indiquant que le Voleur réalise une attaque spéciale.
     */
    @Override
    public void attaquerSpecial() {
        System.out.println(getNom() + " fait une attaque spécial !");
    }

    /**
     * Défense du Voleur.
     * Affiche un message indiquant que le Voleur se défend.
     */
    @Override
    public void defense() {
        System.out.println(getNom() + " se défend");
    }

    /**
     * Utilisation d'un objet par le Voleur.
     * Affiche un message indiquant l'utilisation d'un objet.
     */
    @Override
    public void UtilisationObjet() {
        System.out.println(getNom() + " utilise un objet");
    }

    /**
     * Le Voleur subit des dégâts réduits en fonction de sa résistance.
     * Calcule les dégâts réels subis et met à jour la vie restante.
     * Affiche les dégâts reçus et la vie restante.
     *
     * @param degatsSubis Dégâts initiaux subis
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
     * Le Voleur se soigne en augmentant sa vie.
     * Affiche les points de vie récupérés et la vie actuelle.
     *
     * @param pointsSoin Nombre de points de vie restaurés
     */
    @Override
    public void soigner(double pointsSoin) {
        setVie(getVie() + pointsSoin);
        System.out.println(getNom() + " se soigne de " + pointsSoin + " points de vie. Vie actuelle : " + getVie());
    }
}
