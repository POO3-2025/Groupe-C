package be.helha.projects.GuerreDesRoyaumes.Controller;

import be.helha.projects.GuerreDesRoyaumes.DAO.ItemMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.ItemMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.JoueurDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceBoutique;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/boutique")
public class BoutiqueController {

    private ItemMongoDAO itemMongoDAO;
    private ServiceBoutique serviceBoutique;
    private JoueurDAOImpl joueurDAO;

    @Autowired
    public BoutiqueController(ServiceBoutique serviceBoutique) {
        this.itemMongoDAO = ItemMongoDAOImpl.getInstance();
        this.joueurDAO = JoueurDAOImpl.getInstance();
        this.serviceBoutique = serviceBoutique;
    }
    
    // Ajout des setters pour les tests unitaires
    public void setItemMongoDAO(ItemMongoDAO itemMongoDAO) {
        this.itemMongoDAO = itemMongoDAO;
    }
    
    public void setJoueurDAO(JoueurDAOImpl joueurDAO) {
        this.joueurDAO = joueurDAO;
    }
    
    public void setServiceBoutique(ServiceBoutique serviceBoutique) {
        this.serviceBoutique = serviceBoutique;
    }

    /**
     * Récupère tous les items disponibles dans la boutique
     * @return Liste des items
     */
    @GetMapping("/items")
    public ResponseEntity<List<Item>> obtenirTousLesItems() {
        try {
            List<Item> items = itemMongoDAO.obtenirTousLesItems();
            if (items.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupère les items d'un type spécifique
     * @param type Le type d'item (arme, bouclier, potion, etc.)
     * @return Liste des items du type spécifié
     */
    @GetMapping("/items/type/{type}")
    public ResponseEntity<List<Item>> obtenirItemsParType(@PathVariable String type) {
        try {
            List<Item> items = itemMongoDAO.obtenirItemsParType(type);
            if (items.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(items);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Récupère un item spécifique par son ID
     * @param id L'ID de l'item
     * @return L'item correspondant à l'ID
     */
    @GetMapping("/items/{id}")
    public ResponseEntity<Item> obtenirItemParId(@PathVariable int id) {
        try {
            Item item = itemMongoDAO.obtenirItemParId(id);
            if (item == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(item);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Permet à un joueur d'acheter un item
     * @param joueurId L'ID du joueur qui fait l'achat
     * @param itemId L'ID de l'item à acheter
     * @param quantite La quantité d'items à acheter
     * @return Confirmation de l'achat ou message d'erreur
     */
    @PostMapping("/acheter")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> acheterItem(@RequestParam int joueurId, @RequestParam int itemId, @RequestParam int quantite) {
        try {
            // Vérifier si le joueur existe
            Joueur joueur = joueurDAO.obtenirJoueurParId(joueurId);
            if (joueur == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Joueur non trouvé"));
            }

            // Vérifier si l'item existe
            Item item = itemMongoDAO.obtenirItemParId(itemId);
            if (item == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Item non trouvé"));
            }

            // Effectuer l'achat
            boolean achatReussi = serviceBoutique.acheterItem(joueurId, itemId, quantite);

            if (achatReussi) {
                return ResponseEntity.ok(Map.of(
                        "message", "Achat réussi",
                        "joueur", joueur.getPseudo(),
                        "item", item.getNom(),
                        "quantite", quantite,
                        "prixTotal", item.getPrix() * quantite,
                        "argentRestant", joueur.getArgent()
                ));
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Map.of("message", "L'achat a échoué"));
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de l'achat: " + e.getMessage()));
        }
    }

    /**
     * Ajoute un nouvel item à la boutique (réservé aux administrateurs)
     * @param item L'item à ajouter
     * @return Confirmation de l'ajout
     */
    @PostMapping("/admin/items")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> ajouterItem(@RequestBody Item item) {
        try {
            itemMongoDAO.ajouterItem(item);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("message", "Item ajouté avec succès", "id", item.getId()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de l'ajout de l'item: " + e.getMessage()));
        }
    }

    /**
     * Met à jour un item existant (réservé aux administrateurs)
     * @param id L'ID de l'item à modifier
     * @param item L'item avec les nouvelles valeurs
     * @return Confirmation de la mise à jour
     */
    @PutMapping("/admin/items/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> mettreAJourItem(@PathVariable int id, @RequestBody Item item) {
        try {
            // Vérifier si l'item existe
            Item itemExistant = itemMongoDAO.obtenirItemParId(id);
            if (itemExistant == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Item non trouvé"));
            }

            // Mettre à jour l'ID pour s'assurer qu'il correspond à l'URL
            item.setId(id);

            // Mettre à jour l'item
            itemMongoDAO.mettreAJourItem(item);

            return ResponseEntity.ok(Map.of("message", "Item mis à jour avec succès"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de la mise à jour de l'item: " + e.getMessage()));
        }
    }

    /**
     * Supprime un item (réservé aux administrateurs)
     * @param id L'ID de l'item à supprimer
     * @return Confirmation de la suppression
     */
    @DeleteMapping("/admin/items/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> supprimerItem(@PathVariable int id) {
        try {
            // Vérifier si l'item existe
            Item itemExistant = itemMongoDAO.obtenirItemParId(id);
            if (itemExistant == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("message", "Item non trouvé"));
            }

            // Supprimer l'item
            itemMongoDAO.supprimerItem(id);

            return ResponseEntity.ok(Map.of("message", "Item supprimé avec succès"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de la suppression de l'item: " + e.getMessage()));
        }
    }
}