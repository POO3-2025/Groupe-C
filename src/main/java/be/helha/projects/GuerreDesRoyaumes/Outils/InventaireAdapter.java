package be.helha.projects.GuerreDesRoyaumes.Outils;

import com.google.gson.*;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Slot;
import be.helha.projects.GuerreDesRoyaumes.Model.Items.Item;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Adaptateur Gson pour la sérialisation et désérialisation des objets Inventaire
 */
public class InventaireAdapter implements JsonSerializer<Inventaire>, JsonDeserializer<Inventaire> {

    @Override
    public JsonElement serialize(Inventaire src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();
        
        // Sérialiser les slots individuellement
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

    @Override
    public Inventaire deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        Inventaire inventaire = new Inventaire();
        
        // Désérialiser les slots
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