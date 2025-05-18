package be.helha.projects.GuerreDesRoyaumes.Model.Personnage;

import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GuerrierTest {

    private Guerrier guerrier;
    
    @BeforeEach
    void setUp() {
        guerrier = new Guerrier();
    }
    
    @Test
    void testConstructeur() {
        // Assert
        assertEquals("Guerrier", guerrier.getNom());
        assertEquals(100, guerrier.getVie());
        assertEquals(40, guerrier.getDegats());
        assertEquals(20, guerrier.getResistance());
        assertNotNull(guerrier.getInventaire());
    }
    
    @Test
    void testGettersSetters() {
        // Act
        guerrier.setNom("Guerrier Test");
        guerrier.setVie(150);
        guerrier.setDegats(50);
        guerrier.setResistance(25);
        Inventaire nouvelInventaire = new Inventaire();
        guerrier.setInventaire(nouvelInventaire);
        
        // Assert
        assertEquals("Guerrier Test", guerrier.getNom());
        assertEquals(150, guerrier.getVie());
        assertEquals(50, guerrier.getDegats());
        assertEquals(25, guerrier.getResistance());
        assertEquals(nouvelInventaire, guerrier.getInventaire());
    }
    
    @Test
    void testPointsDeVieAliases() {
        // La méthode getPointsDeVie est un alias pour getVie
        assertEquals(guerrier.getVie(), guerrier.getPointsDeVie());
        
        // Act
        guerrier.setPointsDeVie(80);
        
        // Assert
        assertEquals(80, guerrier.getVie());
        assertEquals(80, guerrier.getPointsDeVie());
    }
    
    @Test
    void testSubirDegats() {
        // Arrange
        double vieInitiale = guerrier.getVie();
        double degatsSubis = 50;
        double resistance = guerrier.getResistance(); // 20
        
        // Calcul attendu des dégâts réels selon la formule dans le code
        double degatsReels = degatsSubis / (100 / resistance); // 50 / (100 / 20) = 50 / 5 = 10
        
        // Act
        guerrier.subirDegats(degatsSubis);
        
        // Assert
        assertEquals(vieInitiale - degatsReels, guerrier.getVie(), 0.001);
    }
    
    @Test
    void testSoigner() {
        // Arrange
        guerrier.setVie(50);
        double vieAvantSoin = guerrier.getVie();
        double pointsSoin = 30;
        
        // Act
        guerrier.soigner(pointsSoin);
        
        // Assert
        assertEquals(vieAvantSoin + pointsSoin, guerrier.getVie());
    }
    
    @Test
    void testToString() {
        // Assert
        String expected = "Guerrier vie = 100.0 degats = 40.0 resistance = 20.0";
        assertEquals(expected, guerrier.toString());
    }
    
    @Test
    void testSubirDegatsMortels() {
        // Arrange
        guerrier.setVie(10);
        double degatsSubis = 100;
        
        // Act
        guerrier.subirDegats(degatsSubis);
        
        // Assert
        assertTrue(guerrier.getVie() <= 0, "Le guerrier devrait être mort (vie <= 0)");
    }
    
    @Test
    void testSoignerMaximum() {
        // Arrange
        guerrier.setVie(10);
        double pointsSoin = 1000;
        
        // Act
        guerrier.soigner(pointsSoin);
        
        // Assert
        assertEquals(1010, guerrier.getVie(), "Le soin devrait être appliqué sans limite");
    }
} 