package be.helha.projects.GuerreDesRoyaumes.Model;

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
}
