package eu.accesa.onlinestore.service.implementation;

import eu.accesa.onlinestore.exceptionhandler.EntityNotFoundException;
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

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);


    private final EmailServiceImpl emailService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserServiceImpl(EmailServiceImpl emailService, ModelMapper modelMapper, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.emailService = emailService;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
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
    public UserDto findByEmail(String mail) {
        return modelMapper.map(userRepository.findByUsername(mail), UserDto.class);
    }

    @Override
    public UserDto createUser(UserDtoNoId userDtoNoId) throws Exception {
        LOGGER.info("UserService: creating user");
        List<UserDto> mailChecker = findAll();
        for (UserDto user : mailChecker) {
            if (user.getUsername().equals(userDtoNoId.getUsername())) {
                throw new Exception("This user name is already used!");
            }else if(user.getEmail().equals((userDtoNoId.getEmail()))){
                throw new Exception(("This email is already used"));
            }
        }
        UserEntity userEntity = modelMapper.map(userDtoNoId, UserEntity.class);
        String encodedPassword = passwordEncoder.encode(userDtoNoId.getPassword());
        userEntity.setPassword(encodedPassword);
        userEntity = userRepository.save(userEntity);

        emailService.sendSimpleMessage(userEntity.getEmail(),
                "Confirmation Email", "Please verify your account by pressing the link below:\n" +
                        "http://18.224.7.25:5000/userConfirmation?userId=" + userEntity.getId());
        return modelMapper.map(userEntity, UserDto.class);
    }

    public String confirmUser(String userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException(UserEntity.class.getName(), "UserID", userId));

        userEntity.setEnabled(true);
        userRepository.save(userEntity);
        return "Your account is confirmed!";
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
}
