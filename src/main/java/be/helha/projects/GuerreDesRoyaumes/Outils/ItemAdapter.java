package be.helha.projects.GuerreDesRoyaumes.Outils;

import com.google.gson.*;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;
import java.lang.reflect.Type;

public class ItemAdapter implements JsonSerializer<Item>, JsonDeserializer<Item> {
    private static final String ITEM_PACKAGE = "be.helha.projects.GuerreDesRoyaumes.Model.Items.";

    @Override
    public JsonElement serialize(Item src, Type typeOfSrc, JsonSerializationContext context) {
        JsonElement jsonElement = context.serialize(src, src.getClass());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        jsonObject.addProperty("itemClass", src.getClass().getSimpleName());
        return jsonObject;
    }

    @Override
    public Item deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String className = jsonObject.get("itemClass").getAsString();
        try {
            Class<?> clazz = Class.forName(ITEM_PACKAGE + className);
            return context.deserialize(jsonObject, clazz);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Classe d'item non trouvée: " + className, e);
        }
    }
}