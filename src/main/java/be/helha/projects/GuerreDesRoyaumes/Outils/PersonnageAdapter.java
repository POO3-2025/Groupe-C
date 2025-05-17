package be.helha.projects.GuerreDesRoyaumes.Outils;

import com.google.gson.*;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Golem;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Guerrier;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Titan;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Voleur;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;
import org.bson.types.ObjectId;

import java.lang.reflect.Type;

public class PersonnageAdapter implements JsonSerializer<Personnage>, JsonDeserializer<Personnage> {
    private static final String PERSONNAGE_PACKAGE = "be.helha.projects.GuerreDesRoyaumes.Model.Personnage.";

    @Override
    public JsonElement serialize(Personnage src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        
        // Ajouter les propriétés de base du personnage
        jsonObject.addProperty("nom", src.getNom());
        jsonObject.addProperty("vie", src.getVie());
        jsonObject.addProperty("degats", src.getDegats());
        jsonObject.addProperty("resistance", src.getResistance());
        jsonObject.addProperty("type", src.getClass().getSimpleName());
        
        // Ajouter l'inventaire si présent
        if (src.getInventaire() != null) {
            jsonObject.add("inventaire", context.serialize(src.getInventaire()));
        }
        
        return jsonObject;
    }

    @Override
    public Personnage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        
        // Récupérer le type de personnage (Golem, Guerrier, etc.)
        String className;
        if (jsonObject.has("type")) {
            className = jsonObject.get("type").getAsString();
        } else {
            throw new JsonParseException("Type de personnage non trouvé dans le JSON");
        }
        
        Personnage personnage;
        try {
            // Créer l'instance appropriée en fonction du type
            switch (className) {
                case "Golem":
                    personnage = new Golem();
                    break;
                case "Guerrier":
                    personnage = new Guerrier();
                    break;
                case "Titan":
                    personnage = new Titan();
                    break;
                case "Voleur":
                    personnage = new Voleur();
                    break;
                default:
                    throw new ClassNotFoundException("Type de personnage non reconnu: " + className);
            }
            
            // Définir les valeurs de base depuis le JSON
            if (jsonObject.has("nom")) {
                personnage.setNom(jsonObject.get("nom").getAsString());
            }
            if (jsonObject.has("vie")) {
                personnage.setVie(jsonObject.get("vie").getAsDouble());
            }
            if (jsonObject.has("degats")) {
                personnage.setDegats(jsonObject.get("degats").getAsDouble());
            }
            if (jsonObject.has("resistance")) {
                personnage.setResistance(jsonObject.get("resistance").getAsDouble());
            }
            
            // Gérer l'inventaire si présent
            if (jsonObject.has("inventaire") && !jsonObject.get("inventaire").isJsonNull()) {
                Inventaire inventaire = context.deserialize(jsonObject.get("inventaire"), Inventaire.class);
                personnage.setInventaire(inventaire);
            }
            
            return personnage;
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Classe de personnage non trouvée: " + className, e);
        }
    }
}