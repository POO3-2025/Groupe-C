package be.helha.projects.GuerreDesRoyaumes.Config;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/*
 * La classe ConfigReader est responsable de la lecture et de l'analyse d'un fichier de configuration JSON.
 *
 */
public class ConfigJsonReader {
    private static final String CONFIG_FILE = "db_config.json";
    private static ConfigJsonReader instance;
    private JsonObject config;

    /**
     * Constructeur de la classe ConfigReader.
     * Il charge le fichier de configuration JSON et l'analyse.
     */
    public ConfigJsonReader() {
        ReadConfig();
    }

    /**
     * Méthode pour obtenir l'instance unique de ConfigReader.
     * @return instance unique de ConfigReader.
     */
    public static ConfigJsonReader getInstance() {
        if (instance == null) {
            synchronized (ConfigJsonReader.class) {
                if (instance == null) {
                    instance = new ConfigJsonReader();
                }
            }
        }
        return instance;
    }

    /**
     * Méthode pour lire le fichier de configuration JSON.
     * Elle charge le fichier et l'analyse en un objet JsonObject.
     */
    private void ReadConfig() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE);
             InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {

            config = JsonParser.parseReader(reader).getAsJsonObject();

        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du chargement de " + CONFIG_FILE, e);
        }
    }

    /**
     * Méthode pour obtenir la configuration de la base de données.
     * @return JsonObject représentant la configuration de la base de données.
     */
    public JsonObject getDBConfig() {
        if (config == null) {
            throw new IllegalStateException("Le fichier de configuration n'a pas été chargé.");
        }
        return config;
    }

}
