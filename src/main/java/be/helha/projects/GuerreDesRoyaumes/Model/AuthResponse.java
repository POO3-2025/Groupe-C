
package be.helha.projects.GuerreDesRoyaumes.Model;

public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private String pseudo;
    private int joueurId;
    private String message;

    // Constructeur par défaut
    public AuthResponse() {
    }

    // Constructeur avec paramètres
    public AuthResponse(String token, String pseudo, int joueurId, String message) {
        this.token = token;
        this.pseudo = pseudo;
        this.joueurId = joueurId;
        this.message = message;
    }

    // Getters et setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPseudo() {
        return pseudo;
    }

    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    public int getJoueurId() {
        return joueurId;
    }

    public void setJoueurId(int joueurId) {
        this.joueurId = joueurId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
