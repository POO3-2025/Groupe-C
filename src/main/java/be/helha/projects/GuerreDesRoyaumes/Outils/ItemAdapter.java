package be.helha.projects.GuerreDesRoyaumes.Outils;

import com.google.gson.*;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;

import java.lang.reflect.Type;

/**
 * Adaptateur Gson personnalisé pour la sérialisation et la désérialisation
 * des objets {@link Item} et leurs sous-classes.
 * <p>
 * Lors de la sérialisation, ajoute un champ "itemClass" indiquant le nom
 * de la classe concrète de l'objet pour faciliter la désérialisation.
 * </p>
 * <p>
 * Lors de la désérialisation, utilise la valeur de "itemClass" ou "type"
 * pour déterminer la classe concrète à instancier et désérialiser.
 * </p>
 * <p>
 * Gère les exceptions en cas de classe non trouvée ou d'erreur générale,
 * en levant une {@link JsonParseException}.
 * </p>
 */
public class ItemAdapter implements JsonSerializer<Item>, JsonDeserializer<Item> {

    private static final String ITEM_PACKAGE = "be.helha.projects.GuerreDesRoyaumes.Model.Items.";

    /**
     * Sérialise un objet {@link Item} en {@link JsonElement}.
     * <p>
     * Ajoute un champ "itemClass" avec le nom simple de la classe concrète.
     * </p>
     *
     * @param src        L'objet item à sérialiser.
     * @param typeOfSrc  Le type source.
     * @param context    Contexte de sérialisation Gson.
     * @return Un élément JSON représentant l'item avec information de classe.
     */
    @Override
    public JsonElement serialize(Item src, Type typeOfSrc, JsonSerializationContext context) {
        JsonElement jsonElement = context.serialize(src, src.getClass());
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        jsonObject.addProperty("itemClass", src.getClass().getSimpleName());
        return jsonObject;
    }

    /**
     * Désérialise un {@link JsonElement} en un objet {@link Item}.
     * <p>
     * Tente de déterminer la classe concrète via le champ "itemClass" ou
     * à défaut via le champ "type". Puis désérialise en conséquence.
     * </p>
     *
     * @param json       L'élément JSON à désérialiser.
     * @param typeOfT    Le type cible.
     * @param context    Contexte de désérialisation Gson.
     * @return L'objet Item désérialisé.
     * @throws JsonParseException En cas d'échec (classe non trouvée ou erreur).
     */
    @Override
    public Item deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String className;

        if (jsonObject.has("itemClass")) {
            className = jsonObject.get("itemClass").getAsString();
        } else if (jsonObject.has("type")) {
            className = jsonObject.get("type").getAsString();
        } else {
            throw new JsonParseException("Impossible de déterminer le type d'item: ni itemClass ni type n'est présent");
        }

        try {
            Class<?> clazz = Class.forName(ITEM_PACKAGE + className);
            return context.deserialize(jsonObject, clazz);
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Classe d'item non trouvée: " + className, e);
        } catch (Exception e) {
            throw new JsonParseException("Erreur lors de la désérialisation de l'item", e);
        }
    }
}
