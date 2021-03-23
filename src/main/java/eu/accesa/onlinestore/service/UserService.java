package eu.accesa.onlinestore.service;

import eu.accesa.onlinestore.model.dto.UserDto;
import eu.accesa.onlinestore.model.dto.UserDtoNoId;
import eu.accesa.onlinestore.model.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface UserService {

    List<UserDto> findAll();

    UserDto findById(String id);

    UserDto createUser(UserDtoNoId userDtoNoId) throws Exception;

    UserDto updateUser(String id, UserDtoNoId userDtoNoId);

    void deleteUser(String id);

    UserDto findByEmail(String mail);
}
