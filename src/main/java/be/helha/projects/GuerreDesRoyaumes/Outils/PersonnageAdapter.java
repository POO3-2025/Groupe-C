package be.helha.projects.GuerreDesRoyaumes.Outils;

import com.google.gson.*;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Personnage;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Golem;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Guerrier;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Titan;
import be.helha.projects.GuerreDesRoyaumes.Model.Personnage.Voleur;
import be.helha.projects.GuerreDesRoyaumes.Model.Inventaire.Inventaire;

import java.lang.reflect.Type;

/**
 * Adaptateur Gson personnalisé pour la sérialisation et la désérialisation
 * des objets {@link Personnage} et leurs sous-classes.
 * <p>
 * Lors de la sérialisation, convertit un personnage en JSON en ajoutant ses
 * propriétés de base et son type concret.
 * </p>
 * <p>
 * Lors de la désérialisation, crée une instance concrète selon le type,
 * puis remplit ses attributs et son inventaire.
 * </p>
 * <p>
 * Supporte les types : {@link Golem}, {@link Guerrier}, {@link Titan}, {@link Voleur}.
 * </p>
 */
public class PersonnageAdapter implements JsonSerializer<Personnage>, JsonDeserializer<Personnage> {

    /**
     * Sérialise un objet {@link Personnage} en {@link JsonElement}.
     *
     * @param src        Le personnage à sérialiser.
     * @param typeOfSrc  Le type source.
     * @param context    Contexte de sérialisation Gson.
     * @return Un {@link JsonElement} représentant le personnage.
     */
    @Override
    public JsonElement serialize(Personnage src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("nom", src.getNom());
        jsonObject.addProperty("vie", src.getVie());
        jsonObject.addProperty("degats", src.getDegats());
        jsonObject.addProperty("resistance", src.getResistance());
        jsonObject.addProperty("type", src.getClass().getSimpleName());

        if (src.getInventaire() != null) {
            jsonObject.add("inventaire", context.serialize(src.getInventaire()));
        }

        return jsonObject;
    }

    /**
     * Désérialise un {@link JsonElement} en objet {@link Personnage}.
     *
     * @param json       L'élément JSON à désérialiser.
     * @param typeOfT    Le type cible.
     * @param context    Contexte de désérialisation Gson.
     * @return Le personnage désérialisé.
     * @throws JsonParseException Si le type n'est pas reconnu ou si une erreur survient.
     */
    @Override
    public Personnage deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        if (!jsonObject.has("type")) {
            throw new JsonParseException("Type de personnage non trouvé dans le JSON");
        }

        String className = jsonObject.get("type").getAsString();

        Personnage personnage;
        try {
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
