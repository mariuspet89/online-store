package eu.accesa.onlinestore.repository;

import eu.accesa.onlinestore.model.entity.OrderEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderRepository extends MongoRepository<OrderEntity, String> {

    List<OrderEntity> findByUserId(String userId);
}
