package eu.accesa.onlinestore.repository;

import eu.accesa.onlinestore.model.entity.ProductEntity;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends MongoRepository<ProductEntity, String> {

}
