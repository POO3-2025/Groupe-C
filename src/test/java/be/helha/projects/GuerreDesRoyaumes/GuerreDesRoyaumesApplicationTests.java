package be.helha.projects.GuerreDesRoyaumes;

import be.helha.projects.GuerreDesRoyaumes.Controller.AuthController;
import be.helha.projects.GuerreDesRoyaumes.Controller.BoutiqueController;
import be.helha.projects.GuerreDesRoyaumes.Controller.CombatController;
import be.helha.projects.GuerreDesRoyaumes.Controller.ControleurCoffre;
import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.CombatDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.JoueurDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.RoyaumeMongoDAOImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class GuerreDesRoyaumesApplicationTests {

	@Autowired
	private ApplicationContext context;

	/**
	 * Vérifie que l'application se charge correctement avec tous les beans nécessaires
	 */
	@Test
	void contextLoads() {
		// Vérifier que les contrôleurs principaux sont chargés
		assertNotNull(context.getBean(AuthController.class));
		assertNotNull(context.getBean(BoutiqueController.class));
		assertNotNull(context.getBean(CombatController.class));
		assertNotNull(context.getBean(ControleurCoffre.class));
		
		// Vérifier que les services principaux sont chargés
		assertNotNull(context.getBean(JoueurDAO.class));
	}
	
	/**
	 * Vérifie que les dépendances sont correctement injectées 
	 * et que l'application a les composants essentiels pour fonctionner
	 */
	@Test
	void applicationComponentsCheck() {
		// Vérifier la présence des implémentations DAO
		assertTrue(context.getBeanNamesForType(JoueurDAOImpl.class).length > 0);
		assertTrue(context.getBeanNamesForType(CombatDAOImpl.class).length > 0);
		assertTrue(context.getBeanNamesForType(RoyaumeMongoDAOImpl.class).length > 0);
	}

}
