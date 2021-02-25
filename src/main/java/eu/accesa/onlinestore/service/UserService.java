package eu.accesa.onlinestore.service;


import eu.accesa.onlinestore.model.dto.ProductDto;
import eu.accesa.onlinestore.model.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> findAll();

    UserDto findById(String id);

    UserDto addNewUser(UserDto userDto);

    void deleteUserById(String id);

    UserDto updateUser(UserDto userDto);
}
