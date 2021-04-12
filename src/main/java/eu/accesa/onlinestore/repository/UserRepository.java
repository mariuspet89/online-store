package eu.accesa.onlinestore.repository;

import eu.accesa.onlinestore.model.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<UserEntity, String> {

    boolean existsByUsername(String username);

    Optional<UserEntity> findByUsername(String username);

    boolean existsByEmail(String email);

    Optional<UserEntity> findByEmail(String mail);

    Optional<UserEntity>findUserEntityByTokenEquals (String resetToken);
}
