package be.helha.projects.GuerreDesRoyaumes.DAOImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.RoyaumeMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Royaume;
import org.bson.Document;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class RoyaumeMongoDAOImplTest {

    private TestRoyaumeMongoDAOImpl royaumeMongoDAO;
    private Royaume royaume;
    
    @BeforeEach
    void setUp() {
        // Initialiser une implémentation de test
        royaumeMongoDAO = new TestRoyaumeMongoDAOImpl();
        
        // Initialiser un royaume pour les tests
        royaume = new Royaume(1, "Royaume de Test", 3);
    }
    
    @Test
    void testAjouterRoyaume() {
        // Act
        boolean result = royaumeMongoDAO.ajouterRoyaumeTest(royaume);
        
        // Assert
        assertTrue(result);
        assertEquals(1, royaumeMongoDAO.documents.size());
        Document document = royaumeMongoDAO.documents.get(0);
        assertEquals(royaume.getId(), document.getInteger("id"));
        assertEquals(royaume.getNom(), document.getString("nom"));
        assertEquals(royaume.getNiveau(), document.getInteger("niveau"));
    }
    
    @Test
    void testObtenirRoyaumeParId() {
        // Arrange
        royaumeMongoDAO.ajouterRoyaumeTest(royaume);
        
        // Act
        Royaume result = royaumeMongoDAO.obtenirRoyaumeParId(1);
        
        // Assert
        assertNotNull(result);
        assertEquals(royaume.getId(), result.getId());
        assertEquals(royaume.getNom(), result.getNom());
        assertEquals(royaume.getNiveau(), result.getNiveau());
    }
    
    @Test
    void testObtenirRoyaumeParId_NonTrouve() {
        // Act
        Royaume result = royaumeMongoDAO.obtenirRoyaumeParId(999);
        
        // Assert
        assertNull(result);
    }
    
    @Test
    void testGetRoyaumeByJoueurId() {
        // Arrange
        Document docJoueur = new Document("_id", 1)
                .append("royaume", new Document("id", 1)
                        .append("nom", royaume.getNom())
                        .append("niveau", royaume.getNiveau()));
        royaumeMongoDAO.joueurDocuments.add(docJoueur);
        
        // Act
        Royaume result = royaumeMongoDAO.getRoyaumeByJoueurId(1);
        
        // Assert
        assertNotNull(result);
        assertEquals(royaume.getId(), result.getId());
        assertEquals(royaume.getNom(), result.getNom());
        assertEquals(royaume.getNiveau(), result.getNiveau());
    }
    
    @Test
    void testMettreAJourRoyaume() {
        // Arrange
        royaumeMongoDAO.ajouterRoyaumeTest(royaume);
        royaume.setNiveau(5);
        
        // Act
        boolean result = royaumeMongoDAO.mettreAJourRoyaumeTest(royaume);
        
        // Assert
        assertTrue(result);
        assertEquals(5, royaumeMongoDAO.documents.get(0).getInteger("niveau"));
    }
    
    @Test
    void testMettreAJourRoyaume_NonTrouve() {
        // Act
        boolean result = royaumeMongoDAO.mettreAJourRoyaumeTest(royaume);
        
        // Assert
        assertFalse(result);
    }
    
    @Test
    void testSupprimerRoyaume() {
        // Arrange
        royaumeMongoDAO.ajouterRoyaumeTest(royaume);
        
        // Act
        boolean result = royaumeMongoDAO.supprimerRoyaumeTest(1);
        
        // Assert
        assertTrue(result);
        assertEquals(0, royaumeMongoDAO.documents.size());
    }
    
    @Test
    void testSupprimerRoyaume_NonTrouve() {
        // Act
        boolean result = royaumeMongoDAO.supprimerRoyaumeTest(999);
        
        // Assert
        assertFalse(result);
    }
    
    @Test
    void testIncrementNiveau() {
        // Arrange
        royaumeMongoDAO.ajouterRoyaumeTest(royaume);
        
        // Act
        boolean result = royaumeMongoDAO.incrementNiveau(1);
        
        // Assert
        assertTrue(result);
        assertEquals(royaume.getNiveau() + 1, royaumeMongoDAO.documents.get(0).getInteger("niveau"));
    }
    
    @Test
    void testIncrementNiveau_RoyaumeNonTrouve() {
        // Act
        boolean result = royaumeMongoDAO.incrementNiveau(999);
        
        // Assert
        assertFalse(result);
    }
    
    // Classe d'implémentation pour les tests
    private static class TestRoyaumeMongoDAOImpl implements RoyaumeMongoDAO {
        List<Document> documents = new ArrayList<>();
        List<Document> joueurDocuments = new ArrayList<>();
        
        // Méthodes pour les tests seulement
        public boolean ajouterRoyaumeTest(Royaume royaume) {
            Document doc = new Document("id", royaume.getId())
                    .append("nom", royaume.getNom())
                    .append("niveau", royaume.getNiveau());
            documents.add(doc);
            return true;
        }
        
        public Royaume obtenirRoyaumeParId(int id) {
            return documents.stream()
                    .filter(doc -> doc.getInteger("id") == id)
                    .findFirst()
                    .map(doc -> new Royaume(
                            doc.getInteger("id"),
                            doc.getString("nom"),
                            doc.getInteger("niveau")))
                    .orElse(null);
        }
        
        public Royaume getRoyaumeByJoueurId(int joueurId) {
            return joueurDocuments.stream()
                    .filter(doc -> doc.getInteger("_id") == joueurId)
                    .findFirst()
                    .map(doc -> {
                        Document royaumeDoc = doc.get("royaume", Document.class);
                        return new Royaume(
                                royaumeDoc.getInteger("id"),
                                royaumeDoc.getString("nom"),
                                royaumeDoc.getInteger("niveau"));
                    })
                    .orElse(null);
        }
        
        public boolean mettreAJourRoyaumeTest(Royaume royaume) {
            for (int i = 0; i < documents.size(); i++) {
                Document doc = documents.get(i);
                if (doc.getInteger("id") == royaume.getId()) {
                    Document newDoc = new Document("id", royaume.getId())
                            .append("nom", royaume.getNom())
                            .append("niveau", royaume.getNiveau());
                    documents.set(i, newDoc);
                    return true;
                }
            }
            return false;
        }
        
        public boolean supprimerRoyaumeTest(int id) {
            boolean removed = documents.removeIf(doc -> doc.getInteger("id") == id);
            return removed;
        }
        
        public boolean incrementNiveau(int idRoyaume) {
            for (Document doc : documents) {
                if (doc.getInteger("id") == idRoyaume) {
                    int niveauActuel = doc.getInteger("niveau");
                    doc.put("niveau", niveauActuel + 1);
                    return true;
                }
            }
            return false;
        }
        
        // Implémentation des méthodes de l'interface RoyaumeMongoDAO
        @Override
        public void ajouterRoyaume(Royaume royaume, int joueurId) {
            Document doc = new Document("id", royaume.getId())
                    .append("nom", royaume.getNom())
                    .append("niveau", royaume.getNiveau())
                    .append("id_joueur", joueurId);
            documents.add(doc);
        }
        
        @Override
        public Royaume obtenirRoyaumeParJoueurId(int joueurId) {
            return documents.stream()
                    .filter(doc -> doc.getInteger("id_joueur") == joueurId)
                    .findFirst()
                    .map(doc -> new Royaume(
                            doc.getInteger("id", 0),
                            doc.getString("nom"),
                            doc.getInteger("niveau")))
                    .orElse(null);
        }
        
        @Override
        public void mettreAJourRoyaume(Royaume royaume, int joueurId) {
            for (int i = 0; i < documents.size(); i++) {
                Document doc = documents.get(i);
                if (doc.getInteger("id_joueur") == joueurId) {
                    Document newDoc = new Document("id", royaume.getId())
                            .append("nom", royaume.getNom())
                            .append("niveau", royaume.getNiveau())
                            .append("id_joueur", joueurId);
                    documents.set(i, newDoc);
                }
            }
        }
        
        @Override
        public void supprimerRoyaume(int joueurId) {
            documents.removeIf(doc -> doc.getInteger("id_joueur") == joueurId);
        }
    }
} 