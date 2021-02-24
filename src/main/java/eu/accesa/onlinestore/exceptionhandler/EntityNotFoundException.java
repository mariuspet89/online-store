package eu.accesa.onlinestore.exceptionhandler;

public class EntityNotFoundException extends OnlineStoreException{
    public EntityNotFoundException(String entityName, String entityField, String entityFieldValue) {
        super(entityName + " with " + entityField + "=" + entityFieldValue + " not found");
    }
}
