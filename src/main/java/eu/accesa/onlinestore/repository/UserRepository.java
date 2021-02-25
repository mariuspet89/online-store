package eu.accesa.onlinestore.repository;

import eu.accesa.onlinestore.model.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends MongoRepository<UserEntity, String> {

}

