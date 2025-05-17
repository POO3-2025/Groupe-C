package be.helha.projects.GuerreDesRoyaumes.Reseau;

import be.helha.projects.GuerreDesRoyaumes.Model.Joueur;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Gestionnaire de communication réseau pour le jeu Guerre des Royaumes.
 * Cette classe permet l'échange de messages entre l'hôte et le client lors des combats.
 */
public class GestionnaireReseau {
    private static final Logger logger = LoggerFactory.getLogger(GestionnaireReseau.class);
    private static final int PORT_PAR_DEFAUT = 12345;

    // Mode développement pour simuler la connexion réseau
    private static final boolean MODE_DEVELOPPEMENT = true;

    private boolean estHote;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private ObjectInputStream entree;
    private ObjectOutputStream sortie;
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    private Map<String, CompletableFuture<Object>> attentesReponses = new HashMap<>();

    /**
     * Initialise le gestionnaire en tant qu'hôte (serveur)
     * @return true si l'initialisation a réussi
     */
    public boolean demarrerEnTantQuHote() {
        // En mode développement, simuler une connexion réussie
        if (MODE_DEVELOPPEMENT) {
            estHote = true;
            logger.info("Mode développement: Simulation d'un serveur démarré avec succès");
            return true;
        }

        // Code réel pour une connexion réseau
        try {
            estHote = true;
            serverSocket = new ServerSocket(PORT_PAR_DEFAUT);
            logger.info("Serveur démarré sur le port {}", PORT_PAR_DEFAUT);

            // Attendre la connexion du client (bloquant)
            clientSocket = serverSocket.accept();
            logger.info("Client connecté depuis {}", clientSocket.getInetAddress());

            // Initialiser les flux d'entrée/sortie
            sortie = new ObjectOutputStream(clientSocket.getOutputStream());
            entree = new ObjectInputStream(clientSocket.getInputStream());

            // Démarrer l'écoute des messages
            demarrerEcouteur();

            return true;
        } catch (IOException e) {
            logger.error("Erreur lors du démarrage du serveur", e);
            return false;
        }
    }

    /**
     * Initialise le gestionnaire en tant que client
     * @param adresseHote Adresse IP du serveur hôte
     * @return true si la connexion a réussi
     */
    public boolean connecterAuServeur(String adresseHote) {
        // En mode développement, simuler une connexion réussie
        if (MODE_DEVELOPPEMENT) {
            estHote = false;
            logger.info("Mode développement: Simulation d'une connexion client réussie");
            return true;
        }

        // Code réel pour une connexion réseau
        try {
            estHote = false;
            clientSocket = new Socket(adresseHote, PORT_PAR_DEFAUT);
            logger.info("Connecté au serveur {}", adresseHote);

            // Initialiser les flux d'entrée/sortie
            sortie = new ObjectOutputStream(clientSocket.getOutputStream());
            entree = new ObjectInputStream(clientSocket.getInputStream());

            // Démarrer l'écoute des messages
            demarrerEcouteur();

            return true;
        } catch (IOException e) {
            logger.error("Erreur lors de la connexion au serveur", e);
            return false;
        }
    }

    /**
     * Démarre un thread d'écoute des messages entrants
     */
    private void demarrerEcouteur() {
        // En mode développement, ne pas démarrer d'écouteur
        if (MODE_DEVELOPPEMENT) {
            logger.info("Mode développement: Pas d'écouteur démarré");
            return;
        }

        executorService.submit(() -> {
            try {
                while (!Thread.currentThread().isInterrupted() && clientSocket != null && !clientSocket.isClosed()) {
                    Object message = entree.readObject();
                    traiterMessage(message);
                }
            } catch (IOException | ClassNotFoundException e) {
                logger.error("Erreur lors de la réception d'un message", e);
            }
        });
    }

