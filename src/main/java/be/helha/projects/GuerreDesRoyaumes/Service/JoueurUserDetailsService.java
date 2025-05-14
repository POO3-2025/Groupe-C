package be.helha.projects.GuerreDesRoyaumes.Service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Interface définissant les services d'authentification des joueurs.
 */
public interface JoueurUserDetailsService {

    /**
     * Charge un utilisateur par son pseudo.
     *
     * @param pseudo Le pseudo du joueur à charger
     * @return Les détails de l'utilisateur
     * @throws UsernameNotFoundException Si le joueur n'est pas trouvé
     */
    UserDetails loadUserByUsername(String pseudo) throws UsernameNotFoundException;
}