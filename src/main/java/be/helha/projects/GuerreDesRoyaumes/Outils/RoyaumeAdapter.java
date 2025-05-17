package be.helha.projects.GuerreDesRoyaumes.Outils;

import com.google.gson.*;
import be.helha.projects.GuerreDesRoyaumes.Model.Royaume;
import org.bson.types.ObjectId;

import java.lang.reflect.Type;

public class RoyaumeAdapter implements JsonSerializer<Royaume>, JsonDeserializer<Royaume> {
    @Override
    public JsonElement serialize(Royaume src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        
        // On ne sérialise pas l'id car il n'est pas utile
        // Sérialiser uniquement les champs utiles du royaume
        jsonObject.addProperty("nom", src.getNom());
        jsonObject.addProperty("niveau", src.getNiveau());
        
        return jsonObject;
    }

    @Override
    public Royaume deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        
        // Créer un nouveau royaume
        Royaume royaume = new Royaume();
        
        // Extraire les données et les assigner au royaume
        // Ignorer le champ id
        
        if (jsonObject.has("nom")) {
            royaume.setNom(jsonObject.get("nom").getAsString());
        }
        
        if (jsonObject.has("niveau")) {
            royaume.setNiveau(jsonObject.get("niveau").getAsInt());
        }
        
        return royaume;
    }
}