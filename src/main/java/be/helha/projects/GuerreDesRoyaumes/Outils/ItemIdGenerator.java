package be.helha.projects.GuerreDesRoyaumes.Outils;

public class ItemIdGenerator {
    private static int currentId = 0;

    public static int generateId() {
        currentId++;
        return currentId;
    }
}
