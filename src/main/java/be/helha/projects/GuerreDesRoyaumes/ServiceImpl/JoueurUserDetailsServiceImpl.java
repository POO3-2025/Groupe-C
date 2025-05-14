package be.helha.projects.GuerreDesRoyaumes.ServiceImpl;

import be.helha.projects.GuerreDesRoyaumes.DAOImpl.JoueurDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Service.JoueurUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class JoueurUserDetailsServiceImpl implements UserDetailsService, JoueurUserDetailsService {

    private final JoueurDAO joueurDAO;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public JoueurUserDetailsServiceImpl(JoueurDAO joueurDAO, PasswordEncoder passwordEncoder) {
        this.joueurDAO = joueurDAO;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String pseudo) throws UsernameNotFoundException {
        // Vérifier si c'est l'utilisateur par défaut "sa"
        if ("sa".equals(pseudo)) {
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

            return new User(
                    "sa",
                    passwordEncoder.encode("1234"), // Encode le mot de passe à la volée
                    authorities
            );
        }

        // Recherche du joueur par son pseudo dans la base de données
        Joueur joueur = joueurDAO.obtenirJoueurParPseudo(pseudo);

        if (joueur == null) {
            throw new UsernameNotFoundException("Joueur non trouvé avec le pseudo: " + pseudo);
        }

        // Création des autorités pour l'utilisateur
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_JOUEUR"));

        // Création d'un objet UserDetails avec les informations du joueur
        return new User(
                joueur.getPseudo(),
                joueur.getMotDePasse(),
                authorities
        );
    }
}