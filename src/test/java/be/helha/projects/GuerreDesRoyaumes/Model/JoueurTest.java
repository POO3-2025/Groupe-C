package be.helha.projects.GuerreDesRoyaumes.Model;

import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Coffre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JoueurTest {

    private Joueur joueur;
    private final String pseudo = "TestUser";
    private final String email = "test@example.com";
    private final String motDePasse = "password123";

    @BeforeEach
    void setUp() {
        joueur = new Joueur();
        joueur.setPseudo(pseudo);
        joueur.setMotDePasse(motDePasse);
        joueur.setArgent(100);
        
        // Initialiser un royaume et un coffre pour le joueur
        Royaume royaume = new Royaume(0, "Royaume de " + pseudo, 1);
        Coffre coffre = new Coffre();
        
        joueur.setRoyaume(royaume);
        joueur.setCoffre(coffre);
    }

    @Test
    void testJoueurInitialization() {
        assertNotNull(joueur);
        assertEquals(pseudo, joueur.getPseudo());
        assertEquals(motDePasse, joueur.getMotDePasse());
        assertEquals(100, joueur.getArgent());
        assertNotNull(joueur.getRoyaume());
        assertNotNull(joueur.getCoffre());
    }

    @Test
    void testAjouterArgent() {
        int argentInitial = joueur.getArgent();
        int montantAjoute = 50;
        
        joueur.ajouterArgent(montantAjoute);
        
        assertEquals(argentInitial + montantAjoute, joueur.getArgent());
    }



    @Test
    void testSetVictoires() {
        joueur.setVictoires(5);
        assertEquals(5, joueur.getVictoires());
    }

    @Test
    void testSetDefaites() {
        joueur.setDefaites(3);
        assertEquals(3, joueur.getDefaites());
    }





    @Test
    void testEquals_MemeJoueur() {
        Joueur memeJoueur = joueur;
        assertEquals(joueur, memeJoueur);
    }



    @Test
    void testEquals_JoueursDifferents() {
        Joueur autreJoueur = new Joueur();
        autreJoueur.setPseudo("DifferentUser");
        
        assertNotEquals(joueur, autreJoueur);
    }
} 