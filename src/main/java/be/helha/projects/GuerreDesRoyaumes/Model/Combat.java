package be.helha.projects.GuerreDesRoyaumes.Model;

import java.util.Scanner;

public class Combat {
    private int id;
    private int nbrTour;
    private boolean victoire;
    private Joueur joueur;

    //Constructeur
    public Combat(int id, int nbrTour, boolean victoire, Joueur joueur) {
        this.id = id;
        this.nbrTour = nbrTour;
        this.victoire = victoire;
        this.joueur = joueur;
    }

    //Getteur
    public int getId() {
        return id;
    }
    public int getNbrTour() {
        return nbrTour;
    }
    public boolean isVictoire() {
        return victoire;
    }
    public Joueur getJoueur() {
        return joueur;
    }

    //Setteur
    public void setId(int id) {
        this.id = id;
    }
    public void setNbrTour(int nbrTour) {
        this.nbrTour = nbrTour;
    }
    public void setVictoire(boolean victoire) {
        this.victoire = victoire;
    }
    public void setJoueur(Joueur joueur) {
        this.joueur = joueur;
    }

    // Méthode pour gérer un tour de combat
    public void commencerTour() {
        // Demander au joueur de choisir une compétence avant chaque tour
        joueur.choisirCompetenceAvantCombat();

        // Logique du tour (attaque/défense)
        // Par exemple, joueur attaque, puis l'autre joueur se défend
        // Ici, tu peux ajouter plus de logique pour simuler le combat, en fonction de ce que tu veux.
        System.out.println("Le tour " + (nbrTour + 1) + " commence !");
        // Exemple d'actions pendant le tour
        // (tu peux ajouter ici des actions comme attaquer, se défendre, etc.)

        // Fin du tour, on incrémente le nombre de tours
        nbrTour++;

        // Condition de victoire ou autre logique de fin de combat
        // Tu peux ici vérifier si le combat est terminé, ou si un des joueurs a gagné
        // Par exemple, si la vie du personnage est inférieure ou égale à zéro, il perd
    }

    // Méthode pour commencer le combat et gérer les tours
    public void commencerCombat() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            commencerTour();  // Commencer chaque tour

            // Logique de fin de combat ou condition pour arrêter le jeu
            System.out.println("Voulez-vous continuer le combat ? (oui/non)");
            String reponse = scanner.nextLine();
            if (reponse.equalsIgnoreCase("non")) {
                System.out.println("Combat terminé !");
                break;
            }
        }
    }
}
