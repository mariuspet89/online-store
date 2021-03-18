package eu.accesa.onlinestore.configuration;

import com.mongodb.WriteConcern;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.WriteResultChecking;

@Configuration
public class MongoTemplateConfig {

    @Bean
    MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDbFactory) {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory);
        mongoTemplate.setWriteConcern(WriteConcern.MAJORITY);
        mongoTemplate.setWriteResultChecking(WriteResultChecking.EXCEPTION);
        return mongoTemplate;
    }
}
