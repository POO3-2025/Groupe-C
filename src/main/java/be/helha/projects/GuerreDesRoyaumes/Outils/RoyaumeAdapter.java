package be.helha.projects.GuerreDesRoyaumes.Outils;

import com.google.gson.*;
import be.helha.projects.GuerreDesRoyaumes.Model.Royaume;

import java.lang.reflect.Type;

/**
 * Adaptateur Gson personnalisé pour la sérialisation et la désérialisation
 * des objets {@link Royaume}.
 * <p>
 * Lors de la sérialisation, le champ `id` n'est pas pris en compte,
 * seuls les champs `nom` et `niveau` sont sérialisés.
 * </p>
 * <p>
 * Lors de la désérialisation, un nouvel objet {@link Royaume} est créé
 * et ses champs `nom` et `niveau` sont initialisés à partir du JSON.
 * </p>
 */
public class RoyaumeAdapter implements JsonSerializer<Royaume>, JsonDeserializer<Royaume> {

    /**
     * Sérialise un objet {@link Royaume} en {@link JsonElement}.
     *
     * @param src        Le royaume à sérialiser.
     * @param typeOfSrc  Le type source.
     * @param context    Contexte de sérialisation Gson.
     * @return Un élément JSON représentant le royaume (sans l'id).
     */
    @Override
    public JsonElement serialize(Royaume src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("nom", src.getNom());
        jsonObject.addProperty("niveau", src.getNiveau());
        return jsonObject;
    }

    /**
     * Désérialise un {@link JsonElement} en objet {@link Royaume}.
     *
     * @param json       L'élément JSON à désérialiser.
     * @param typeOfT    Le type cible.
     * @param context    Contexte de désérialisation Gson.
     * @return Le royaume désérialisé.
     * @throws JsonParseException En cas d'erreur de parsing.
     */
    @Override
    public Royaume deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Royaume royaume = new Royaume();

        if (jsonObject.has("nom")) {
            royaume.setNom(jsonObject.get("nom").getAsString());
        }
        if (jsonObject.has("niveau")) {
            royaume.setNiveau(jsonObject.get("niveau").getAsInt());
        }

        return royaume;
    }
}
