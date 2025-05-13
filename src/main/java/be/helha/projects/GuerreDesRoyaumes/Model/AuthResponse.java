package be.helha.projects.GuerreDesRoyaumes.Model;

/**
 * Classe représentant la réponse d'authentification dans l'application.
 * Cette classe encapsule les informations renvoyées après une authentification réussie,
 * notamment le token JWT, le pseudo du joueur et son identifiant.
 */
public class AuthResponse {
    private String token;
    private String type = "Bearer";
    private String pseudo;
    private int joueurId;
    private String message;

    /**
     * Constructeur par défaut.
     * Requis pour la sérialisation JSON.
     */
    public AuthResponse() {
    }

    /**
     * Constructeur avec paramètres pour créer une réponse d'authentification.
     *
     * @param token Le token JWT généré pour l'authentification
     * @param pseudo Le pseudo du joueur authentifié
     * @param joueurId L'identifiant du joueur authentifié
     * @param message Un message explicatif sur le résultat de l'authentification
     */
    public AuthResponse(String token, String pseudo, int joueurId, String message) {
        this.token = token;
        this.pseudo = pseudo;
        this.joueurId = joueurId;
        this.message = message;
    }

    /**
     * @return Le token JWT
     */
    public String getToken() {
        return token;
    }

    /**
     * @param token Le nouveau token JWT
     */
    public void setToken(String token) {
        this.token = token;
    }

    /**
     * @return Le type de token (par défaut "Bearer")
     */
    public String getType() {
        return type;
    }

    /**
     * @param type Le nouveau type de token
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return Le pseudo du joueur authentifié
     */
    public String getPseudo() {
        return pseudo;
    }

    /**
     * @param pseudo Le nouveau pseudo du joueur
     */
    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    /**
     * @return L'identifiant du joueur authentifié
     */
    public int getJoueurId() {
        return joueurId;
    }

    /**
     * @param joueurId Le nouvel identifiant du joueur
     */
    public void setJoueurId(int joueurId) {
        this.joueurId = joueurId;
    }

    /**
     * @return Le message explicatif sur le résultat de l'authentification
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message Le nouveau message explicatif
     */
    public void setMessage(String message) {
        this.message = message;
    }
}
