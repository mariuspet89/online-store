package eu.accesa.onlinestore.configuration;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;

@Configuration
public class GridFSConfig {

    @Bean
    public GridFSBucket getGridFSBuckets(MongoDatabaseFactory mongoDbFactory) {
        return GridFSBuckets.create(mongoDbFactory.getMongoDatabase());
    }
}
