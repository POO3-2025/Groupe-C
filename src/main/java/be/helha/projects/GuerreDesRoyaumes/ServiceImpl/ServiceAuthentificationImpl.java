package be.helha.projects.GuerreDesRoyaumes.ServiceImpl;

import be.helha.projects.GuerreDesRoyaumes.DAO.JoueurDAO;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.JoueurDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.PersonnageMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.DAOImpl.RoyaumeMongoDAOImpl;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.AuthentificationException;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.JoueurNotFoundException;
import be.helha.projects.GuerreDesRoyaumes.Exceptions.PersonnageNotFoundException;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Coffre;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Slot;
import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;
import be.helha.projects.GuerreDesRoyaumes.Model.Royaume;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceAuthentification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

/**
 * Implémentation du service d'authentification des joueurs.
 * <p>
 * Gère l'inscription, l'authentification, la gestion de profil,
 * le choix du personnage, et l'initialisation des données du joueur.
 * </p>
 */
@Service
public class ServiceAuthentificationImpl implements ServiceAuthentification {

    private JoueurDAO joueurDAO;

    /**
     * Constructeur avec injection du DAO joueur.
     *
     * @param joueurDAO DAO pour accéder aux données des joueurs.
     */
    @Autowired
    public ServiceAuthentificationImpl(JoueurDAO joueurDAO) {
        this.joueurDAO = joueurDAO;
    }

