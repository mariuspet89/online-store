package eu.accesa.onlinestore.service.implementation;

import eu.accesa.onlinestore.exceptionhandler.EntityNotFoundException;
import eu.accesa.onlinestore.model.dto.UserDto;
import eu.accesa.onlinestore.model.dto.UserDtoNoId;
import eu.accesa.onlinestore.model.entity.AddressEntity;
import eu.accesa.onlinestore.model.entity.UserEntity;
import eu.accesa.onlinestore.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

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

    @Test
    void testFindAll() {
        // GIVEN
        UserEntity mockUser1 = createUserEntity("1", "John", "Wayne", "johnwayne@movies.com",
                "johnwayne", "pistols", "123-456-789", "M", "Main Street 1",
                "Las Vegas", "Nevada", "123456");
        AddressEntity mockAddress1 = mockUser1.getAddress();

        UserEntity mockUser2 = createUserEntity("2", "John", "Travolta", "johntravolta@movies.com",
                "johntravolta", "guns", "123-456-789-0", "M", "Main Street 2",
                "Los Angeles", "California", "123457");
        AddressEntity mockAddress2 = mockUser2.getAddress();

        doReturn(List.of(mockUser1, mockUser2)).when(userRepository).findAll();

        // WHEN
        List<UserDto> users = userService.findAll();

        // THEN
        assertEquals(2, users.size(), "findAll() should have returned 2 users!");
    }

    @Test
    void testFindByIdSuccess() {
        // GIVEN
        UserEntity mockUser = createUserEntity("1", "John", "Wayne", "johnwayne@movies.com",
                "johnwayne", "pistols", "123-456-789", "M", "Main Street 1",
                "Main Street 1", "Nevada", "123456");
        AddressEntity mockAddress = mockUser.getAddress();

        doReturn(Optional.of(mockUser)).when(userRepository).findById(anyString());

        // WHEN
        UserDto userDto = userService.findById("1");

        // THEN
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
        String expectedMessage = "UserEntity with UserID = 1 not found";
        doReturn(Optional.empty()).when(userRepository).findById(anyString());

        // WHEN
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> userService.findById("1"));

        // THEN
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void testCreateUser() {
        // GIVEN
        UserDtoNoId mockUserDto = createUserDto(null, "John", "Wayne", "johnwayne@movies.com",
                "johnwayne", "pistols", "123-456-789", "M", "Main Street 1",
                "Main Street 1", "Nevada", "123456");
        UserEntity mockUser = createUserEntity("1", "John", "Wayne", "johnwayne@movies.com",
                "johnwayne", "pistols",
                "123-456-789", "M", "Main Street 1", "Main Street 1", "Nevada",
                "123456");

        doReturn(mockUser).when(userRepository).save(any(UserEntity.class));

        // WHEN
        UserDto newUser = userService.createUser(mockUserDto);

        // THEN
        assertNotNull(newUser);

        verify(passwordEncoder).encode(anyString());
        verify(userRepository).save(any(UserEntity.class));

        assertNotNull(newUser.getId());
        assertNotEquals(0, newUser.getId().length(), "The user ID should not be empty!");
        assertThat(newUser).usingRecursiveComparison()
                .ignoringFields("id", "password")
                .isEqualTo(mockUserDto);
    }
    
    @Test
    void testUpdateUser() {
        // GIVEN
        UserDtoNoId mockUserDto = createUserDto(null, "John", "Wayne", "johnwayne@movies.com",
                "johnwayne", "pistols", "123-456-789", "M", "Main Street 1",
                "Main Street 1", "Nevada", "123456");
        UserEntity mockUser = createUserEntity("1", "John", "Wayne", "johnwayne@movies.com",
                "johnwayne", "Bigpistols",
                "123-456-789", "M", "Main Street 1", "Main Street 1", "Nevada",
                "123456");
        String userId = mockUser.getId();

        doReturn(Optional.of(mockUser)).when(userRepository).findById(anyString());
        doReturn(mockUser).when(userRepository).save(any(UserEntity.class));

        // WHEN
        UserDto updatedUser = userService.updateUser(userId, mockUserDto);

        // THEN
        assertNotNull(updatedUser);

        verify(userRepository).save(any(UserEntity.class));

        assertThat(updatedUser).usingRecursiveComparison()
                .ignoringFields("id", "password")
                .isEqualTo(mockUserDto);
    }

    @Test
    void testUpdateUserFailure() {
        // GIVEN
        String expectedMessage = "UserEntity with UserID = 1 not found";
        doReturn(Optional.empty()).when(userRepository).findById(anyString());

        // WHEN
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class,
                () -> userService.updateUser("1", new UserDtoNoId()));

        // THEN
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));
    }
}
