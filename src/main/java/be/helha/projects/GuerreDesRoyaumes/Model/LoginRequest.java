package be.helha.projects.GuerreDesRoyaumes.Model;

/**
 * Classe représentant une demande de connexion dans l'application.
 * Cette classe encapsule les informations nécessaires pour authentifier un utilisateur.
 */
public class LoginRequest {
    private String pseudo;
    private String motDePasse;

    /**
     * Constructeur par défaut.
     * Requis pour la désérialisation JSON.
     */
    public LoginRequest() {
    }

    /**
     * Constructeur avec paramètres pour créer une demande de connexion.
     *
     * @param pseudo Le pseudo de l'utilisateur
     * @param motDePasse Le mot de passe de l'utilisateur
     */
    public LoginRequest(String pseudo, String motDePasse) {
        this.pseudo = pseudo;
        this.motDePasse = motDePasse;
    }

    /**
     * @return Le pseudo de l'utilisateur
     */
    public String getPseudo() {
        return pseudo;
    }

    /**
     * @param pseudo Le nouveau pseudo de l'utilisateur
     */
    public void setPseudo(String pseudo) {
        this.pseudo = pseudo;
    }

    /**
     * @return Le mot de passe de l'utilisateur
     */
    public String getMotDePasse() {
        return motDePasse;
    }

    /**
     * @param motDePasse Le nouveau mot de passe de l'utilisateur
     */
    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }
}