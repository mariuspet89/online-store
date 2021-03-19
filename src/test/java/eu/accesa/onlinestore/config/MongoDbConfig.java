package eu.accesa.onlinestore.config;

import com.mongodb.WriteConcern;
import com.mongodb.client.MongoClients;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import eu.accesa.onlinestore.repository.UserRepository;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactoryBean;

@TestConfiguration
public class MongoDbConfig implements InitializingBean, DisposableBean {

    private static final String CONNECTION_URL = "mongodb://%s:%d";

    private static String HOST = "dev096.dev.cloud.accesa.eu";
    private static int PORT = 27019;

    MongodExecutable mongodExecutable;

    @Override
    public void afterPropertiesSet() throws Exception {
        IMongodConfig mongodConfig = new MongodConfigBuilder()
                .version(Version.Main.PRODUCTION)
                .net(new Net(HOST, PORT, Network.localhostIsIPv6()))
                .build();

        MongodStarter starter = MongodStarter.getDefaultInstance();
        mongodExecutable = starter.prepare(mongodConfig);
        mongodExecutable.start();
    }

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory() {
        // also possible to connect to a remote or real MongoDB instance
        return new SimpleMongoClientDatabaseFactory(MongoClients
                .create(String.format(CONNECTION_URL, HOST, PORT)), "test");
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoDatabaseFactory mongoDatabaseFactory) {
        MongoTemplate mongoTemplate = new MongoTemplate(mongoDatabaseFactory);
        mongoTemplate.setWriteConcern(WriteConcern.ACKNOWLEDGED);
        return mongoTemplate;
    }

    @Bean
    public MongoRepositoryFactoryBean mongoRepositoryFactoryBean(MongoTemplate mongoTemplate) {
        MongoRepositoryFactoryBean mongoRepositoryFactoryBean = new MongoRepositoryFactoryBean(UserRepository.class);
        mongoRepositoryFactoryBean.setMongoOperations(mongoTemplate);
        return mongoRepositoryFactoryBean;
    }

    @Override
    public void destroy() throws Exception {
        mongodExecutable.stop();
    }
}
