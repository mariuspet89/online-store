package eu.accesa.onlinestore.utils.mongodb;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

public class MongoSpringExtension implements BeforeEachCallback, AfterEachCallback {

    private static final Path JSON_PATH = Paths.get("src", "test", "resources", "data");
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Called before each test executes. This callback is responsible for importing the JSON document, defined by the
     * MongoDataFile annotation, into the embedded MongoDB, through the provided MongoTemplate.
     *
     * @param context the ExtensionContext, which provides access to the test method
     * @throws Exception if an error occurs retrieving the test method or extracting the MongoDataFile annotation
     */
    @Override
    public void beforeEach(ExtensionContext context) throws Exception {
        context.getTestMethod().ifPresent(method -> {
            // load test file from the annotation
            MongoDataFile mongoDataFile = method.getAnnotation(MongoDataFile.class);

            // load the MongoTemplate that we can use to import our data
            getMongoTemplate(context).ifPresent(mongoTemplate -> {
                try {
                    // Use Jackson's ObjectMapper to load a list of objects from the JSON file
                    List objects = objectMapper.readValue(JSON_PATH.resolve(mongoDataFile.value()).toFile(),
                            objectMapper.getTypeFactory().constructCollectionType(List.class, mongoDataFile.classType()));

                    // save each object into DB
                    objects.forEach(mongoTemplate::save);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        });
    }

    /**
     * Called after each test executes. This callback is responsible for dropping the test's MongoDB collection
     * so that the next test that runs is clean.
     *
     * @param context The ExtensionContext, which provides access to the test method.
     * @throws Exception If an error occurs retrieving the test method or extracting the MongoDataFile annotation.
     */
    @Override
    public void afterEach(ExtensionContext context) throws Exception {
        context.getTestMethod().ifPresent(method -> {
            // load the MongoDataFile annotation value from the test method
            MongoDataFile mongoDataFile = method.getAnnotation(MongoDataFile.class);

            // Load the MongoTemplate that we can use to drop the test collection
            Optional<MongoTemplate> mongoTemplate = getMongoTemplate(context);
            mongoTemplate.ifPresent(template -> template.dropCollection(mongoDataFile.collectionName()));
        });
    }

    /**
     * Helper method that uses reflection to invoke the getMongoTemplate() method on the test instance.
     *
     * @param context The ExtensionContext, which provides access to the test instance
     * @return An optional MongoTemplate, if it exists
     */
    private Optional<MongoTemplate> getMongoTemplate(ExtensionContext context) {
        Optional<Class<?>> clazz = context.getTestClass();
        if (clazz.isPresent()) {
            Class<?> c = clazz.get();
            try {
                // find the getMongoTemplate() method on the testClass
                Method method = c.getMethod("getMongoTemplate", ArrayUtils.EMPTY_CLASS_ARRAY);

                // invoke the getMongoTemplate on the test class
                Optional<Object> testInstance = context.getTestInstance();
                if (testInstance.isPresent()) {
                    return Optional.of((MongoTemplate) method.invoke(testInstance.get(), ArrayUtils.EMPTY_OBJECT_ARRAY));
                }
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }

        return Optional.empty();
    }
}
