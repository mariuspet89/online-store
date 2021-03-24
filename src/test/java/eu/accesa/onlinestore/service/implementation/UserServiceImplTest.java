package eu.accesa.onlinestore.service.implementation;

import eu.accesa.onlinestore.exceptionhandler.EntityNotFoundException;
import eu.accesa.onlinestore.model.dto.UserDto;
import eu.accesa.onlinestore.model.dto.UserDtoNoId;
import eu.accesa.onlinestore.model.entity.AddressEntity;
import eu.accesa.onlinestore.model.entity.UserEntity;
import eu.accesa.onlinestore.repository.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

import static eu.accesa.onlinestore.utils.UserTestUtils.createUserDto;
import static eu.accesa.onlinestore.utils.UserTestUtils.createUserEntity;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Spy
    private ModelMapper modelMapper;

    @Spy
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Captor
    private ArgumentCaptor<UserEntity> userEntityArgumentCaptor;

    @Test
    void testFindAll() {
        // GIVEN
        UserEntity mockUser1 = createUserEntity("1", "John", "Wayne", "johnwayne@movies.com",
                "johnwayne", "pistols", "123-456-789", "M", "Main Street 1",
                "Las Vegas", "Nevada", "123456");
        UserEntity mockUser2 = createUserEntity("2", "John", "Travolta", "johntravolta@movies.com",
                "johntravolta", "guns", "123-456-789-0", "M", "Main Street 2",
                "Los Angeles", "California", "123457");

        doReturn(List.of(mockUser1, mockUser2)).when(userRepository).findAll();

        // WHEN
        List<UserDto> users = userService.findAll();

        // THEN
        verify(userRepository).findAll();
        verifyNoMoreInteractions(userRepository);

        assertEquals(2, users.size(), "findAll() should have returned 2 users!");
    }

    @Test
    void testFindByIdSuccess() {
        // GIVEN
        String userId = "1";
        UserEntity mockUser = createUserEntity(userId, "John", "Wayne", "johnwayne@movies.com",
                "johnwayne", "pistols", "123-456-789", "M", "Main Street 1",
                "Main Street 1", "Nevada", "123456");
        AddressEntity mockAddress = mockUser.getAddressEntity();

        doReturn(Optional.of(mockUser)).when(userRepository).findById(anyString());

        // WHEN
        UserDto userDto = userService.findById(userId);

        // THEN
        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);

        assertNotNull(userDto);
        assertEquals(mockUser.getId(), userDto.getId());
        assertEquals(mockUser.getFirstName(), userDto.getFirstName());
        assertEquals(mockUser.getLastName(), userDto.getLastName());
        assertEquals(mockUser.getEmail(), userDto.getEmail());
        assertEquals(mockUser.getUsername(), userDto.getUsername());
        assertEquals(mockUser.getPassword(), userDto.getPassword());
        assertEquals(mockUser.getTelephone(), userDto.getTelephone());
        assertEquals(mockUser.getSex(), userDto.getSex());

        AddressEntity addressEntity = userDto.getAddressEntity();
        assertNotNull(addressEntity);
        assertEquals(mockAddress.getAddress(), addressEntity.getAddress());
        assertEquals(mockAddress.getCity(), addressEntity.getCity());
        assertEquals(mockAddress.getCounty(), addressEntity.getCounty());
        assertEquals(mockAddress.getPostalCode(), addressEntity.getPostalCode());
    }

    @Test
    void testFindByIdFailure() {
        // GIVEN
        String userId = "1";
        String expectedMessage = "UserEntity with UserID = " + userId + " not found";
        doReturn(Optional.empty()).when(userRepository).findById(anyString());

        // WHEN
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.findById(userId));

        // THEN
        verify(userRepository).findById(userId);
        verifyNoMoreInteractions(userRepository);

        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    @Disabled
    void testCreateUser() throws Exception {
        // GIVEN
        String originalPassword = "pistols";
        UserDtoNoId mockUserDto = createUserDto(null, "John", "Wayne", "johnwayne@movies.com",
                "johnwayne", "pistols", "123-456-789", "M", "Main Street 1",
                "Main Street 1", "Nevada", "123456");
        UserEntity createdUserEntity = createUserEntity("1", "John", "Wayne", "johnwayne@movies.com",
                "johnwayne", "$2y$12$qaxDEX2KgF0/83EfLjRziOhOwUrrf/sZAPReO5Nl8u4IV0QUxYeNe",
                "123-456-789", "M", "Main Street 1", "Main Street 1", "Nevada",
                "123456");

        doReturn(createdUserEntity).when(userRepository).save(userEntityArgumentCaptor.capture());

        // WHEN
        UserDto newUser = userService.createUser(mockUserDto);

        // THEN
        // verify password encoding
        verify(passwordEncoder).encode(originalPassword);

        // verified saved entity
        verify(userRepository).save(any(UserEntity.class));
        verifyNoMoreInteractions(userRepository);

        UserEntity entityToSave = userEntityArgumentCaptor.getValue();
        assertThat(entityToSave)
                .usingRecursiveComparison()
                .ignoringFields("id", "password")
                .isEqualTo(createdUserEntity);

        // verified retrieved user DTO
        assertNotNull(newUser);
        assertThat(newUser).usingRecursiveComparison()
                .ignoringFields("id", "password")
                .isEqualTo(mockUserDto);

        // verify ignored fields
        assertNotEquals(0, newUser.getId().length(), "The user ID should not be empty!");
        assertTrue(passwordEncoder.matches(originalPassword, newUser.getPassword()));
    }

    @Test
    void testUpdateUser() {
        // GIVEN
        String userId = "1";
        String oldPassword = "pistols";
        String newPassword = "BigPistols";
        String oldPostalCode = "123456";
        String newPostalCode = "654321";
        UserDtoNoId newUserDataDto = createUserDto(null, "John", "Wayne", "johnwayne@movies.com",
                "johnwayne", newPassword, "123-456-789", "M", "Main Street 1",
                "Main Street 1", "Nevada", newPostalCode);
        UserEntity initialUserEntity = createUserEntity(userId, "John", "Wayne", "johnwayne@movies.com",
                "johnwayne", oldPassword, "123-456-789", "M", "Main Street 1",
                "Main Street 1", "Nevada", oldPostalCode);
        UserEntity updatedUserEntity = createUserEntity(userId, "John", "Wayne", "johnwayne@movies.com",
                "johnwayne", "$2y$12$DHCALr9bzYqQ7VW0k0hke.OBbzQ3Cv4EFeqc6MhwTkiBCR5s3hIse", "123-456-789", "M", "Main Street 1",
                "Main Street 1", "Nevada", newPostalCode);

        doReturn(Optional.of(initialUserEntity)).when(userRepository).findById(anyString());
        when(userRepository.save(initialUserEntity)).thenReturn(updatedUserEntity);

        // WHEN
        UserDto updatedUserDto = userService.updateUser(userId, newUserDataDto);

        // THEN
        verify(userRepository).save(initialUserEntity);
        verifyNoMoreInteractions(userRepository);
        assertNotNull(updatedUserDto);
        assertEquals(updatedUserDto.getId(),userId,"ID mismatch !!");
        assertEquals(updatedUserDto.getFirstName(),"John");
        assertEquals(updatedUserDto.getLastName(),"Wayne");
        assertEquals(updatedUserDto.getPassword(),"$2y$12$DHCALr9bzYqQ7VW0k0hke.OBbzQ3Cv4EFeqc6MhwTkiBCR5s3hIse");
    }

    @Test
    void testUpdateUserFailure() {
        // GIVEN
        String expectedMessage = "UserEntity with UserID = 1 not found";
        doReturn(Optional.empty()).when(userRepository).findById(anyString());

        // WHEN
        UserDtoNoId userDtoNoId = new UserDtoNoId();
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.updateUser("1", userDtoNoId));

        // THEN
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testDeleteUserSuccess() {
        // GIVEN
        String userId = "1";
        UserEntity mockUser = createUserEntity(userId, "John", "Wayne", "johnwayne@movies.com",
                "johnwayne", "pistols", "123-456-789", "M", "Main Street 1",
                "Main Street 1", "Nevada", "123456");

        doReturn(Optional.of(mockUser)).when(userRepository).findById(userId);

        // WHEN
        userService.deleteUser(userId);

        // THEN
        verify(userRepository).delete(any(UserEntity.class));
    }

    @Test
    void testDeleteUserFailure() {
        // GIVEN
        String expectedMessage = "UserEntity with UserID = 1 not found";
        doReturn(Optional.empty()).when(userRepository).findById(anyString());

        // WHEN
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.deleteUser("1"));

        // THEN
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}
