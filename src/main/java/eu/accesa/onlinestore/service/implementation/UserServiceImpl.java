package eu.accesa.onlinestore.service.implementation;

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

    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public UserServiceImpl(ModelMapper modelMapper, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public List<UserDto> findAll() {
        List<UserEntity> users = userRepository.findAll();
        return users.stream()
                .map(user -> modelMapper.map(user, UserDto.class))
                .collect(toList());
    }

    @Override
    public UserDto findById(String id) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow();
        return modelMapper.map(userEntity, UserDto.class);
    }

    @Override
    public UserDto createUser(UserDtoNoId userDtoNoId) {
        String encodedPassword = passwordEncoder.encode(userDtoNoId.getPassword());
        userDtoNoId.setPassword(encodedPassword);
        UserEntity userEntity = modelMapper.map(userDtoNoId, UserEntity.class);
        return modelMapper.map(userRepository.save(userEntity), UserDto.class);
    }

    @Override
    public UserDto updateUser(String id, UserDtoNoId userDtoNoId) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow();
        modelMapper.map(userDtoNoId, userEntity);
        userRepository.save(userEntity);
        return modelMapper.map(userEntity, UserDto.class);
    }

    @Override
    public void deleteUser(String id) {
        UserEntity userEntity = userRepository.findById(id).orElseThrow();
        userRepository.delete(userEntity);
    }
}
