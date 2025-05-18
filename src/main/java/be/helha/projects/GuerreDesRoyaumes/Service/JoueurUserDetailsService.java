package be.helha.projects.GuerreDesRoyaumes.Service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Interface définissant les services d'authentification des joueurs.
 * <p>
 * Fournit une méthode pour charger les détails d'un utilisateur
 * à partir de son pseudo, utilisée notamment par Spring Security.
 * </p>
 */
public interface JoueurUserDetailsService {

    /**
     * Charge un utilisateur par son pseudo.
     *
     * @param pseudo Le pseudo du joueur à charger.
     * @return Les détails de l'utilisateur sous forme d'un {@link UserDetails}.
     * @throws UsernameNotFoundException Si aucun joueur ne correspond au pseudo donné.
     */
    UserDetails loadUserByUsername(String pseudo) throws UsernameNotFoundException;
}
