package eu.accesa.onlinestore.utils.mongoDbUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MongoDataFile {
    /**
     * The name of the MongoDB JSON test file.
     * @return  The name of the MongoDB JSON test file.
     */
    String value();

    /**
     * The class of objects stored in the MongoDB test file.
     * @return  The class of objects stored in the MongoDB test file.
     */
    Class classType();

    /**
     * The name of the MongoDB collection hosting the test objects.
     * @return  The name of the MongoDB collection hosting the test objects.
     */
    String collectionName();
}
