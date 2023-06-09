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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

import static java.util.stream.Collectors.toList;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Value("${user.confirmation.base.url}")
    private String userConfirmationBaseURL;

    @Value("${user.password.reset.url}")
    private String userPasswordResetURL;

    private final EmailServiceImpl emailService;
    private final MessageSource messageSource;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtTokenUtil jwtTokenUtil;

    public UserServiceImpl(EmailServiceImpl emailService, MessageSource messageSource, ModelMapper modelMapper,
                           PasswordEncoder passwordEncoder, UserRepository userRepository, JwtTokenUtil jwtTokenUtil) {
        this.emailService = emailService;
        this.messageSource = messageSource;
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
        templateModel.put("confirmationURL", userConfirmationBaseURL + userEntity.getId());

        Locale locale = LocaleContextHolder.getLocale();
        String subject = messageSource.getMessage("user.confirmation.subject", null, locale);
        emailService.sendMessage(userEntity.getEmail(), subject, "user-email-confirmation", templateModel, null);

        return modelMapper.map(userEntity, UserDto.class);
    }

    @Override
    public UserEntity loginWithSocialUser(UserDtoNoId userDtoNoId) {
        final Optional<UserEntity> optionalUser = userRepository.findByEmail(userDtoNoId.getEmail());
        if (optionalUser.isPresent()) {
            return optionalUser.get();
        } else {
            UserEntity userEntity = modelMapper.map(userDtoNoId, UserEntity.class);
            userRepository.save(userEntity);
            return userEntity;
        }
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
        UserEntity user = userRepository.findByToken(token)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.class.getName(), "Token ", token));

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
        Optional<UserEntity> user = userRepository.findByToken(resetToken);
        return modelMapper.map(user, UserDto.class);
    }

    @Override
    public String forgotPassword(String email) {
        UserEntity user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException(UserEntity.class.getName(), "User email", email));
        String token = jwtTokenUtil.generatePasswordToken(user);

        user.setToken(token);
        userRepository.save(user);

        Map<String, Object> templateModel = new HashMap<>();
        templateModel.put("token", userPasswordResetURL + user.getToken());

        Locale locale = LocaleContextHolder.getLocale();
        String subject = messageSource.getMessage("password.reset.subject", null, locale);
        emailService.sendMessage(user.getEmail(), subject, "user-token", templateModel, null);

        return user.getToken();
    }
}
