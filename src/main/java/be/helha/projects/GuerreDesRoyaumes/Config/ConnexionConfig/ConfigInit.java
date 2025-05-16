package be.helha.projects.GuerreDesRoyaumes.Config.ConnexionConfig;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ConfigInit {
    public static void initAll() {
        try {
            Properties properties = new Properties();
            try (InputStream input = ConfigInit.class.getClassLoader().getResourceAsStream("application.properties")) {
                if (input != null) {
                    properties.load(input);
                } else {
                    try (FileInputStream fileInput = new FileInputStream("src/main/resources/application.properties")) {
                        properties.load(fileInput);
                    }
                }
            }

            // SQL
            String sqlUrl = properties.getProperty("spring.datasource.url");
            String sqlUsername = properties.getProperty("spring.datasource.username");
            String sqlPassword = properties.getProperty("spring.datasource.password");
            String sqlDriverClassName = properties.getProperty("spring.datasource.driver-class-name");
            SQLConfigManager.initialize(sqlUrl, sqlUsername, sqlPassword, sqlDriverClassName);

            // MongoDB
            String mongoUri = properties.getProperty("spring.data.mongodb.uri");
            if (mongoUri != null && !mongoUri.isEmpty()) {
                MongoDBConfigManager.initializeWithUri(mongoUri);
            } else {
                String mongoHost = properties.getProperty("spring.data.mongodb.host");
                String mongoPort = properties.getProperty("spring.data.mongodb.port");
                String mongoDatabaseName = properties.getProperty("spring.data.mongodb.database");
                String mongoUsername = properties.getProperty("spring.data.mongodb.username");
                String mongoPassword = properties.getProperty("spring.data.mongodb.password");
                MongoDBConfigManager.initialize(mongoHost, mongoPort, mongoDatabaseName, mongoUsername, mongoPassword);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erreur d'initialisation des configs : " + e.getMessage(), e);
        }
    }
}