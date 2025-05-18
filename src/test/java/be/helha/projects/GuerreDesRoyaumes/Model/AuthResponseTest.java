package be.helha.projects.GuerreDesRoyaumes.Model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthResponseTest {

    private final String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ";
    private final String pseudo = "utilisateur";
    private final int joueurId = 123;
    private final String message = "Authentification réussie";

    @Test
    void testConstructeurVide() {
        AuthResponse authResponse = new AuthResponse();
        
        assertNotNull(authResponse);
        assertNull(authResponse.getToken());
        assertEquals("Bearer", authResponse.getType());
        assertNull(authResponse.getPseudo());
        assertEquals(0, authResponse.getJoueurId());
        assertNull(authResponse.getMessage());
    }

    @Test
    void testConstructeurAvecParametres() {
        AuthResponse authResponse = new AuthResponse(token, pseudo, joueurId, message);
        
        assertNotNull(authResponse);
        assertEquals(token, authResponse.getToken());
        assertEquals("Bearer", authResponse.getType());
        assertEquals(pseudo, authResponse.getPseudo());
        assertEquals(joueurId, authResponse.getJoueurId());
        assertEquals(message, authResponse.getMessage());
    }

    @Test
    void testSetToken() {
        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(token);
        
        assertEquals(token, authResponse.getToken());
    }

    @Test
    void testSetType() {
        AuthResponse authResponse = new AuthResponse();
        String newType = "JWT";
        authResponse.setType(newType);
        
        assertEquals(newType, authResponse.getType());
    }

    @Test
    void testSetPseudo() {
        AuthResponse authResponse = new AuthResponse();
        authResponse.setPseudo(pseudo);
        
        assertEquals(pseudo, authResponse.getPseudo());
    }

    @Test
    void testSetJoueurId() {
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJoueurId(joueurId);
        
        assertEquals(joueurId, authResponse.getJoueurId());
    }

    @Test
    void testSetMessage() {
        AuthResponse authResponse = new AuthResponse();
        authResponse.setMessage(message);
        
        assertEquals(message, authResponse.getMessage());
    }

    @Test
    void testGetters() {
        AuthResponse authResponse = new AuthResponse(token, pseudo, joueurId, message);
        
        assertEquals(token, authResponse.getToken());
        assertEquals("Bearer", authResponse.getType());
        assertEquals(pseudo, authResponse.getPseudo());
        assertEquals(joueurId, authResponse.getJoueurId());
        assertEquals(message, authResponse.getMessage());
    }
} 