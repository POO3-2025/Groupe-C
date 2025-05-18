package be.helha.projects.GuerreDesRoyaumes.Model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoginRequestTest {

    private final String pseudo = "utilisateur";
    private final String motDePasse = "mot_de_passe_secret";

    @Test
    void testConstructeurVide() {
        LoginRequest loginRequest = new LoginRequest();
        
        assertNotNull(loginRequest);
        assertNull(loginRequest.getPseudo());
        assertNull(loginRequest.getMotDePasse());
    }

    @Test
    void testConstructeurAvecParametres() {
        LoginRequest loginRequest = new LoginRequest(pseudo, motDePasse);
        
        assertNotNull(loginRequest);
        assertEquals(pseudo, loginRequest.getPseudo());
        assertEquals(motDePasse, loginRequest.getMotDePasse());
    }

    @Test
    void testSetPseudo() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPseudo(pseudo);
        
        assertEquals(pseudo, loginRequest.getPseudo());
    }

    @Test
    void testSetMotDePasse() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setMotDePasse(motDePasse);
        
        assertEquals(motDePasse, loginRequest.getMotDePasse());
    }

    @Test
    void testGetters() {
        LoginRequest loginRequest = new LoginRequest(pseudo, motDePasse);
        
        assertEquals(pseudo, loginRequest.getPseudo());
        assertEquals(motDePasse, loginRequest.getMotDePasse());
    }
} 