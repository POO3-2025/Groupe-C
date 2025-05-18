package be.helha.projects.GuerreDesRoyaumes.Controller;

import be.helha.projects.GuerreDesRoyaumes.DAO.CombatDAO;
import be.helha.projects.GuerreDesRoyaumes.Model.*;
import be.helha.projects.GuerreDesRoyaumes.Model.Combat.ActionTour;
import be.helha.projects.GuerreDesRoyaumes.Model.Combat.Combat;
import be.helha.projects.GuerreDesRoyaumes.Model.Competence_Combat.*;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Coffre;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Slot;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Arme;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;
import be.helha.projects.GuerreDesRoyaumes.Service.ServiceCombat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/combat")
public class CombatController {

    private final ServiceCombat serviceCombat;

    private final CombatDAO combatDAO;
    private Joueur joueur1;
    private Joueur joueur2;
    private Joueur joueurActuel;
    private Combat combatEnCours;
    private List<ActionTour> actionTours;

    @Autowired
    public CombatController(ServiceCombat serviceCombat, CombatDAO combatDAO, Joueur joueur1, Joueur joueur2) {
        this.serviceCombat = serviceCombat;
        this.combatDAO = combatDAO;
        this.joueur1 = joueur1;
        this.joueur2 = joueur2;
        this.actionTours = new ArrayList<>();
    }

