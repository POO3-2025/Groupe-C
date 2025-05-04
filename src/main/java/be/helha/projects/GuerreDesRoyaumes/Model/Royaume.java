package be.helha.projects.GuerreDesRoyaumes.Model;

public class Royaume {

    private int id;
    private String nom;
    private int niveau;

    //Constructeur
    public Royaume(int id, String nom, int niveau) {
        this.id = id;
        this.nom = nom;
        this.niveau = niveau;
    }

    //Getteur
    public int getId() {
        return id;
    }
    public String getNom() {
        return nom;
    }
    public int getNiveau() {
        return niveau;
    }

    //Setteur
    public void setId(int id) {
        this.id = id;
    }
    public void setNom(String nom) {
        this.nom = nom;
    }
    public void setNiveau(int niveau) {
        this.niveau = niveau;
    }

    @Override
    public String toString() {
        return nom + " niveau " + niveau;
    }
}
