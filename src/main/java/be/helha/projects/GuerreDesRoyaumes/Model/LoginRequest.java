package be.helha.projects.GuerreDesRoyaumes.Model;

public class LoginRequest {
    private String pseudo;
    private String motDePasse;

    // Constructeur par défaut
    public LoginRequest() {
    }

    // Constructeur avec paramètres
    public LoginRequest(String pseudo, String motDePasse) {
        this.pseudo = pseudo;
        this.motDePasse = motDePasse;
    }

    // Getters et setters
    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }
}