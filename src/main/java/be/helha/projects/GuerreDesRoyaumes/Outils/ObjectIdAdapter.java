package be.helha.projects.GuerreDesRoyaumes.Outils;

import com.google.gson.*;
import org.bson.types.ObjectId;
import java.lang.reflect.Type;

public class ObjectIdAdapter implements JsonSerializer<ObjectId>, JsonDeserializer<ObjectId> {

    @Override
    public JsonElement serialize(ObjectId src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) {
            return JsonNull.INSTANCE;
        }
        // Convertit l'ObjectId en chaîne hexadécimale pour la sérialisation JSON
        return new JsonPrimitive(src.toHexString());
    }

    @Override
    public ObjectId deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonNull()) {
            return null;
        }
        try {
            // Deux formats possibles: objet JSON avec $oid ou chaîne simple
            if (json.isJsonObject() && json.getAsJsonObject().has("$oid")) {
                return new ObjectId(json.getAsJsonObject().get("$oid").getAsString());
            } else {
                return new ObjectId(json.getAsString());
            }
        } catch (Exception e) {
            throw new JsonParseException("Impossible de convertir en ObjectId: " + json, e);
        }
    }
}
