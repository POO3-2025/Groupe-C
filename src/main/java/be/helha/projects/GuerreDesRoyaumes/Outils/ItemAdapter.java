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
        String className;
        
        // Essayer d'abord de récupérer le champ itemClass
        if (jsonObject.has("itemClass")) {
            className = jsonObject.get("itemClass").getAsString();
        } 
        // Si itemClass n'existe pas, utiliser le champ type pour déterminer la classe
        else if (jsonObject.has("type")) {
            // Récupérer le type et le convertir en nom de classe
            String type = jsonObject.get("type").getAsString();
            System.out.println("Type trouvé: " + type);
            
            // Conversion du type en nom de classe - assumer que le type est le nom de la classe
            // Ex: "Arme" => "Arme"
            className = type;
        } 
        else {
            throw new JsonParseException("Impossible de déterminer le type d'item: ni itemClass ni type n'est présent");
        }
        
        try {
            System.out.println("Tentative de chargement de la classe: " + ITEM_PACKAGE + className);
            Class<?> clazz = Class.forName(ITEM_PACKAGE + className);
            return context.deserialize(jsonObject, clazz);
        } catch (ClassNotFoundException e) {
            System.err.println("Erreur de désérialisation: classe non trouvée " + className);
            e.printStackTrace();
            throw new JsonParseException("Classe d'item non trouvée: " + className, e);
        } catch (Exception e) {
            System.err.println("Erreur générale de désérialisation pour: " + jsonObject.toString());
            e.printStackTrace();
            throw new JsonParseException("Erreur lors de la désérialisation de l'item", e);
        }
    }
}