    /**
     * Inscrit un nouveau joueur en vérifiant la disponibilité du pseudo,
     * validant le format, créant le royaume, le coffre et hachant le mot de passe.
     *
     * @param nom        Nom du joueur
     * @param prenom     Prénom du joueur
     * @param pseudo     Pseudo unique du joueur
     * @param motDePasse Mot de passe en clair
     * @throws AuthentificationException en cas de pseudo déjà utilisé ou format invalide
     */
    @Override
    public void inscrireJoueur(String nom, String prenom, String pseudo, String motDePasse) {
        if (joueurDAO.obtenirJoueurParPseudo(pseudo) != null) {
            throw new AuthentificationException("Ce pseudo est déjà utilisé");
        }
        if (!pseudo.matches("[a-zA-Z0-9_]+")) {
            throw new AuthentificationException("Le pseudo ne peut contenir que des lettres, chiffres et underscores.");
        }

        Royaume royaume = new Royaume(0, "Royaume de " + pseudo, 1);
        Coffre coffre = new Coffre();
        String motDePasseHache = BCrypt.hashpw(motDePasse, BCrypt.gensalt());
        Joueur joueur = new Joueur(0, nom, prenom, pseudo, motDePasseHache, 5000, royaume, null, coffre, 0, 0);
        joueurDAO.ajouterJoueur(joueur);

        Joueur joueurCree = joueurDAO.obtenirJoueurParPseudo(pseudo);
        if (joueurCree == null) {
            throw new AuthentificationException("Erreur lors de la création du joueur");
        }

        try {
            RoyaumeMongoDAOImpl royaumeMongoDAO = RoyaumeMongoDAOImpl.getInstance();
            royaumeMongoDAO.ajouterRoyaume(royaume, joueurCree.getId());
            System.out.println("Royaume créé dans MongoDB pour le joueur: " + joueurCree.getId());
        } catch (Exception e) {
            System.err.println("Erreur lors de la création du royaume dans MongoDB: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Authentifie un joueur en vérifiant son pseudo et mot de passe.
     *
     * @param pseudo     Pseudo du joueur
     * @param motDePasse Mot de passe en clair
     * @return true si l'authentification réussit, false sinon
     */
    @Override
    public boolean authentifierJoueur(String pseudo, String motDePasse) {
        if (pseudo == null || pseudo.trim().isEmpty()) {
            System.err.println("Le pseudo ne peut pas être vide");
            return false;
        }
        if (motDePasse == null || motDePasse.trim().isEmpty()) {
            System.err.println("Le mot de passe ne peut pas être vide");
            return false;
        }

        if (joueurDAO instanceof JoueurDAOImpl) {
            try {
                boolean resultat = ((JoueurDAOImpl) joueurDAO).verifierIdentifiants(pseudo, motDePasse);
                if (!resultat) {
                    System.err.println("Échec d'authentification pour l'utilisateur " + pseudo);
                }
                return resultat;
            } catch (Exception e) {
                System.err.println("Erreur lors de l'authentification de l'utilisateur " + pseudo + ": " + e.getMessage());
                e.printStackTrace();
                return false;
            }
        } else {
            System.err.println("Service d'authentification mal configuré: le DAO n'est pas du type attendu");
            return false;
        }
    }

    /**
     * Met à jour le profil du joueur (pseudo et mot de passe).
     * Valide la disponibilité du pseudo et hache le mot de passe si modifié.
     *
     * @param id         ID du joueur
     * @param pseudo     Nouveau pseudo
     * @param motDePasse Nouveau mot de passe
     * @throws AuthentificationException Si pseudo déjà utilisé ou erreur de mise à jour
     * @throws JoueurNotFoundException    Si joueur non trouvé
     */
    @Override
    public void gererProfil(int id, String pseudo, String motDePasse) {
        try {
            Joueur joueur = joueurDAO.obtenirJoueurParId(id);
            if (!joueur.getPseudo().equals(pseudo)) {
                Joueur existant = joueurDAO.obtenirJoueurParPseudo(pseudo);
                if (existant != null && existant.getId() != joueur.getId()) {
                    throw new AuthentificationException("Ce pseudo est déjà utilisé");
                }
            }
            joueur.setPseudo(pseudo);

            if (!motDePasse.equals(joueur.getMotDePasse())) {
                joueur.setMotDePasse(BCrypt.hashpw(motDePasse, BCrypt.gensalt()));
            }
            joueurDAO.mettreAJourJoueur(joueur);
        } catch (JoueurNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new AuthentificationException("Erreur lors de la mise à jour du profil", e);
        }
    }

    /**
     * Initialise le joueur avec un royaume et un personnage.
     * Initialise aussi l'inventaire du personnage si nécessaire.
     *
     * @param pseudo    Pseudo du joueur
     * @param royaume   Royaume à associer
     * @param personnage Personnage à associer
     * @throws IllegalArgumentException Si joueur non trouvé
     * @throws IllegalStateException    Si joueur déjà initialisé
     */
    @Override
    public void initialiserJoueur(String pseudo, Royaume royaume, Personnage personnage) {
        Joueur joueur = joueurDAO.obtenirJoueurParPseudo(pseudo);
        if (joueur == null) {
            throw new IllegalArgumentException("Joueur non trouvé");
        }
        if (joueur.getRoyaume() != null || joueur.getPersonnage() != null) {
            throw new IllegalStateException("Le joueur est déjà initialisé");
        }
        if (personnage.getInventaire() == null) {
            Inventaire inventaire = new Inventaire();
            for (int i = 0; i < inventaire.getMaxSlots(); i++) {
                inventaire.getSlots().set(i, new Slot(null, 0));
            }
            personnage.setInventaire(inventaire);
        } else {
            for (int i = 0; i < personnage.getInventaire().getMaxSlots(); i++) {
                if (personnage.getInventaire().getSlots().get(i) == null) {
                    personnage.getInventaire().getSlots().set(i, new Slot(null, 0));
                }
            }
        }
        joueur.setRoyaume(royaume);
        joueur.setPersonnage(personnage);
        joueurDAO.mettreAJourJoueur(joueur);
    }

    /**
     * Associe un personnage à un joueur via leurs IDs.
     * Récupère le personnage depuis MongoDB et met à jour le joueur.
     *
     * @param joueurId    ID du joueur
     * @param personnageId ID du personnage (non utilisé directement ici)
     * @throws JoueurNotFoundException      Si joueur introuvable
     * @throws PersonnageNotFoundException  Si personnage introuvable
     * @throws RuntimeException             En cas d'autres erreurs
     */
    @Override
    public void choisirPersonnage(int joueurId, int personnageId) {
        try {
            Joueur joueur = joueurDAO.obtenirJoueurParId(joueurId);
            if (joueur == null) {
                throw new JoueurNotFoundException("Joueur non trouvé avec l'ID: " + joueurId);
            }

            PersonnageMongoDAOImpl personnageMongoDAO = PersonnageMongoDAOImpl.getInstance();
            Personnage personnage = personnageMongoDAO.obtenirPersonnageParJoueurId(joueurId);
            if (personnage == null) {
                throw new PersonnageNotFoundException("Personnage non trouvé pour ce joueur");
            }
            joueur.setPersonnage(personnage);
            joueurDAO.mettreAJourJoueur(joueur);
            System.out.println("Personnage associé au joueur " + joueur.getPseudo());
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du choix du personnage: " + e.getMessage(), e);
        }
    }

    /**
     * Récupère un joueur par son pseudo.
     *
     * @param pseudo Le pseudo recherché
     * @return Le joueur ou null s'il n'existe pas
     */
    @Override
    public Joueur obtenirJoueurParPseudo(String pseudo) {
        return joueurDAO.obtenirJoueurParPseudo(pseudo);
    }

    /**
     * Met à jour un joueur en base.
     *
     * @param joueur Le joueur à mettre à jour
     */
    @Override
    public void mettreAJourJoueur(Joueur joueur) {
        joueurDAO.mettreAJourJoueur(joueur);
    }

    /**
     * Connecte un joueur en positionnant son statut comme inactif (attente).
     *
     * @param pseudo Le pseudo du joueur
     * @return true si succès, false sinon
     */
    @Override
    public boolean connecterJoueur(String pseudo) {
        try {
            Joueur joueur = joueurDAO.obtenirJoueurParPseudo(pseudo);
            if (joueur == null) {
                System.err.println("Impossible de connecter le joueur, pseudo introuvable: " + pseudo);
                return false;
            }
            joueurDAO.definirStatutConnexion(joueur.getId(), false);
            System.out.println("Joueur connecté avec succès (statut inactif): " + pseudo);
            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors de la connexion du joueur " + pseudo + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Déconnecte un joueur en positionnant son statut comme inactif.
     *
     * @param pseudo Le pseudo du joueur
     * @return true si succès, false sinon
     */
    @Override
    public boolean deconnecterJoueur(String pseudo) {
        try {
            Joueur joueur = joueurDAO.obtenirJoueurParPseudo(pseudo);
            if (joueur == null) {
                System.err.println("Impossible de déconnecter le joueur, pseudo introuvable: " + pseudo);
                return false;
            }
            joueurDAO.definirStatutConnexion(joueur.getId(), false);
            System.out.println("Joueur déconnecté avec succès: " + pseudo);
            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors de la déconnexion du joueur " + pseudo + ": " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
