package be.helha.projects.GuerreDesRoyaumes.Outils;

import com.google.gson.*;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Slot;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Adaptateur Gson personnalisé pour la sérialisation et la désérialisation
 * des objets {@link Inventaire}.
 * <p>
 * Cette classe permet de convertir un inventaire en JSON et vice versa,
 * en gérant notamment la conversion des slots contenant des items et leur quantité.
 * </p>
 */
public class InventaireAdapter implements JsonSerializer<Inventaire>, JsonDeserializer<Inventaire> {

    /**
     * Sérialise un objet {@link Inventaire} en {@link JsonElement}.
     * <p>
     * Chaque slot est converti en objet JSON avec un item et une quantité.
     * Les slots vides ou items nuls sont gérés correctement.
     * </p>
     *
     * @param src        L'inventaire à sérialiser.
     * @param typeOfSrc  Le type source.
     * @param context    Contexte de sérialisation Gson.
     * @return Un {@link JsonElement} représentant l'inventaire.
     */
    @Override
    public JsonElement serialize(Inventaire src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        if (src.getSlots() != null) {
            JsonArray slotsArray = new JsonArray();
            for (Slot slot : src.getSlots()) {
                if (slot != null) {
                    JsonObject slotObject = new JsonObject();
                    if (slot.getItem() != null) {
                        slotObject.add("item", context.serialize(slot.getItem()));
                    } else {
                        slotObject.add("item", JsonNull.INSTANCE);
                    }
                    slotObject.addProperty("quantity", slot.getQuantity());
                    slotsArray.add(slotObject);
                }
            }
            jsonObject.add("slots", slotsArray);
        }

        return jsonObject;
    }

    /**
     * Désérialise un {@link JsonElement} en objet {@link Inventaire}.
     * <p>
     * Reconstruit la liste des slots avec les items et quantités associées.
     * </p>
     *
     * @param json       L'élément JSON à désérialiser.
     * @param typeOfT    Le type cible.
     * @param context    Contexte de désérialisation Gson.
     * @return L'objet {@link Inventaire} reconstruit.
     * @throws JsonParseException En cas d'erreur de parsing JSON.
     */
    @Override
    public Inventaire deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Inventaire inventaire = new Inventaire();

        if (jsonObject.has("slots") && !jsonObject.get("slots").isJsonNull()) {
            JsonArray slotsArray = jsonObject.getAsJsonArray("slots");
            List<Slot> slots = new ArrayList<>();

            for (JsonElement slotElement : slotsArray) {
                JsonObject slotObject = slotElement.getAsJsonObject();

                Item item = null;
                if (slotObject.has("item") && !slotObject.get("item").isJsonNull()) {
                    item = context.deserialize(slotObject.get("item"), Item.class);
                }

                int quantity = 0;
                if (slotObject.has("quantity")) {
                    quantity = slotObject.get("quantity").getAsInt();
                }

                Slot slot = new Slot(item, quantity);
                slots.add(slot);
            }

            inventaire.setSlots(slots);
        }

        return inventaire;
    }
}
