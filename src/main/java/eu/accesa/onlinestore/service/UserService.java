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

    UserDto createUser(UserDtoNoId userDtoNoId);

    UserDto updateUser(String id, UserDtoNoId userDtoNoId);

    void deleteUser(String id);

    UserDto resetPassword(String token, String newPassword, String userEmail);

    UserDto findByUserResetToken (String resetToken);

    /**
     * Sets the {@code enabled} field's value to true.
     *
     * @param userId the user ID
     * @return a confirmation message
     */
    String confirmUser(String userId);
}