    /**
     * Traite un message reçu
     * @param message Le message reçu
     */
    private void traiterMessage(Object message) {
        if (message instanceof MessageCombat) {
            MessageCombat messageCombat = (MessageCombat) message;
            logger.info("Message reçu: {}", messageCombat.getType());

            // Si c'est une réponse à un message précédent
            if (messageCombat.getIdMessage() != null && attentesReponses.containsKey(messageCombat.getIdMessage())) {
                CompletableFuture<Object> future = attentesReponses.get(messageCombat.getIdMessage());
                future.complete(messageCombat);
                attentesReponses.remove(messageCombat.getIdMessage());
            }

            // Traitement selon le type de message
            switch (messageCombat.getType()) {
                case ACTION:
                    // Traiter l'action de combat
                    break;
                case RESULTAT:
                    // Traiter le résultat de l'action
                    break;
                case FIN_COMBAT:
                    // Traiter la fin du combat
                    break;
            }
        }
    }

    /**
     * Envoie un message à l'autre partie
     * @param message Le message à envoyer
     * @return true si l'envoi a réussi
     */
    public boolean envoyerMessage(Object message) {
        // En mode développement, simuler un envoi réussi
        if (MODE_DEVELOPPEMENT) {
            logger.info("Mode développement: Simulation d'un envoi de message réussi");
            return true;
        }

        try {
            sortie.writeObject(message);
            sortie.flush();
            return true;
        } catch (IOException e) {
            logger.error("Erreur lors de l'envoi d'un message", e);
            return false;
        }
    }

    /**
     * Envoie un message et attend une réponse
     * @param message Le message à envoyer
     * @param idMessage L'identifiant du message pour associer la réponse
     * @return Un CompletableFuture qui sera complété quand la réponse arrivera
     */
    public CompletableFuture<Object> envoyerEtAttendreReponse(MessageCombat message, String idMessage) {
        CompletableFuture<Object> future = new CompletableFuture<>();
        message.setIdMessage(idMessage);

        // En mode développement, simuler une réponse immédiate
        if (MODE_DEVELOPPEMENT) {
            logger.info("Mode développement: Simulation d'une réponse immédiate");

            // Créer une réponse simulée
            MessageCombat reponse = new MessageCombat(MessageCombat.Type.ACTION);
            reponse.setIdMessage(idMessage);

            // Si c'est un message d'action de combat, simuler une action adverse
            if (message.getType() == MessageCombat.Type.ACTION) {
                // Alterner entre attaque et défense comme action simulée
                String typeAction = Math.random() > 0.5 ? "attaque" : "defense";
                reponse.ajouterDonnee("typeAction", typeAction);
                logger.info("Action simulée pour l'adversaire: {}", typeAction);
            }

            // Compléter le future après un court délai pour simuler la latence réseau
            executorService.submit(() -> {
                try {
                    Thread.sleep(500); // Délai simulé de 500 ms
                    future.complete(reponse);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            return future;
        }

        // Code réel pour une connexion réseau
        attentesReponses.put(idMessage, future);
        envoyerMessage(message);
        return future;
    }

    /**
     * Envoie les informations d'action de combat
     * @param joueur Le joueur qui effectue l'action
     * @param typeAction Le type d'action (attaque, defense, special)
     * @return Un CompletableFuture qui sera complété avec la réponse
     */
    public CompletableFuture<Object> envoyerAction(Joueur joueur, String typeAction) {
        MessageCombat message = new MessageCombat(MessageCombat.Type.ACTION);
        message.ajouterDonnee("joueurId", joueur.getId());
        message.ajouterDonnee("typeAction", typeAction);

        String idMessage = "action-" + System.currentTimeMillis();
        return envoyerEtAttendreReponse(message, idMessage);
    }

    /**
     * Ferme toutes les connexions
     */
    public void fermer() {
        // En mode développement, rien à fermer
        if (MODE_DEVELOPPEMENT) {
            logger.info("Mode développement: Pas de connexions à fermer");
            return;
        }

        try {
            executorService.shutdownNow();

            if (entree != null) entree.close();
            if (sortie != null) sortie.close();
            if (clientSocket != null) clientSocket.close();
            if (serverSocket != null) serverSocket.close();

            logger.info("Connexions fermées");
        } catch (IOException e) {
            logger.error("Erreur lors de la fermeture des connexions", e);
        }
    }
}