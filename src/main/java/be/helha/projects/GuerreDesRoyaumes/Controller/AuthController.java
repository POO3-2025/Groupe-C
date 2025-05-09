package be.helha.projects.GuerreDesRoyaumes.Controller;

import be.helha.projects.GuerreDesRoyaumes.DAOImpl.JoueurDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.JoueurNotFoundException;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;


import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/joueurs")
public class AuthController {

    private JoueurDAOImpl joueurDAO = JoueurDAOImpl.getInstance();


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
}
