package be.helha.projects.GuerreDesRoyaumes.Controller.ConfigSpring;

import be.helha.projects.GuerreDesRoyaumes.Model.Combat;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.SQLException;

@Configuration
public class AppConfig {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setDriverClassName(driverClassName);
        return dataSource;
    }

    // Note: Les beans CombatDAO et JoueurDAO ne sont plus nécessaires
    // car ils sont maintenant gérés par Spring avec les annotations @Repository

    @Bean
    public Combat combat(Joueur joueur1, Joueur joueur2) {
        return new Combat(01, 2, true, joueur1);
    }

    @Bean
    public Joueur joueur1() {
        return new Joueur(001,"nom1","prenom1","pseudo1","1234",10000, new Royaume(001,"nomRoyaume1",15),new Guerrier(),new Coffre());
    }

    @Bean
    public Joueur joueur2() {
        return new Joueur(002,"nom2","prenom2","pseudo2","1234",10000, new Royaume(002,"nomRoyaume2",15),new Guerrier(),new Coffre());
    }
}
