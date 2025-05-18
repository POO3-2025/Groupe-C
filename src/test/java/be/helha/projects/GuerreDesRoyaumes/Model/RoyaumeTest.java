package be.helha.projects.GuerreDesRoyaumes.Model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RoyaumeTest {

    private Royaume royaume;
    private final int id = 1;
    private final String nom = "Royaume de Test";
    private final int niveau = 3;

    @BeforeEach
    void setUp() {
        royaume = new Royaume(id, nom, niveau);
    }

    @Test
    void testRoyaumeInitialization() {
        assertNotNull(royaume);
        assertEquals(id, royaume.getId());
        assertEquals(nom, royaume.getNom());
        assertEquals(niveau, royaume.getNiveau());
    }

    @Test
    void testSetId() {
        int newId = 2;
        royaume.setId(newId);
        assertEquals(newId, royaume.getId());
    }

    @Test
    void testSetNom() {
        String newNom = "Nouveau Royaume";
        royaume.setNom(newNom);
        assertEquals(newNom, royaume.getNom());
    }

    @Test
    void testSetNiveau() {
        int newNiveau = 5;
        royaume.setNiveau(newNiveau);
        assertEquals(newNiveau, royaume.getNiveau());
    }

    @Test
    void testIncrementNiveau() {
        int niveauInitial = royaume.getNiveau();
        royaume.incrementNiveau();
        assertEquals(niveauInitial + 1, royaume.getNiveau());
    }

    @Test
    void testEquals_MemeRoyaume() {
        Royaume memeRoyaume = royaume;
        assertEquals(royaume, memeRoyaume);
    }

    @Test
    void testEquals_RoyaumesDifferentsMemesProperties() {
        Royaume autreRoyaume = new Royaume(id, nom, niveau);
        assertEquals(royaume, autreRoyaume);
    }

    @Test
    void testEquals_RoyaumesDifferents() {
        Royaume autreRoyaume = new Royaume(2, "Autre Royaume", 1);
        assertNotEquals(royaume, autreRoyaume);
    }

    @Test
    void testHashCode() {
        Royaume autreRoyaume = new Royaume(id, nom, niveau);
        assertEquals(royaume.hashCode(), autreRoyaume.hashCode());
    }

    @Test
    void testToString() {
        String expected = nom + " niveau " + niveau;
        assertEquals(expected, royaume.toString());
    }
} 