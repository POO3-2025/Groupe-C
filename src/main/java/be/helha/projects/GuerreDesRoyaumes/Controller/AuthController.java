package be.helha.projects.GuerreDesRoyaumes.Controller;

import be.helha.projects.GuerreDesRoyaumes.Config.JwtUtils;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.JoueurDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.JoueurNotFoundException;
import be.helha.projects.GuerreDesRoyaumes.Model.AuthResponse;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Coffre;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Model.LoginRequest;
import be.helha.projects.GuerreDesRoyaumes.Model.Royaume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/joueurs")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final JoueurDAOImpl joueurDAO = JoueurDAOImpl.getInstance();

    @PostMapping
    public ResponseEntity<String> ajouterJoueur(@RequestBody Joueur joueur) {
        try {
            joueurDAO.ajouterJoueur(joueur);
            return ResponseEntity.ok("Joueur ajoutée avec succès");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de l'ajout de la joueur : " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<List<Joueur>> getAllJoueurs() {
        try {
            List<Joueur> joueurs = joueurDAO.obtenirTousLesJoueurs();
            if (joueurs.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(joueurs);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Joueur> obtenirJoueur(@PathVariable int id) {
        try {
            Joueur joueur = joueurDAO.obtenirJoueurParId(id);
            return ResponseEntity.ok(joueur);
        } catch (JoueurNotFoundException e) {
            return ResponseEntity.status(404).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> mettreAJourJoueur(@PathVariable int id, @RequestBody Joueur joueur) {
        try {
            joueur.setId(id);
            joueurDAO.mettreAJourJoueur(joueur);
            return ResponseEntity.ok("Joueur mise à jour avec succès");
        } catch (JoueurNotFoundException e) {
            return ResponseEntity.status(404).body("Erreur : Joueur avec l'ID " + id + " non trouvée.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de la mise à jour de la joueur : " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> supprimerJoueur(@PathVariable int id) {
        try {
            joueurDAO.supprimerJoueur(id);
            return ResponseEntity.ok("Joueur supprimée avec succès");
        } catch (JoueurNotFoundException e) {
            return ResponseEntity.status(404).body("Erreur : Joueur avec l'ID " + id + " non trouvée.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erreur lors de la suppression de la joueur : " + e.getMessage());
        }
    }

    @PostMapping("/connexion")
    public ResponseEntity<?> authentifierJoueur(@RequestBody LoginRequest loginRequest) {
        try {
            // Authentification avec Spring Security
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

            // Récupérer le joueur
            Joueur joueur = joueurDAO.obtenirJoueurParPseudo(loginRequest.getPseudo());

            // Créer et retourner la réponse
            AuthResponse authResponse = new AuthResponse(
                    jwt,
                    joueur.getPseudo(),
                    joueur.getId(),
                    "Authentification réussie"
            );

            return ResponseEntity.ok(authResponse);
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Échec de l'authentification: " + e.getMessage()));
        }
    }

    @PostMapping("/inscription")
    public ResponseEntity<?> inscrireJoueur(@RequestBody Joueur joueur) {
        try {
            // Vérifier si le pseudo existe déjà
            if (joueurDAO.obtenirJoueurParPseudo(joueur.getPseudo()) != null) {
                return ResponseEntity.badRequest().body(Map.of("message", "Ce pseudo est déjà utilisé!"));
            }

            // Créer un royaume par défaut pour le joueur
            Royaume royaume = new Royaume(0, "Royaume de " + joueur.getPseudo(), 1);

            // Créer un coffre vide pour le joueur
            Coffre coffre = new Coffre();

            // Encoder le mot de passe
            joueur.setMotDePasse(passwordEncoder.encode(joueur.getMotDePasse()));
            joueur.setRoyaume(royaume);
            joueur.setCoffre(coffre);
            joueur.setArgent(100); // Valeur par défaut

            // Sauvegarder le joueur
            joueurDAO.ajouterJoueur(joueur);

            return ResponseEntity.ok(Map.of("message", "Joueur inscrit avec succès!"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de l'inscription: " + e.getMessage()));
        }
    }
}
