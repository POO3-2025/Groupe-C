package be.helha.projects.GuerreDesRoyaumes.Controller;

import be.helha.projects.GuerreDesRoyaumes.Config.JwtUtils;
import be.helha.projects.GuerreDesRoyaumes.Model.AuthResponse;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Model.LoginRequest;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceAuthentification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class ControleurAuthentification {
    private static final Logger logger = LoggerFactory.getLogger(ControleurAuthentification.class);

    @Autowired
    private ServiceAuthentification serviceAuthentification;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/inscription")
    public ResponseEntity<?> inscription(@RequestBody Map<String, String> inscriptionRequest) {
        try {
            String nom = inscriptionRequest.get("nom");
            String prenom = inscriptionRequest.get("prenom");
            String pseudo = inscriptionRequest.get("pseudo");
            String motDePasse = inscriptionRequest.get("motDePasse");

            if (nom == null || prenom == null || pseudo == null || motDePasse == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Tous les champs sont obligatoires"));
            }

            serviceAuthentification.inscrireJoueur(nom, prenom, pseudo, motDePasse);

            return ResponseEntity.ok(Map.of("message", "Inscription réussie!"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Erreur lors de l'inscription: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de l'inscription: " + e.getMessage()));
        }
    }

    @PostMapping("/connexion")
    public ResponseEntity<?> connexion(@RequestBody LoginRequest loginRequest) {
        try {
            // Authentification via Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getPseudo(),
                            loginRequest.getMotDePasse()
                    )
            );

            // Mettre à jour le contexte de sécurité
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Générer le token JWT
            String jwt = jwtUtils.generateToken(authentication);

            // Créer et retourner la réponse
            AuthResponse authResponse = new AuthResponse(
                    jwt,
                    loginRequest.getPseudo(),
                    0, // L'ID sera rempli par le service
                    "Authentification réussie"
            );

            return ResponseEntity.ok(authResponse);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Échec de l'authentification: " + e.getMessage()));
        }
    }

    @PutMapping("/profil/{id}")
    public ResponseEntity<?> modifierProfil(@PathVariable int id, @RequestBody Map<String, String> profilRequest) {
        try {
            String pseudo = profilRequest.get("pseudo");
            String motDePasse = profilRequest.get("motDePasse");

            if (pseudo == null || motDePasse == null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Le pseudo et le mot de passe sont obligatoires"));
            }

            serviceAuthentification.gererProfil(id, pseudo, motDePasse);

            return ResponseEntity.ok(Map.of("message", "Profil mis à jour avec succès"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour du profil: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de la mise à jour du profil: " + e.getMessage()));
        }
    }

    @PostMapping("/personnage/{joueurId}/{personnageId}")
    public ResponseEntity<?> choisirPersonnage(@PathVariable int joueurId, @PathVariable int personnageId) {
        try {
            serviceAuthentification.choisirPersonnage(joueurId, personnageId);
            return ResponseEntity.ok(Map.of("message", "Personnage choisi avec succès"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Erreur lors du choix du personnage: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors du choix du personnage: " + e.getMessage()));
        }
    }
}