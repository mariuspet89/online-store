package eu.accesa.onlinestore.repository;

import eu.accesa.onlinestore.model.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserEntity, String> {
}
