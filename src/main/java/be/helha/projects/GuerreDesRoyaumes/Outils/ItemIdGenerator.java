package be.helha.projects.GuerreDesRoyaumes.Outils;

/**
 * Générateur simple d'identifiants uniques pour les items.
 * <p>
 * Cette classe fournit une méthode statique pour générer des IDs incrémentaux,
 * garantissant que chaque appel retourne un identifiant unique.
 * </p>
 */
public class ItemIdGenerator {

    private static int currentId = 0;

    /**
     * Génère un nouvel identifiant unique.
     *
     * @return Un entier représentant un nouvel ID unique.
     */
    public static int generateId() {
        currentId++;
        return currentId;
    }
}
