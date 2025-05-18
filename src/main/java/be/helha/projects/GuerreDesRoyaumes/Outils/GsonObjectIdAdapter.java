package be.helha.projects.GuerreDesRoyaumes.Outils;

import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;
import be.helha.projects.GuerreDesRoyaumes.Model.Royaume;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bson.types.ObjectId;

/**
 * Classe utilitaire fournissant une instance configurée de {@link Gson}
 * adaptée à la sérialisation et désérialisation des objets spécifiques
 * au jeu Guerre des Royaumes, notamment pour une intégration avec MongoDB.
 * <p>
 * Cette classe gère la configuration de Gson avec des adaptateurs personnalisés
 * pour les types complexes comme {@link ObjectId}, {@link Item}, {@link Personnage},
 * {@link Royaume} et {@link Inventaire}.
 * </p>
 * <p>
 * Utilise un singleton pour partager une instance optimisée.
 * </p>
 */
public class GsonObjectIdAdapter {

    private static Gson instance;

    /**
     * Retourne une instance singleton de {@link Gson} configurée pour MongoDB
     * et les types du jeu.
     * <p>
     * Configure Gson pour sérialiser les valeurs nulles, désactive l'échappement HTML,
     * et enregistre les adaptateurs personnalisés nécessaires.
     * </p>
     *
     * @return Une instance Gson configurée.
     */
    public static synchronized Gson getGson() {
        if (instance == null) {
            instance = new GsonBuilder()
                    .serializeNulls()
                    .disableHtmlEscaping()
                    .registerTypeAdapter(ObjectId.class, new ObjectIdAdapter())
                    .registerTypeAdapter(Item.class, new ItemAdapter())
                    .registerTypeAdapter(Personnage.class, new PersonnageAdapter())
                    .registerTypeAdapter(Royaume.class, new RoyaumeAdapter())
                    .registerTypeAdapter(Inventaire.class, new InventaireAdapter())
                    .create();
        }
        return instance;
    }
}
