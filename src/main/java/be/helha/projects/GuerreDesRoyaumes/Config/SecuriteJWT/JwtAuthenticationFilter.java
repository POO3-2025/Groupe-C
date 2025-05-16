package be.helha.projects.GuerreDesRoyaumes.Config.SecuriteJWT;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    public JwtAuthenticationFilter(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // Extraction du token depuis la requête
        String jwtToken = jwtUtils.extractJwtFromRequest(request);

        // Validation du token et ajout de l'authentification au contexte si valide
        if (jwtToken != null && jwtUtils.validateJwtToken(jwtToken)) {
            String pseudo = jwtUtils.getUsernameFromJwtToken(jwtToken);

            // Récupération des rôles depuis le token
            List<SimpleGrantedAuthority> authorities = jwtUtils.getRolesFromJwtToken(jwtToken).stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());

            if (pseudo != null) {
                // Création d'une authentification basée sur le token
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        pseudo, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Mise à jour du contexte de sécurité
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        // Passer au filtre suivant dans la chaîne
        chain.doFilter(request, response);
    }
}