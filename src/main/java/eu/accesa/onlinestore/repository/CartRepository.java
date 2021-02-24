package eu.accesa.onlinestore.repository;

import eu.accesa.onlinestore.model.entity.CartEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CartRepository extends MongoRepository<CartEntity, String> {
}