    @PostMapping("/creerCombat")
    public ResponseEntity<Combat> creerCombat(@RequestBody Joueur[] joueurs) {
        System.out.println("Création du combat avec les joueurs : " + joueurs[0].getPseudo() + " et " + joueurs[1].getPseudo());
        try {
            // Vérifier l'initialisation des personnages
            if(joueurs[0].getPersonnage() == null || joueurs[1].getPersonnage() == null) {
                throw new IllegalStateException("Personnage non initialisé");
            }

            Combat combat = new Combat(0, joueurs[0], joueurs[1], null, 0, LocalDateTime.now());
            combatDAO.enregistrerCombat(combat);
            return ResponseEntity.ok(combat);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/{id}/tour")
    public ResponseEntity<String> executerTour(
            @PathVariable int id,
            @RequestBody ActionTour action) {

        Combat combat = combatDAO.obtenirCombatParId(id);
        if (combat == null) {
            return ResponseEntity.notFound().build();
        }

        String resultat = serviceCombat.executerAction(combat.getJoueur1(), combat.getJoueur2(),
                action.getAction(), combat.getNombreTours());
        return ResponseEntity.ok(resultat);
    }


    public Combat enregistrerCombat(Combat combat) {
        // Logique d'enregistrement
        return combat; // Retourne l'objet enregistré
    }
    // Initialisation du combat
    public void initialiserCombat() {
        this.combatEnCours = new Combat(0, joueur1, joueur2, null, 0, java.time.LocalDateTime.now());
        serviceCombat.initialiserCombat(joueur1, joueur2);
        preparerInventairesCombat();
    }

    // Initialisation du combat
    public void initialiserCombat(Joueur joueur1, Joueur joueur2) {
        serviceCombat.initialiserCombat(joueur1, joueur2);
        this.combatEnCours = new Combat(0, joueur1, joueur2, null, 0, java.time.LocalDateTime.now());
        preparerInventairesCombat();
    }

    // Pour accéder au joueur actuel
    public Joueur getJoueurActuel() {
        if (combatEnCours == null) return null;

        return (combatEnCours.getNombreTours() % 2 == 0)
                ? combatEnCours.getJoueur1()
                : combatEnCours.getJoueur2();
    }

    private void preparerInventairesCombat() {
        transfererItemsCoffreVersInventaire(combatEnCours.getJoueur1());
        transfererItemsCoffreVersInventaire(combatEnCours.getJoueur2());
    }

    public List<Competence> getCompetencesBonusDisponibles() {
        return List.of(
                new DoubleDegats(),
                new DoubleResistance(),
                new Regeneration(),
                new DoubleArgent()
        );
    }

    public void transfererItemsCoffreVersInventaire(Joueur joueur) {
        if(joueur.getPersonnage() == null) {
            throw new IllegalStateException("Personnage non initialisé pour " + joueur.getPseudo());
        }

        Coffre coffre = joueur.getCoffre();
        Inventaire inventaire = joueur.getPersonnage().getInventaire();

        if (inventaire == null) {
            inventaire = new Inventaire();
            joueur.getPersonnage().setInventaire(inventaire);
        }

        for (Slot slot : coffre.getSlots()) {
            if (slot.getItem() != null && slot.getQuantity() > 0) {
                inventaire.ajouterItem(slot.getItem(), slot.getQuantity());
                coffre.enleverItem(slot.getItem(), slot.getQuantity());
            }
        }
    }

    public void transfererItemsCoffreVersInventaire(Joueur joueur, Item item, int quantity) {
        Coffre coffre = joueur.getCoffre();
        Inventaire inventaire_Combat = joueur.getPersonnage().getInventaire();

        if (coffre.enleverItem(item, quantity)) {
            inventaire_Combat.ajouterItem(item, quantity);
        }
    }

    // Gestion des compétences
    public void acheterCompetence(Joueur joueur, Competence competence) {
        if (joueur.getArgent() >= competence.getPrix()) {
            joueur.retirerArgent(competence.getPrix());
            competence.appliquerEffet(joueur.getPersonnage());
        }
    }

    public void acheterCompetence(Competence competence) {
        this.acheterCompetence(this.getJoueurActuel(), competence);
    }

    // Gestion des tours
    public void executerTour(ActionTour actionJoueur1, ActionTour actionJoueur2) {
        resoudreActions(actionJoueur1, actionJoueur2);
        combatEnCours.setNombreTours(combatEnCours.getNombreTours() + 1);

        // Vérifier si un joueur a utilisé une compétence
        if (actionJoueur1.getCompetenceUtilisee() != null) {
            actionJoueur1.getCompetenceUtilisee().appliquerEffet(actionJoueur1.getJoueur().getPersonnage());
        }
        if (actionJoueur2.getCompetenceUtilisee() != null) {
            actionJoueur2.getCompetenceUtilisee().appliquerEffet(actionJoueur2.getJoueur().getPersonnage());
        }

        if (checkFinCombat()) {
            terminerCombat();
        }
    }


    private void resoudreActions(ActionTour action1, ActionTour action2) {
        Personnage p1 = action1.getJoueur().getPersonnage();
        Personnage p2 = action2.getJoueur().getPersonnage();

        if ("Attaque".equals(action1.getAction())) {
            int degats = calculerDegats(p1, p2);
            p2.setPointsDeVie(p2.getPointsDeVie() - degats);

            if (estMort(p2)) {
                System.out.println(p2.getNom() + " est mort !");
            }
        }

        if (action1.getCompetenceUtilisee() != null) {
            action1.getCompetenceUtilisee().appliquerEffet(p1);
        }
    }

    private int calculerDegats(Personnage attaquant, Personnage defenseur) {
        double degatsAttaquant = attaquant.getDegats();
        double resistanceDefenseur = defenseur.getResistance();

        // Récupérer l'arme dans l'inventaire
        Arme arme = null;
        for (Slot slot : attaquant.getInventaire().getSlots()) {
            if (slot.getItem() instanceof Arme) {
                arme = (Arme) slot.getItem();
                break;
            }
        }

        if (arme != null) {
            degatsAttaquant += arme.getDegats();
        }

        double degatsFinals = degatsAttaquant * (1 - resistanceDefenseur / 100.0);
        return (int) Math.round(degatsFinals); // Conversion en entier
    }

    private boolean estMort(Personnage personnage) {
        return personnage.getPointsDeVie() <= 0;
    }
    // Gestion fin de combat
    private boolean checkFinCombat() {
        return combatEnCours.getNombreTours() >= 5 ||
                combatEnCours.getJoueur1().getPersonnage().getPointsDeVie() <= 0 ||
                combatEnCours.getJoueur2().getPersonnage().getPointsDeVie() <= 0;
    }

    private void terminerCombat() {
        determinerVainqueur();
        restituerItemsInventaires();
        enregistrerResultats();
    }

    private void determinerVainqueur() {
        Joueur j1 = combatEnCours.getJoueur1();
        Joueur j2 = combatEnCours.getJoueur2();

        if (j1.getPersonnage().getPointsDeVie() == j2.getPersonnage().getPointsDeVie()) {
            combatEnCours.setVainqueur(null);
        } else {
            combatEnCours.setVainqueur(j1.getPersonnage().getPointsDeVie() > j2.getPersonnage().getPointsDeVie() ? j1 : j2);
        }
    }

    private void restituerItemsInventaires() {
        transfererItemsInventaireVersCoffre(combatEnCours.getJoueur1());
        transfererItemsInventaireVersCoffre(combatEnCours.getJoueur2());
    }

    private void transfererItemsInventaireVersCoffre(Joueur joueur) {
        Coffre coffre = joueur.getCoffre();
        Inventaire inventaire = joueur.getPersonnage().getInventaire();

        for (Slot slot : inventaire.getSlots()) {
            if (slot.getItem() != null && slot.getQuantity() > 0) {
                coffre.ajouterItem(slot.getItem(), slot.getQuantity());
                inventaire.enleverItem(slot.getItem(), slot.getQuantity());
            }
        }


    }

    private void transfererItemsInventaireVersCoffre(Joueur joueur, Item item, int quantity) {
        Coffre coffre = joueur.getCoffre();
        Inventaire inventaire_Combat = joueur.getPersonnage().getInventaire();

        if (inventaire_Combat.enleverItem(item, quantity)) {
            coffre.ajouterItem(item, quantity);
        }
    }

    private void enregistrerResultats() {
        combatDAO.enregistrerCombat(combatEnCours);

        if (combatEnCours.getVainqueur() != null) {
            combatDAO.enregistrerVictoire(combatEnCours.getVainqueur());
            combatEnCours.getVainqueur().ajouterVictoire();

            Joueur perdant = combatEnCours.getVainqueur().equals(combatEnCours.getJoueur1()) ?
                    combatEnCours.getJoueur2() : combatEnCours.getJoueur1();
            combatDAO.enregistrerDefaite(perdant);
            perdant.ajouterDefaite();
        }
    }

    // Getters pour l'UI
    public Combat getCombatEnCours() {
        if (combatEnCours == null) {
            throw new IllegalStateException("Le combat n'a pas été initialisé.");
        }
        return combatEnCours;
    }


    public String getStatutCombat() {
        if (combatEnCours == null) return "Combat non initialisé";

        return "Tour " + combatEnCours.getNombreTours() + "/5\n" +
                combatEnCours.getJoueur1().getPseudo() + ": " +
                combatEnCours.getJoueur1().getPersonnage().getPointsDeVie() + " PV\n" +
                combatEnCours.getJoueur2().getPseudo() + ": " +
                combatEnCours.getJoueur2().getPersonnage().getPointsDeVie() + " PV";
    }
}