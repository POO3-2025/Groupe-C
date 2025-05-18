package be.helha.projects.GuerreDesRoyaumes.Config;

import be.helha.projects.GuerreDesRoyaumes.Config.ConnexionConfig.MongoDBConfigManager;
import com.mongodb.client.MongoDatabase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {

    @Autowired
    private MongoDBConfigManager mongoDBConfigManager;

    @Bean
    public MongoDatabase mongoDatabase() {
        return mongoDBConfigManager.getDatabase();
    }
} 