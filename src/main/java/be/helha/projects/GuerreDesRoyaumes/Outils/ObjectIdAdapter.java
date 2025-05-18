package be.helha.projects.GuerreDesRoyaumes.Outils;

import com.google.gson.*;
import org.bson.types.ObjectId;

import java.lang.reflect.Type;

/**
 * Adaptateur Gson pour la sérialisation et la désérialisation de la classe {@link ObjectId}
 * utilisée par MongoDB.
 * <p>
 * Convertit un {@link ObjectId} en chaîne hexadécimale lors de la sérialisation,
 * et inversement, supporte la désérialisation à partir d'une chaîne simple
 * ou d'un objet JSON contenant le champ "$oid".
 * </p>
 */
public class ObjectIdAdapter implements JsonSerializer<ObjectId>, JsonDeserializer<ObjectId> {

    /**
     * Sérialise un {@link ObjectId} en élément JSON.
     * <p>
     * Retourne une chaîne hexadécimale représentant l'ObjectId,
     * ou {@link JsonNull} si l'objet est null.
     * </p>
     *
     * @param src       L'objet ObjectId à sérialiser.
     * @param typeOfSrc Le type source.
     * @param context   Contexte de sérialisation Gson.
     * @return Un {@link JsonElement} représentant l'ObjectId.
     */
    @Override
    public JsonElement serialize(ObjectId src, Type typeOfSrc, JsonSerializationContext context) {
        if (src == null) {
            return JsonNull.INSTANCE;
        }
        return new JsonPrimitive(src.toHexString());
    }

    /**
     * Désérialise un élément JSON en {@link ObjectId}.
     * <p>
     * Supporte deux formats : un objet JSON avec un champ "$oid",
     * ou une chaîne simple.
     * </p>
     *
     * @param json     Élément JSON à désérialiser.
     * @param typeOfT  Type cible.
     * @param context  Contexte de désérialisation Gson.
     * @return L'ObjectId désérialisé.
     * @throws JsonParseException En cas d'erreur de parsing ou format inattendu.
     */
    @Override
    public ObjectId deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        if (json.isJsonNull()) {
            return null;
        }
        try {
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
