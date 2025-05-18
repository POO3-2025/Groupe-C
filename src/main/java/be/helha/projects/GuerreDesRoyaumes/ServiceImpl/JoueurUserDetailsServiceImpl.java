package be.helha.projects.GuerreDesRoyaumes.ServiceImpl;

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

/**
 * Implémentation du service Spring Security {@link UserDetailsService}
 * pour la gestion de l'authentification des joueurs.
 * <p>
 * Cette classe gère la récupération des informations d'un joueur à partir de son pseudo,
 * et crée un objet {@link UserDetails} utilisé par Spring Security.
 * </p>
 * <p>
 * Un utilisateur spécial "sa" avec rôle ADMIN est géré en dur ici pour l'administration.
 * </p>
 */
@Service
public class JoueurUserDetailsServiceImpl implements UserDetailsService, JoueurUserDetailsService {

    private final JoueurDAO joueurDAO;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructeur avec injection des dépendances.
     *
     * @param joueurDAO       DAO pour accéder aux joueurs.
     * @param passwordEncoder Encodeur de mot de passe.
     */
    @Autowired
    public JoueurUserDetailsServiceImpl(JoueurDAO joueurDAO, PasswordEncoder passwordEncoder) {
        this.joueurDAO = joueurDAO;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Charge un utilisateur par son pseudo (nom d'utilisateur).
     * <p>
     * Si le pseudo est "sa", retourne un utilisateur ADMIN par défaut.
     * Sinon, recherche le joueur en base et crée un {@link UserDetails} avec le rôle ROLE_JOUEUR.
     * </p>
     *
     * @param pseudo Le pseudo du joueur.
     * @return Un objet {@link UserDetails} représentant l'utilisateur.
     * @throws UsernameNotFoundException Si aucun joueur n'est trouvé avec ce pseudo.
     */
    @Override
    public UserDetails loadUserByUsername(String pseudo) throws UsernameNotFoundException {
        if ("sa".equals(pseudo)) {
            List<SimpleGrantedAuthority> authorities = new ArrayList<>();
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

            return new User(
                    "sa",
                    passwordEncoder.encode("1234"),
                    authorities
            );
        }

        Joueur joueur = joueurDAO.obtenirJoueurParPseudo(pseudo);
        if (joueur == null) {
            throw new UsernameNotFoundException("Joueur non trouvé avec le pseudo: " + pseudo);
        }

        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_JOUEUR"));

        return new User(
                joueur.getPseudo(),
                joueur.getMotDePasse(),
                authorities
        );
    }
}
