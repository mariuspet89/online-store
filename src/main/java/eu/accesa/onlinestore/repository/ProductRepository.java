package eu.accesa.onlinestore.repository;

import eu.accesa.onlinestore.model.entity.ProductEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepository extends MongoRepository<ProductEntity, String> {

    List<ProductEntity> findByNameIsContainingIgnoreCase(String text);
}
