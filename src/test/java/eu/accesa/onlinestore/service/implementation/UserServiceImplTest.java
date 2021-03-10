package eu.accesa.onlinestore.service.implementation;

import eu.accesa.onlinestore.model.dto.UserDto;
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

import static eu.accesa.onlinestore.utils.TestUtils.testUserEntity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

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
        UserEntity mockUser1 = testUserEntity("1", "John", "Wayne", "johnwayne@movies.com",
                "johnwayne", "pistols", "123-456-789", "M", "Main Street 1",
                "Las Vegas", "Nevada", "123456");
        AddressEntity mockAddress1 = mockUser1.getAddress();

        UserEntity mockUser2 = testUserEntity("2", "John", "Travolta", "johntravolta@movies.com",
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
        UserEntity mockUser = testUserEntity("1", "John", "Wayne", "johnwayne@movies.com",
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

}
