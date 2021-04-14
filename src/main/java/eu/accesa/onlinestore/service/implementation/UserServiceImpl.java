package eu.accesa.onlinestore.service.implementation;

import eu.accesa.onlinestore.configuration.security.JwtTokenUtil;
import eu.accesa.onlinestore.exceptionhandler.EntityNotFoundException;
import eu.accesa.onlinestore.exceptionhandler.OnlineStoreException;
import eu.accesa.onlinestore.model.dto.UserDto;
import eu.accesa.onlinestore.model.dto.UserDtoNoId;
import eu.accesa.onlinestore.model.entity.UserEntity;
import eu.accesa.onlinestore.repository.UserRepository;
import eu.accesa.onlinestore.service.UserService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);


    private final EmailServiceImpl emailService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;

    public UserServiceImpl(EmailServiceImpl emailService, ModelMapper modelMapper, PasswordEncoder passwordEncoder, UserRepository userRepository, JwtTokenUtil jwtTokenUtil) {
        this.emailService = emailService;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtTokenUtil = jwtTokenUtil;
    }

    @Override
    public List<UserDto> findAll() {
        LOGGER.info("UserService: getting all users");

        List<UserEntity> users = userRepository.findAll();
        return users.stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(toList());
    }

    @Override
    public UserDto findById(String id) {
        LOGGER.info("UserService: searching for user with ID = {}", id);

        UserEntity userEntity = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(UserEntity.class.getName(), "UserID", id));
        return modelMapper.map(userEntity, UserDto.class);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public UserDto findByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username).orElseThrow(
                () -> new EntityNotFoundException(UserEntity.class.getName(), "username", username));

        return modelMapper.map(userEntity, UserDto.class);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserDto findByEmail(String email) {
        UserEntity userEntity = userRepository.findByEmail(email).orElseThrow(
                () -> new EntityNotFoundException(UserEntity.class.getName(), "email", email));

        return modelMapper.map(userEntity, UserDto.class);
    }

    @Override
    public UserDto createUser(UserDtoNoId userDtoNoId) {
        LOGGER.info("UserService: creating user");

        // duplicate checks
        if (existsByUsername(userDtoNoId.getUsername())) {
            throw new OnlineStoreException("This user name is already used!");
        }

        if (existsByEmail(userDtoNoId.getEmail())) {
            throw new OnlineStoreException("This email is already used!");
        }

        UserEntity userEntity = modelMapper.map(userDtoNoId, UserEntity.class);
        String encodedPassword = passwordEncoder.encode(userDtoNoId.getPassword());
        userEntity.setPassword(encodedPassword);
        userEntity = userRepository.save(userEntity);

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("confirmationURL", "http://18.224.7.25:5000/#/userConfirmation?userId=" + userEntity.getId());

        emailService.sendMessage(userEntity.getEmail(), "User Confirmation",
                "user-email-confirmation", templateModel, null);

        return modelMapper.map(userEntity, UserDto.class);
    }

    @Override
    public UserDto updateUser(String id, UserDtoNoId userDtoNoId) {
        LOGGER.info("Updating user with ID = {}", id);

        UserEntity userEntity = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(UserEntity.class.getName(), "UserID", id));

        modelMapper.map(userDtoNoId, userEntity);
        String encodedPassword = passwordEncoder.encode(userDtoNoId.getPassword());
        userEntity.setPassword(encodedPassword);
        userEntity = userRepository.save(userEntity);

        return modelMapper.map(userEntity, UserDto.class);
    }

    @Override
    public void deleteUser(String id) {
        LOGGER.info("Deleting user with ID = {}", id);

        UserEntity userEntity = userRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(UserEntity.class.getName(), "UserID", id));
        userRepository.delete(userEntity);
    }

    @Override
    public String confirmUser(String userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(UserEntity.class.getName(), "UserID", userId));

        userEntity.setEnabled(true);
        userRepository.save(userEntity);
        return "Your account is confirmed!";
    }

    @Override
    public String resetPassword(String token, String password) {

        UserEntity user = userRepository.findUserEntityByToken(token).orElseThrow(() -> new EntityNotFoundException(UserEntity.class.getName(), "Token ", token));

        if (jwtTokenUtil.validate(token)) {
            if (token.equals(user.getToken())) {
                String newEncodePassword = passwordEncoder.encode(password);
                user.setPassword(newEncodePassword);
                user.setToken(null);
                userRepository.save(user);

            }
        } else {
            return "Invalid token";
        }
        return "Your was password successfully updated.";
    }

    @Override
    public UserDto findByUserResetToken(String resetToken) {

        Optional<UserEntity> user = userRepository.findUserEntityByToken(resetToken);
        return modelMapper.map(user, UserDto.class);
    }


    @Override
    public String forgotPassword(String email) {
        UserEntity user = userRepository.findByEmail(email).orElseThrow(() -> new EntityNotFoundException(UserEntity.class.getName(), "User email", email));
        String token = jwtTokenUtil.generatePasswordToken(user);

        user.setToken(token);
        userRepository.save(user);

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("token", "http://18.224.7.25:5000/#/account/new-password?token=" + user.getToken());

        emailService.sendMessage(user.getEmail(), "Password reset link for onlinestore account ",
                "user-token", templateModel, null);

        return user.getToken();
    }
}