package be.helha.projects.GuerreDesRoyaumes.Reseau;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Représente un message échangé entre l'hôte et le client pendant un combat
 */
public class MessageCombat implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Types de messages possibles
     */
    public enum Type {
        CONNEXION,
        INITIALISATION_COMBAT,
        ACTION,
        RESULTAT,
        FIN_COMBAT,
        ERREUR
    }

    private Type type;
    private String idMessage;
    private Map<String, Object> donnees;

    /**
     * Crée un nouveau message avec le type spécifié
     */
    public MessageCombat(Type type) {
        this.type = type;
        this.donnees = new HashMap<>();
    }

    /**
     * Ajoute une donnée au message
     */
    public void ajouterDonnee(String cle, Object valeur) {
        donnees.put(cle, valeur);
    }

    /**
     * Récupère une donnée du message
     */
    public Object obtenirDonnee(String cle) {
        return donnees.get(cle);
    }

    /**
     * Récupère une donnée du message sous forme de chaîne
     */
    public String obtenirDonneeString(String cle) {
        Object valeur = donnees.get(cle);
        return (valeur != null) ? valeur.toString() : null;
    }

    /**
     * Récupère une donnée du message sous forme d'entier
     */
    public Integer obtenirDonneeEntier(String cle) {
        Object valeur = donnees.get(cle);
        if (valeur instanceof Integer) {
            return (Integer) valeur;
        } else if (valeur instanceof String) {
            try {
                return Integer.parseInt((String) valeur);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Vérifie si une donnée existe dans le message
     */
    public boolean contientDonnee(String cle) {
        return donnees.containsKey(cle);
    }

    // Getters et setters

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getIdMessage() {
        return idMessage;
    }

    public void setIdMessage(String idMessage) {
        this.idMessage = idMessage;
    }

    public Map<String, Object> getDonnees() {
        return donnees;
    }

    @Override
    public String toString() {
        return "MessageCombat{type=" + type + ", idMessage='" + idMessage + "', donnees=" + donnees + '}';
    }
}