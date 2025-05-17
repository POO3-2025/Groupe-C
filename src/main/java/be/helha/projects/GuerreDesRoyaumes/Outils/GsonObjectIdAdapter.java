package be.helha.projects.GuerreDesRoyaumes.Outils;

import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bson.types.ObjectId;

public class GsonObjectIdAdapter {

    public static Gson getGson() {
        // Création d'une instance Gson avec un adaptateur personnalisé pour ObjectId
        return new GsonBuilder()
                .registerTypeAdapter(ObjectId.class, new ObjectIdAdapter())
                .registerTypeAdapter(Item.class, new ItemAdapter())
                .create();
    }
}
