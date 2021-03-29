package eu.accesa.onlinestore.service;

import eu.accesa.onlinestore.model.dto.UserDto;
import eu.accesa.onlinestore.model.dto.UserDtoNoId;

import java.util.List;

public interface UserService {

    List<UserDto> findAll();

    UserDto findById(String id);

    boolean existsByUsername(String username);

    UserDto findByUsername(String username);

    boolean existsByEmail(String email);

    UserDto findByEmail(String email);

    UserDto createUser(UserDtoNoId userDtoNoId) throws Exception;

    UserDto updateUser(String id, UserDtoNoId userDtoNoId);

    void deleteUser(String id);

    String confirmUser(String userId);
}
