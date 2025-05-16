package be.helha.projects.GuerreDesRoyaumes.Outils;

import com.google.gson.*;
import org.bson.types.ObjectId;
import java.lang.reflect.Type;

public class ObjectIdAdapter implements JsonSerializer<ObjectId>, JsonDeserializer<ObjectId> {

    @Override
    public JsonElement serialize(ObjectId src, Type typeOfSrc, JsonSerializationContext context) {
        // Convertit l'ObjectId en chaîne hexadécimale pour la sérialisation JSON
        return new JsonPrimitive(src.toHexString());
    }

    @Override
    public ObjectId deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        // Reconstruit l'ObjectId à partir de sa représentation en chaîne JSON
        return new ObjectId(json.getAsString());
    }
}
