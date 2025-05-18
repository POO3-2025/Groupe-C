package be.helha.projects.GuerreDesRoyaumes.ServiceImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.CombatDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.CombatSessionMongoDAO;
import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.CombatSessionMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.RoyaumeMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DTO.CombatResolver;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.MongoDBConnectionException;
import be.helha.projects.GuerreDesRoyaumes.Model.Combat.CombatSession;
import be.helha.projects.GuerreDesRoyaumes.Model.Combat.CombatStatus;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Arme;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Bouclier;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Potion;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Model.Royaume;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceCombat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ServiceCombatImpl implements ServiceCombat {

    private final JoueurDAO joueurDAO;
    private final CombatDAO combatDAO;
    private final Random random = new Random();
    private final CombatSessionMongoDAO sessionDAO;
    private final CombatResolver combatResolver;

    // Stockage temporaire des combats en cours avec thread-safety
    private Map<String, Map<String, Object>> combatsEnCours = new ConcurrentHashMap<>();
    // Stockage des actions des joueurs par tour
    private Map<String, Map<String, String>> actionsJoueurs = new ConcurrentHashMap<>();
    // Stockage des résultats des actions
    private Map<String, Map<String, String>> resultatsActions = new ConcurrentHashMap<>();

    @Autowired
    public ServiceCombatImpl(JoueurDAO joueurDAO, CombatDAO combatDAO, CombatSessionMongoDAO sessionDAO, CombatResolver combatResolver) {
        this.joueurDAO = joueurDAO;
        this.combatDAO = combatDAO;
        this.sessionDAO = sessionDAO;
        this.combatResolver = combatResolver;
    }
    
    // Pour les tests
    public CombatDAO getCombatDAO() {
        return this.combatDAO;
    }

    @Override
    public void initialiserCombat(Joueur joueur1, Joueur joueur2, List<Item> itemsSelectionnes) {
        try {
            // Vérifier si les joueurs sont valides
            if (joueur1 == null || joueur2 == null) {
                System.err.println("initialiserCombat: L'un des joueurs est null - joueur1: " + 
                                  (joueur1 == null ? "null" : joueur1.getPseudo()) + 
                                  ", joueur2: " + (joueur2 == null ? "null" : joueur2.getPseudo()));
                throw new IllegalArgumentException("Les joueurs ne peuvent pas être null");
            }
            
            // Vérifier si les personnages sont initialisés
            if (joueur1.getPersonnage() == null || joueur2.getPersonnage() == null) {
                System.err.println("initialiserCombat: L'un des personnages est null - joueur1: " + 
                                  joueur1.getPseudo() + " (personnage: " + (joueur1.getPersonnage() == null ? "null" : "ok") + 
                                  "), joueur2: " + joueur2.getPseudo() + " (personnage: " + 
                                  (joueur2.getPersonnage() == null ? "null" : "ok") + ")");
                throw new IllegalArgumentException("Les personnages des joueurs doivent être initialisés");
            }
            
            // Rechercher si un combat existe déjà entre ces joueurs
            String sessionId = sessionDAO.trouverSessionId(joueur1.getId(), joueur2.getId());
            
            if (sessionId != null) {
                // Combat existant, vérifier s'il est terminé
                CombatSession session = sessionDAO.chargerSession(sessionId);
                
                if (session != null && session.getStatus() != CombatStatus.TERMINE && session.getStatus() != CombatStatus.ABANDONNE) {
                    System.out.println("Un combat est déjà en cours entre ces joueurs");
                    return;
                }
            }
            
            // Créer une nouvelle session de combat
            String nouveauSessionId = sessionDAO.creerSession(joueur1, joueur2);
            
            if (nouveauSessionId == null) {
                throw new RuntimeException("Échec de la création de la session de combat");
            }
            
            System.out.println("Combat initialisé entre " + joueur1.getPseudo() + " et " + joueur2.getPseudo());
            
            // Mettre à jour le statut de la session
            sessionDAO.mettreAJourStatus(nouveauSessionId, CombatStatus.EN_COURS);
            
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation du combat: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'initialisation du combat", e);
        }
    }

    @Override
    public String executerAction(Joueur joueur, Joueur adversaire, String typeAction, int tour) {
        try {
            if (joueur == null || adversaire == null) {
                return "Erreur: Joueur ou adversaire non valide";
            }
            
            // Rechercher la session de combat
            String sessionId = sessionDAO.trouverSessionId(joueur.getId(), adversaire.getId());
            
            if (sessionId == null) {
                return "Erreur: Aucun combat en cours entre ces joueurs";
            }
            
            // Vérifier si c'est bien le tour du joueur
            if (!sessionDAO.estTourDuJoueur(sessionId, joueur.getId())) {
                return "Erreur: Ce n'est pas votre tour";
            }
            
            // Valider que le tour correspond
            int tourActuel = sessionDAO.obtenirTourActuel(sessionId);
            if (tour != tourActuel) {
                return "Erreur: Tour incorrect. Tour actuel: " + tourActuel;
            }
            
            // Créer les paramètres pour l'action
            Map<String, Object> parametres = new HashMap<>();
            
            // Sauvegarder l'action
            boolean actionSauvegardee = sessionDAO.sauvegarderAction(sessionId, joueur.getId(), tour, typeAction, parametres);
            
            if (!actionSauvegardee) {
                return "Erreur: Impossible de sauvegarder l'action";
            }
            
            // Vérifier si les actions des deux joueurs sont complètes
            if (sessionDAO.actionsCompletes(sessionId, tour)) {
                // Résoudre les actions si les deux joueurs ont soumis leur action
                declencherResolution(sessionId, tour);
            }
            
            return "Action '" + typeAction + "' exécutée avec succès";
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution de l'action: " + e.getMessage());
            e.printStackTrace();
            return "Erreur lors de l'exécution de l'action: " + e.getMessage();
        }
    }
    
    @Override
    public String executerActionAvecItem(Joueur joueur, Joueur adversaire, String typeAction, Item item, int tour) {
        try {
            if (joueur == null || adversaire == null || item == null) {
                return "Erreur: Joueur, adversaire ou item non valide";
            }
            
            // Rechercher la session de combat
            String sessionId = sessionDAO.trouverSessionId(joueur.getId(), adversaire.getId());
            
            if (sessionId == null) {
                return "Erreur: Aucun combat en cours entre ces joueurs";
            }
            
            // Vérifier si c'est bien le tour du joueur
            if (!sessionDAO.estTourDuJoueur(sessionId, joueur.getId())) {
                return "Erreur: Ce n'est pas votre tour";
            }
            
            // Valider que le tour correspond
            int tourActuel = sessionDAO.obtenirTourActuel(sessionId);
            if (tour != tourActuel) {
                return "Erreur: Tour incorrect. Tour actuel: " + tourActuel;
            }
            
            // Créer les paramètres pour l'action
            Map<String, Object> parametres = new HashMap<>();
            parametres.put("itemId", item.getId());
            parametres.put("itemType", item.getType());
            parametres.put("itemNom", item.getNom());
            
            if (item instanceof Arme) {
                parametres.put("degats", ((Arme) item).getDegats());
            } else if (item instanceof Bouclier) {
                parametres.put("defense", ((Bouclier) item).getDefense());
            } else if (item instanceof Potion) {
                Potion potion = (Potion) item;
                parametres.put("soin", potion.getSoin());
                parametres.put("degats", potion.getDegats());
            }
            
            // Sauvegarder l'action
            boolean actionSauvegardee = sessionDAO.sauvegarderAction(sessionId, joueur.getId(), tour, "utiliser_item", parametres);
            
            if (!actionSauvegardee) {
                return "Erreur: Impossible de sauvegarder l'action";
            }
            
            // Consommer l'item (le retirer de l'inventaire)
            joueur.getPersonnage().getInventaire().enleverItem(item, 1);
            joueurDAO.mettreAJourJoueur(joueur);
            
            // Vérifier si les actions des deux joueurs sont complètes
            if (sessionDAO.actionsCompletes(sessionId, tour)) {
                // Résoudre les actions si les deux joueurs ont soumis leur action
                declencherResolution(sessionId, tour);
            }
            
            return "Action avec item '" + item.getNom() + "' exécutée avec succès";
        } catch (Exception e) {
            System.err.println("Erreur lors de l'exécution de l'action avec item: " + e.getMessage());
            e.printStackTrace();
            return "Erreur lors de l'exécution de l'action avec item: " + e.getMessage();
        }
    }
    
    private void declencherResolution(String sessionId, int tour) {
        try {
            // Mettre à jour le statut de la session
            sessionDAO.mettreAJourStatus(sessionId, CombatStatus.RESOLUTION);
            
            // Déclencher la résolution des actions
            boolean resolutionReussie = combatResolver.resoudreActions(sessionId, tour);
            
            if (!resolutionReussie) {
                System.err.println("Erreur lors de la résolution des actions pour le tour " + tour);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du déclenchement de la résolution: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void enregistrerVictoire(Joueur joueur) {
        try {
            combatDAO.enregistrerVictoire(joueur);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'enregistrement de la victoire: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void terminerCombat(Joueur joueur1, Joueur joueur2, Joueur vainqueur) {
        try {
            // Rechercher la session de combat
            String sessionId = sessionDAO.trouverSessionId(joueur1.getId(), joueur2.getId());
            
            if (sessionId == null) {
                System.err.println("Aucun combat en cours entre ces joueurs");
                return;
            }
            
            // Archiver la session
            boolean archivageReussi = sessionDAO.archiverSession(sessionId);
            
            if (!archivageReussi) {
                System.err.println("Erreur lors de l'archivage de la session de combat");
            }
            
            // Enregistrer le résultat dans la base SQL
            if (vainqueur != null) {
                // Enregistrer la victoire
                combatDAO.enregistrerVictoire(vainqueur);
                
                // Enregistrer la défaite pour l'autre joueur
                if (vainqueur.getId() == joueur1.getId()) {
                    combatDAO.enregistrerDefaite(joueur2);
                } else {
                    combatDAO.enregistrerDefaite(joueur1);
                }
                
                                // L'incrémentation du niveau du royaume est maintenant gérée dans enregistrerVictoire()                // Pas besoin de mise à jour ici car déjà fait dans CombatDAOImpl.enregistrerVictoire
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la terminaison du combat: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public boolean estCombatTermine(Joueur joueur1, Joueur joueur2, int tourActuel) {
        try {
            // Rechercher la session de combat
            String sessionId = sessionDAO.trouverSessionId(joueur1.getId(), joueur2.getId());
            
            if (sessionId == null) {
                return true; // Si pas de combat, considérer comme terminé
            }
            
            // Charger la session
            CombatSession session = sessionDAO.chargerSession(sessionId);
            
            if (session == null) {
                return true;
            }
            
            return session.estTermine();
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification de fin de combat: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int calculerDegats(Joueur attaquant, Joueur defenseur, String typeAttaque) {
        // Méthode de calcul de dégâts utilisant les statistiques des personnages
        try {
            if (attaquant == null || defenseur == null || attaquant.getPersonnage() == null || defenseur.getPersonnage() == null) {
                return 0;
            }
            
            // Utiliser les statistiques du personnage
            double degatsBase = attaquant.getPersonnage().getDegats();
            
            System.out.println("Statistiques de " + attaquant.getPersonnage().getNom() + ": " + 
                             "Vie=" + attaquant.getPersonnage().getVie() + ", " +
                             "Dégâts=" + attaquant.getPersonnage().getDegats() + ", " +
                             "Résistance=" + attaquant.getPersonnage().getResistance());
            
            // Modifier les dégâts selon le type d'attaque
            if ("special".equals(typeAttaque)) {
                degatsBase *= 1.5; // Bonus pour les attaques spéciales
            }
            
            // La résistance sera appliquée directement dans la méthode subirDegats
            // Aucune variation aléatoire
            return (int) Math.max(1, degatsBase);
        } catch (Exception e) {
            System.err.println("Erreur lors du calcul des dégâts: " + e.getMessage());
            e.printStackTrace();
            return 1; // Dégâts minimaux en cas d'erreur
        }
    }

    @Override
    public int calculerDefense(Joueur defenseur, int degats) {
        try {
            if (defenseur == null || defenseur.getPersonnage() == null) {
                return degats;
            }
            
            System.out.println("Statistiques de défense de " + defenseur.getPersonnage().getNom() + ": " + 
                             "Vie=" + defenseur.getPersonnage().getVie() + ", " +
                             "Résistance=" + defenseur.getPersonnage().getResistance());
            
            // Soustraction directe de la résistance
            double degatsReduits = Math.max(1, degats - defenseur.getPersonnage().getResistance());
            
            System.out.println("Dégâts d'origine: " + degats + ", Dégâts après résistance: " + (int)Math.max(1, degatsReduits));
            
            return (int) Math.max(1, degatsReduits);
        } catch (Exception e) {
            System.err.println("Erreur lors du calcul de la défense: " + e.getMessage());
            e.printStackTrace();
            return degats; // Pas de réduction en cas d'erreur
        }
    }

    @Override
    public boolean estTourDuJoueur(Joueur joueur, Joueur adversaire) {
        try {
            // Si l'adversaire est null, on ne peut pas vérifier le tour
            // Dans ce cas, on considère que c'est le tour du joueur actuel pour éviter les erreurs
            if (adversaire == null) {
                System.out.println("DEBUG: Adversaire null dans estTourDuJoueur, considéré comme tour du joueur");
                return true;
            }
            
            // Rechercher la session de combat
            String sessionId = sessionDAO.trouverSessionId(joueur.getId(), adversaire.getId());
            
            if (sessionId == null) {
                // Si pas de session trouvée, on considère que c'est le tour du joueur actuel
                System.out.println("DEBUG: Pas de session trouvée dans estTourDuJoueur, considéré comme tour du joueur");
                return true;
            }
            
            return sessionDAO.estTourDuJoueur(sessionId, joueur.getId());
        } catch (Exception e) {
            System.err.println("Erreur lors de la vérification du tour du joueur: " + e.getMessage());
            e.printStackTrace();
            // En cas d'erreur, considérer que c'est le tour du joueur pour éviter un blocage
            return true;
        }
    }
    
    @Override
    public String obtenirResultatActionAdverse(Joueur joueur, Joueur adversaire, int tour) {
        try {
            // Rechercher la session de combat
            String sessionId = sessionDAO.trouverSessionId(joueur.getId(), adversaire.getId());
            
            if (sessionId == null) {
                return "Aucun combat en cours";
            }
            
            // Obtenir les résultats du tour
            Map<String, Object> resultats = sessionDAO.obtenirResultatTour(sessionId, tour);
            
            if (resultats.isEmpty()) {
                return "Aucun résultat disponible";
            }
            
            // Construire un message descriptif
            StringBuilder message = new StringBuilder();
            
            // Identifier qui est joueur1 et joueur2 dans la session
            CombatSession session = sessionDAO.chargerSession(sessionId);
            boolean joueurEstJ1 = session.getJoueur1Id() == joueur.getId();
            
            int joueurId = joueur.getId();
            int adversaireId = adversaire.getId();
            
            String actionJoueur = (String) resultats.get(joueurEstJ1 ? "actionJoueur1" : "actionJoueur2");
            String actionAdversaire = (String) resultats.get(joueurEstJ1 ? "actionJoueur2" : "actionJoueur1");
            
            double degatsJoueur = ((Number) resultats.getOrDefault(joueurEstJ1 ? "degatsJoueur1" : "degatsJoueur2", 0)).doubleValue();
            double degatsAdversaire = ((Number) resultats.getOrDefault(joueurEstJ1 ? "degatsJoueur2" : "degatsJoueur1", 0)).doubleValue();
            
            double healingJoueur = ((Number) resultats.getOrDefault(joueurEstJ1 ? "healingJoueur1" : "healingJoueur2", 0)).doubleValue();
            double healingAdversaire = ((Number) resultats.getOrDefault(joueurEstJ1 ? "healingJoueur2" : "healingJoueur1", 0)).doubleValue();
            
            // Décrivez ce que l'adversaire a fait
            message.append("Votre adversaire a choisi: ").append(actionAdversaire).append("\n");
            
            if ("special".equals(actionAdversaire)) {
                String nomCompetence = (String) resultats.get(joueurEstJ1 ? "competenceJoueur2" : "competenceJoueur1");
                message.append("Il a utilisé la compétence: ").append(nomCompetence).append("\n");
            } else if ("utiliser_item".equals(actionAdversaire)) {
                String nomItem = (String) resultats.get(joueurEstJ1 ? "itemJoueur2" : "itemJoueur1");
                message.append("Il a utilisé l'item: ").append(nomItem).append("\n");
            }
            
            if (degatsAdversaire > 0) {
                message.append("Il vous a infligé ").append(String.format("%.1f", degatsAdversaire)).append(" points de dégâts.\n");
            }
            
            if (healingAdversaire > 0) {
                message.append("Il s'est soigné de ").append(String.format("%.1f", healingAdversaire)).append(" points de vie.\n");
            }
            
            if (degatsJoueur > 0) {
                message.append("Vous lui avez infligé ").append(String.format("%.1f", degatsJoueur)).append(" points de dégâts.\n");
            }
            
            if (healingJoueur > 0) {
                message.append("Vous vous êtes soigné de ").append(String.format("%.1f", healingJoueur)).append(" points de vie.\n");
            }
            
            // Points de vie après le tour
            double pvJoueur = ((Number) resultats.get(joueurEstJ1 ? "pvJoueur1" : "pvJoueur2")).doubleValue();
            double pvAdversaire = ((Number) resultats.get(joueurEstJ1 ? "pvJoueur2" : "pvJoueur1")).doubleValue();
            
            message.append("\nPoints de vie après ce tour:\n");
            message.append("- Vous: ").append(String.format("%.1f", pvJoueur)).append("\n");
            message.append("- Adversaire: ").append(String.format("%.1f", pvAdversaire));
            
            return message.toString();
        } catch (Exception e) {
            System.err.println("Erreur lors de l'obtention du résultat de l'action adverse: " + e.getMessage());
            e.printStackTrace();
            return "Erreur lors de l'obtention du résultat";
        }
    }
    
    @Override
    public boolean forcerChangementTour(Joueur joueur, Joueur adversaire) {
        try {
            // Rechercher la session de combat
            String sessionId = sessionDAO.trouverSessionId(joueur.getId(), adversaire.getId());
            
            if (sessionId == null) {
                return false;
            }
            
            // Charger la session
            CombatSession session = sessionDAO.chargerSession(sessionId);
            
            if (session == null) {
                return false;
            }
            
            // Forcer le changement de joueur actif
            int joueurActifId = session.getJoueurActifId();
            int nouveauJoueurActifId = (joueurActifId == joueur.getId()) ? adversaire.getId() : joueur.getId();
            
            session.setJoueurActifId(nouveauJoueurActifId);
            session.setStatus(CombatStatus.EN_COURS);
            
            return sessionDAO.sauvegarderSession(session);
        } catch (Exception e) {
            System.err.println("Erreur lors du forçage du changement de tour: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public int getTourActuel(Joueur joueur, Joueur adversaire) {
        try {
            // Rechercher la session de combat
            String sessionId = sessionDAO.trouverSessionId(joueur.getId(), adversaire.getId());
            
            if (sessionId == null) {
                return 0;
            }
            
            return sessionDAO.obtenirTourActuel(sessionId);
        } catch (Exception e) {
            System.err.println("Erreur lors de l'obtention du tour actuel: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }
    
    @Override
    public boolean transfererItemsCoffreVersInventaire(Joueur joueur, Item item, int quantite) {
        try {
            if (joueur == null || item == null || quantite <= 0) {
                return false;
            }
            
            // Vérifier si le joueur a le personnage et l'inventaire initialisés
            if (joueur.getPersonnage() == null || joueur.getPersonnage().getInventaire() == null) {
                return false;
            }
            
            // Transférer l'item du coffre vers l'inventaire de combat
            if (joueur.getCoffre().enleverItem(item, quantite)) {
                joueur.getPersonnage().getInventaire().ajouterItem(item, quantite);
                // Mettre à jour le joueur dans la base de données
                joueurDAO.mettreAJourJoueur(joueur);
                return true;
            }
            
            return false;
        } catch (Exception e) {
            System.err.println("Erreur lors du transfert d'items: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public String getAdversairePseudo(int joueurId) {
        try {
            // Vérifier si un combat est en cours pour ce joueur
            int adversaireId = getCombatDAO().verifierCombatEnCours(joueurId);
            
            if (adversaireId > 0) {
                // Un combat est en cours, récupérer le pseudo de l'adversaire
                Joueur adversaire = joueurDAO.obtenirJoueurParId(adversaireId);
                if (adversaire != null) {
                    return adversaire.getPseudo();
                }
            }
            
            return null; // Aucun combat en cours
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération du pseudo de l'adversaire: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}



