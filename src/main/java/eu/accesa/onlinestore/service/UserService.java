package eu.accesa.onlinestore.service;

import eu.accesa.onlinestore.model.dto.UserDto;
import eu.accesa.onlinestore.model.dto.UserDtoNoId;

import java.util.List;

public interface UserService {

    List<UserDto> findAll();

    UserDto findById(String id);

    UserDto createUser(UserDtoNoId userDtoNoId);

    UserDto updateUser(String id, UserDtoNoId userDtoNoId);

    void deleteUser(String id);
}
