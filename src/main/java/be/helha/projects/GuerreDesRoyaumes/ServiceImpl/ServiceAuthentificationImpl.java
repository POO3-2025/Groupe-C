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

@Service
public class ServiceAuthentificationImpl implements ServiceAuthentification {

    private JoueurDAO joueurDAO;

    @Autowired
    public ServiceAuthentificationImpl(JoueurDAO joueurDAO) {
        this.joueurDAO = joueurDAO;
    }

    @Override
    public void inscrireJoueur(String nom, String prenom, String pseudo, String motDePasse) {
        // Vérifier si le pseudo existe déjà
        if (joueurDAO.obtenirJoueurParPseudo(pseudo) != null) {
            throw new AuthentificationException("Ce pseudo est déjà utilisé");
        }

        // Validation du format du pseudo
        if (!pseudo.matches("[a-zA-Z0-9_]+")) {
            throw new AuthentificationException("Le pseudo ne peut contenir que des lettres, chiffres et underscores.");
        }

        // Créer un nouveau royaume et le coffre par défaut
        Royaume royaume = new Royaume(0, "Royaume de " + pseudo, 1);
        Coffre coffre = new Coffre();

        // Initialiser les slots du coffre avec une arme et un bouclier par défaut
        if (coffre.getSlots().size() >= 2) {
            coffre.getSlots().set(0, new Slot(new be.helha.projects.GuerreDesRoyaumes.Model.Items.Arme(0, "Épée de base", 1, 0, 10), 1));
            coffre.getSlots().set(1, new Slot(new be.helha.projects.GuerreDesRoyaumes.Model.Items.Bouclier(0, "Bouclier de base", 1, 0, 10), 1));
        }
        // Les autres slots restent vides (déjà initialisés)

        // Créer le joueur avec le mot de passe haché
        String motDePasseHache = BCrypt.hashpw(motDePasse, BCrypt.gensalt());
        Joueur joueur = new Joueur(0, nom, prenom, pseudo, motDePasseHache, 5000, royaume, null, coffre, 0, 0);

        // Persister le joueur dans SQL
        joueurDAO.ajouterJoueur(joueur);
        
        // Récupérer l'ID généré pour le joueur
        Joueur joueurCree = joueurDAO.obtenirJoueurParPseudo(pseudo);
        if (joueurCree == null) {
            throw new AuthentificationException("Erreur lors de la création du joueur");
        }
        
        // Créer et persister le royaume dans MongoDB
        try {
            RoyaumeMongoDAOImpl royaumeMongoDAO = RoyaumeMongoDAOImpl.getInstance();
            royaumeMongoDAO.ajouterRoyaume(royaume, joueurCree.getId());
            System.out.println("Royaume créé dans MongoDB pour le joueur: " + joueurCree.getId());
        } catch (Exception e) {
            System.err.println("Erreur lors de la création du royaume dans MongoDB: " + e.getMessage());
            e.printStackTrace();
            // On continue même en cas d'erreur pour ne pas bloquer l'inscription
        }
    }

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

        // Caster joueurDAO en JoueurDAOImpl pour accéder à la méthode verifierIdentifiants
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

    @Override
    public void gererProfil(int id, String pseudo, String motDePasse) {
        try {
            Joueur joueur = joueurDAO.obtenirJoueurParId(id);

            // Vérifier si le nouveau pseudo est disponible (si changé)
            if (!joueur.getPseudo().equals(pseudo)) {
                Joueur existant = joueurDAO.obtenirJoueurParPseudo(pseudo);
                if (existant != null && existant.getId() != joueur.getId()) {
                    throw new AuthentificationException("Ce pseudo est déjà utilisé");
                }
            }

            // Mettre à jour les informations
            joueur.setPseudo(pseudo);

            // Si le mot de passe est changé, le hacher
            if (!motDePasse.equals(joueur.getMotDePasse())) {
                joueur.setMotDePasse(BCrypt.hashpw(motDePasse, BCrypt.gensalt()));
            }

            // Persister les modifications
            joueurDAO.mettreAJourJoueur(joueur);
        } catch (JoueurNotFoundException e) {
            throw e; // Relance l'exception si c'est déjà une JoueurNotFoundException
        } catch (Exception e) {
            throw new AuthentificationException("Erreur lors de la mise à jour du profil", e);
        }
    }

    @Override
    public void initialiserJoueur(String pseudo, Royaume royaume, Personnage personnage) {
        Joueur joueur = joueurDAO.obtenirJoueurParPseudo(pseudo);
        if (joueur == null) {
            throw new IllegalArgumentException("Joueur non trouvé");
        }

        // Vérifier si le joueur a déjà un royaume ou un personnage
        if (joueur.getRoyaume() != null || joueur.getPersonnage() != null) {
            throw new IllegalStateException("Le joueur est déjà initialisé");
        }

        // Initialisation de l'inventaire du personnage
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

        // Mettre à jour le joueur avec le royaume et le personnage
        joueur.setRoyaume(royaume);
        joueur.setPersonnage(personnage);

        // Persister les modifications
        joueurDAO.mettreAJourJoueur(joueur);
    }

    @Override
    public void choisirPersonnage(int joueurId, int personnageId) {
        try {
            // Récupérer le joueur
            Joueur joueur = joueurDAO.obtenirJoueurParId(joueurId);
            if (joueur == null) {
                throw new JoueurNotFoundException("Joueur non trouvé avec l'ID: " + joueurId);
            }
            
            // Récupérer le personnage depuis MongoDB en utilisant l'ID du joueur
            PersonnageMongoDAOImpl personnageMongoDAO = PersonnageMongoDAOImpl.getInstance();
            Personnage personnage = personnageMongoDAO.obtenirPersonnageParJoueurId(joueurId);
            
            if (personnage == null) {
                throw new PersonnageNotFoundException("Personnage non trouvé pour ce joueur");
            }
            
            // Assigner le personnage au joueur
            joueur.setPersonnage(personnage);
            
            // Persister les modifications
            joueurDAO.mettreAJourJoueur(joueur);
            
            System.out.println("Personnage associé au joueur " + joueur.getPseudo());
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du choix du personnage: " + e.getMessage(), e);
        }
    }

    @Override
    public Joueur obtenirJoueurParPseudo(String pseudo) {
        return joueurDAO.obtenirJoueurParPseudo(pseudo);
    }
    
    @Override
    public void mettreAJourJoueur(Joueur joueur) {
        joueurDAO.mettreAJourJoueur(joueur);
    }
}