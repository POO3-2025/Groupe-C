package be.helha.projects.GuerreDesRoyaumes.Controller;

import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.ItemMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.*;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Coffre;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Slot;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/coffres")
public class ControleurCoffre {

    private static final Logger logger = LoggerFactory.getLogger(ControleurCoffre.class);

    private final JoueurDAO joueurDAO;
    private final ItemMongoDAOImpl itemMongoDAO;

    @Autowired
    public ControleurCoffre(JoueurDAO joueurDAO) {
        this.joueurDAO = joueurDAO;
        this.itemMongoDAO = ItemMongoDAOImpl.getInstance();
    }

    /**
     * Récupère les items dans le coffre d'un joueur
     * @param joueurId L'ID du joueur
     * @return Le contenu du coffre du joueur
     */
    @GetMapping("/{joueurId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getContenuCoffre(@PathVariable int joueurId) {
        try {
            Joueur joueur = joueurDAO.obtenirJoueurParId(joueurId);

            Coffre coffre = joueur.getCoffre();
            if (coffre == null) {
                throw new CoffreException("Le joueur n'a pas de coffre initialisé");
            }

            List<Map<String, Object>> contenu = coffre.getSlots().stream()
                    .filter(slot -> slot != null && slot.getQuantity() > 0)
                    .map(slot -> {
                        Map<String, Object> itemMap = new HashMap<>();
                        itemMap.put("id", slot.getItem().getId());
                        itemMap.put("nom", slot.getItem().getNom());
                        itemMap.put("type", slot.getItem().getType());
                        itemMap.put("quantite", slot.getQuantity());
                        return itemMap;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("joueurId", joueur.getId());
            response.put("maxSlots", coffre.getMaxSlots());
            response.put("items", contenu);

            return ResponseEntity.ok(response);
        } catch (JoueurNotFoundException e) {
            logger.error("Joueur non trouvé: ", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (CoffreException e) {
            logger.error("Erreur de coffre: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération du contenu du coffre: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de la récupération du contenu du coffre: " + e.getMessage()));
        }
    }

    /**
     * Ajoute un item au coffre d'un joueur
     * @param joueurId L'ID du joueur
     * @param request Données de l'item à ajouter (ID et quantité)
     * @return Confirmation de l'ajout
     */
    @PostMapping("/{joueurId}/ajouter")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> ajouterItemAuCoffre(
            @PathVariable int joueurId,
            @RequestBody Map<String, Integer> request) {

        try {
            int itemId = request.get("itemId");
            int quantite = request.get("quantite");

            if (quantite <= 0) {
                throw new IllegalArgumentException("La quantité doit être positive");
            }

            Joueur joueur = joueurDAO.obtenirJoueurParId(joueurId);

            // Chercher l'item dans tous les items de MongoDB
            List<Item> items = itemMongoDAO.obtenirTousLesItems();
            Item item = null;
            for (Item i : items) {
                if (i.getId() == itemId) {
                    item = i;
                    break;
                }
            }
            
            if (item == null) {
                throw new ItemNotFoundException(itemId);
            }

            Coffre coffre = joueur.getCoffre();
            if (coffre == null) {
                throw new CoffreException("Le joueur n'a pas de coffre initialisé");
            }

            boolean ajoutReussi = coffre.ajouterItem(item, quantite);

            if (ajoutReussi) {
                // Mettre à jour le joueur dans la base de données
                joueurDAO.mettreAJourJoueur(joueur);

                return ResponseEntity.ok(Map.of(
                        "message", "Item ajouté au coffre avec succès",
                        "itemNom", item.getNom(),
                        "quantite", quantite
                ));
            } else {
                throw new CoffreException("Impossible d'ajouter l'item au coffre (coffre plein ou quantité trop importante)");
            }
        } catch (JoueurNotFoundException | ItemNotFoundException e) {
            logger.error("Entité non trouvée: ", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (CoffreException e) {
            logger.error("Erreur de coffre: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            logger.error("Argument invalide: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Erreur lors de l'ajout d'un item au coffre: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de l'ajout d'un item au coffre: " + e.getMessage()));
        }
    }

    /**
     * Retire un item du coffre d'un joueur
     * @param joueurId L'ID du joueur
     * @param request Données de l'item à retirer (ID et quantité)
     * @return Confirmation du retrait
     */
    @PostMapping("/{joueurId}/retirer")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> retirerItemDuCoffre(
            @PathVariable int joueurId,
            @RequestBody Map<String, Integer> request) {

        try {
            int itemId = request.get("itemId");
            int quantite = request.get("quantite");

            if (quantite <= 0) {
                throw new IllegalArgumentException("La quantité doit être positive");
            }

            Joueur joueur = joueurDAO.obtenirJoueurParId(joueurId);

            // Chercher l'item dans tous les items de MongoDB
            List<Item> items = itemMongoDAO.obtenirTousLesItems();
            Item item = null;
            for (Item i : items) {
                if (i.getId() == itemId) {
                    item = i;
                    break;
                }
            }
            
            if (item == null) {
                throw new ItemNotFoundException(itemId);
            }

            Coffre coffre = joueur.getCoffre();
            if (coffre == null) {
                throw new CoffreException("Le joueur n'a pas de coffre initialisé");
            }

            boolean retraitReussi = coffre.enleverItem(item, quantite);

            if (retraitReussi) {
                // Mettre à jour le joueur dans la base de données
                joueurDAO.mettreAJourJoueur(joueur);

                return ResponseEntity.ok(Map.of(
                        "message", "Item retiré du coffre avec succès",
                        "itemNom", item.getNom(),
                        "quantite", quantite
                ));
            } else {
                throw new CoffreException("Impossible de retirer l'item du coffre (quantité insuffisante ou item non présent)");
            }
        } catch (JoueurNotFoundException | ItemNotFoundException e) {
            logger.error("Entité non trouvée: ", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (CoffreException e) {
            logger.error("Erreur de coffre: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            logger.error("Argument invalide: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Erreur lors du retrait d'un item du coffre: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors du retrait d'un item du coffre: " + e.getMessage()));
        }
    }

    /**
     * Utilise un item du coffre
     * @param joueurId L'ID du joueur
     * @param itemId L'ID de l'item à utiliser
     * @return Résultat de l'utilisation de l'item
     */
    @PostMapping("/{joueurId}/utiliser/{itemId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> utiliserItem(
            @PathVariable int joueurId,
            @PathVariable int itemId) {

        try {
            Joueur joueur = joueurDAO.obtenirJoueurParId(joueurId);

            // Chercher l'item dans tous les items de MongoDB
            List<Item> items = itemMongoDAO.obtenirTousLesItems();
            Item item = null;
            for (Item i : items) {
                if (i.getId() == itemId) {
                    item = i;
                    break;
                }
            }
            
            if (item == null) {
                throw new ItemNotFoundException(itemId);
            }

            Coffre coffre = joueur.getCoffre();
            if (coffre == null) {
                throw new CoffreException("Le joueur n'a pas de coffre initialisé");
            }

            // Vérifier si l'item est présent dans le coffre
            boolean itemPresent = false;
            for (Slot slot : coffre.getSlots()) {
                if (slot != null && slot.getItem().getId() == itemId && slot.getQuantity() > 0) {
                    itemPresent = true;
                    break;
                }
            }

            if (!itemPresent) {
                throw new CoffreException("Cet item n'est pas disponible dans le coffre");
            }

            // Utiliser l'item
            try {
                item.use();
            } catch (Exception e) {
                throw new ItemException("Erreur lors de l'utilisation de l'item: " + e.getMessage(), e);
            }

            // Retirer un exemplaire de l'item du coffre
            boolean retraitReussi = coffre.enleverItem(item, 1);

            if (retraitReussi) {
                // Mettre à jour le joueur dans la base de données
                joueurDAO.mettreAJourJoueur(joueur);

                return ResponseEntity.ok(Map.of(
                        "message", "Item utilisé avec succès",
                        "itemNom", item.getNom()
                ));
            } else {
                throw new CoffreException("Erreur lors du retrait de l'item après utilisation");
            }
        } catch (JoueurNotFoundException | ItemNotFoundException e) {
            logger.error("Entité non trouvée: ", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (CoffreException | ItemException e) {
            logger.error("Erreur spécifique: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Erreur lors de l'utilisation d'un item: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de l'utilisation d'un item: " + e.getMessage()));
        }
    }

    /**
     * Met à jour la capacité du coffre d'un joueur (réservé aux administrateurs)
     * @param joueurId L'ID du joueur
     * @param request Données de mise à jour (nouvelle capacité)
     * @return Confirmation de la mise à jour
     */
    @PutMapping("/{joueurId}/capacite")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> mettreAJourCapaciteCoffre(
            @PathVariable int joueurId,
            @RequestBody Map<String, Integer> request) {

        try {
            int nouvelleCapacite = request.get("capacite");

            if (nouvelleCapacite <= 0) {
                throw new IllegalArgumentException("La capacité doit être positive");
            }

            Joueur joueur = joueurDAO.obtenirJoueurParId(joueurId);

            Coffre coffre = joueur.getCoffre();
            if (coffre == null) {
                throw new CoffreException("Le joueur n'a pas de coffre initialisé");
            }

            coffre.setMaxSlots(nouvelleCapacite);

            // Mettre à jour le joueur dans la base de données
            joueurDAO.mettreAJourJoueur(joueur);

            return ResponseEntity.ok(Map.of(
                    "message", "Capacité du coffre mise à jour avec succès",
                    "nouvelleCpacite", nouvelleCapacite
            ));
        } catch (JoueurNotFoundException e) {
            logger.error("Joueur non trouvé: ", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("message", e.getMessage()));
        } catch (CoffreException e) {
            logger.error("Erreur de coffre: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (IllegalArgumentException e) {
            logger.error("Argument invalide: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de la capacité du coffre: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Erreur lors de la mise à jour de la capacité du coffre: " + e.getMessage()));
        }
    }
}