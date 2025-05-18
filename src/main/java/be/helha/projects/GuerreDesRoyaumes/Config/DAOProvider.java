package be.helha.projects.GuerreDesRoyaumes.Config;

import be.helha.projects.GuerreDesRoyaumes.DAO.CoffreMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.CombatSessionMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.PersonnageMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.RoyaumeMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.CoffreMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.CombatSessionMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.PersonnageMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.RoyaumeMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.MongoDBConnectionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Fournisseur de DAO pour assurer la compatibilité entre le code ancien et le nouveau code Spring.
 * Cette classe sert de pont entre l'ancien pattern Singleton et le nouveau système d'injection de dépendances.
 */
@Component
public class DAOProvider {
    
    private static ApplicationContext context;
    private static CombatSessionMongoDAO combatSessionMongoDAOInstance;
    private static RoyaumeMongoDAO royaumeMongoDAOInstance;
    private static PersonnageMongoDAO personnageMongoDAOInstance;
    private static CoffreMongoDAO coffreMongoDAOInstance;
    
    @Autowired
    public DAOProvider(ApplicationContext context) {
        DAOProvider.context = context;
    }
    
    /**
     * Obtient l'instance du CombatSessionMongoDAO, soit depuis le contexte Spring 
     * soit en créant une nouvelle instance si le contexte n'est pas disponible.
     * 
     * @return L'instance de CombatSessionMongoDAO
     * @throws MongoDBConnectionException Si une erreur de connexion à MongoDB se produit
     */
    public static synchronized CombatSessionMongoDAO getCombatSessionMongoDAO() throws MongoDBConnectionException {
        if (context != null) {
            return context.getBean(CombatSessionMongoDAO.class);
        }
        
        if (combatSessionMongoDAOInstance == null) {
            combatSessionMongoDAOInstance = new CombatSessionMongoDAOImpl();
        }
        
        return combatSessionMongoDAOInstance;
    }

    /**
     * Obtient l'instance du RoyaumeMongoDAO, soit depuis le contexte Spring 
     * soit en créant une nouvelle instance si le contexte n'est pas disponible.
     * 
     * @return L'instance de RoyaumeMongoDAO
     */
    public static synchronized RoyaumeMongoDAO getRoyaumeMongoDAO() {
        if (context != null) {
            return context.getBean(RoyaumeMongoDAO.class);
        }
        
        if (royaumeMongoDAOInstance == null) {
            royaumeMongoDAOInstance = new RoyaumeMongoDAOImpl();
        }
        
        return royaumeMongoDAOInstance;
    }
    
    /**
     * Obtient l'instance du PersonnageMongoDAO, soit depuis le contexte Spring
     * soit en créant une nouvelle instance si le contexte n'est pas disponible.
     *
     * @return L'instance de PersonnageMongoDAO
     */
    public static synchronized PersonnageMongoDAO getPersonnageMongoDAO() {
        if (context != null) {
            return context.getBean(PersonnageMongoDAO.class);
        }
        
        if (personnageMongoDAOInstance == null) {
            personnageMongoDAOInstance = new PersonnageMongoDAOImpl();
        }
        
        return personnageMongoDAOInstance;
    }
    
    /**
     * Obtient l'instance du CoffreMongoDAO, soit depuis le contexte Spring
     * soit en créant une nouvelle instance si le contexte n'est pas disponible.
     *
     * @return L'instance de CoffreMongoDAO
     * @throws MongoDBConnectionException Si une erreur de connexion à MongoDB se produit
     */
    public static synchronized CoffreMongoDAO getCoffreMongoDAO() throws MongoDBConnectionException {
        if (context != null) {
            return context.getBean(CoffreMongoDAO.class);
        }
        
        if (coffreMongoDAOInstance == null) {
            coffreMongoDAOInstance = new CoffreMongoDAOImpl();
        }
        
        return coffreMongoDAOInstance;
    }
} 