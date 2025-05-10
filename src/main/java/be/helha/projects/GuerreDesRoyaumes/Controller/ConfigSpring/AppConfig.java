package be.helha.projects.GuerreDesRoyaumes.Controller.ConfigSpring;

import be.helha.projects.GuerreDesRoyaumes.Model.Combat.Combat;
import be.helha.projects.GuerreDesRoyaumes.DAO.CombatDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.CombatDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.JoueurDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Coffre;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Guerrier;
import be.helha.projects.GuerreDesRoyaumes.Model.Royaume;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceCombat;
import be.helha.projects.GuerreDesRoyaumes.ServiceImpl.ServiceCombatImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class AppConfig {

    @Bean
    public ServiceCombat serviceCombat() {
        return new ServiceCombatImpl();
    }

    @Bean
    public CombatDAO combatDAO() throws SQLException {
        be.helha.projects.GuerreDesRoyaumes.Config.SQLConfigManager sqlManager = be.helha.projects.GuerreDesRoyaumes.Config.SQLConfigManager.getInstance();
        return new CombatDAOImpl(sqlManager.getConnection("sqlserver"));
    }

    @Bean
    public JoueurDAO joueurDAO() throws SQLException {
        JoueurDAOImpl joueurDAO = JoueurDAOImpl.getInstance();
        // Utilise ta classe de config personnalisée
        be.helha.projects.GuerreDesRoyaumes.Config.SQLConfigManager sqlManager = be.helha.projects.GuerreDesRoyaumes.Config.SQLConfigManager.getInstance();
        joueurDAO.setConnection(sqlManager.getConnection("sqlserver"));
        return joueurDAO;
    }

    @Bean
    public Combat combat(Joueur joueur1, Joueur joueur2) {
        return new Combat(0, joueur1, joueur2, null, 0, java.time.LocalDateTime.now());
    }

    @Bean
    public Joueur joueur1() {
        return new Joueur(001,"nom1","prenom1","pseudo1","1234",10000, new Royaume(001,"nomRoyaume1",15),new Guerrier(),new Coffre(),10, 15);
    }

    @Bean
    public Joueur joueur2() {
        return new Joueur(002,"nom2","prenom2","pseudo2","1234",10000, new Royaume(002,"nomRoyaume2",15),new Guerrier(),new Coffre(),19, 16);
    }
}
