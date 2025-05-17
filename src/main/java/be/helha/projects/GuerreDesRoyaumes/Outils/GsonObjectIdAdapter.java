package be.helha.projects.GuerreDesRoyaumes.Outils;

import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;
import be.helha.projects.GuerreDesRoyaumes.Model.Royaume;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bson.types.ObjectId;

/**
 * Classe utilitaire pour créer des instances Gson configurées pour MongoDB
 */
public class GsonObjectIdAdapter {

    private static Gson instance;

    /**
     * Obtient une instance Gson configurée pour la sérialisation/désérialisation avec MongoDB
     * Tous les adaptateurs nécessaires sont enregistrés pour gérer les types spécifiques au jeu
     * @return Une instance Gson optimisée pour MongoDB
     */
    public static synchronized Gson getGson() {
        if (instance == null) {
            instance = new GsonBuilder()
                    .serializeNulls() // Sérialise les valeurs null
                    .disableHtmlEscaping() // Permet les caractères spéciaux sans échappement
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
