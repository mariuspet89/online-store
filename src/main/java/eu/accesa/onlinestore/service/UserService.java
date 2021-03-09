package eu.accesa.onlinestore.service;


import eu.accesa.onlinestore.model.dto.UserDto;
import eu.accesa.onlinestore.model.dto.UserDtoNoId;

import java.util.List;

public interface UserService {

    List<UserDto> findAll();

    UserDto findById(String id);

    UserDto addNewUser(UserDtoNoId userDtoNoId);

    void deleteUserById(String id);

    UserDto updateUser(String id, UserDtoNoId userDtoNoId);
}
