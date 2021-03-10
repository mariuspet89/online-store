package eu.accesa.onlinestore.utils;

import eu.accesa.onlinestore.model.dto.UserDto;
import eu.accesa.onlinestore.model.entity.AddressEntity;
import eu.accesa.onlinestore.model.entity.UserEntity;

public class UserTestUtils {

    public static UserEntity createUserEntity(String id, String firstName, String lastName, String email, String userName,
                                              String password, String phone, String gender, String address, String city,
                                              String county, String postalCode) {
        final UserEntity userEntity = new UserEntity();
        userEntity.setId(id);
        userEntity.setFirstName(firstName);
        userEntity.setLastName(lastName);
        userEntity.setEmail(email);
        userEntity.setUsername(userName);
        userEntity.setPassword(password);
        userEntity.setTelephone(phone);
        userEntity.setSex(gender);

        final AddressEntity addressEntity = new AddressEntity();
        addressEntity.setAddress(address);
        addressEntity.setCity(city);
        addressEntity.setCounty(county);
        addressEntity.setPostalCode(postalCode);
        userEntity.setAddressEntity(addressEntity);
        return userEntity;
    }

    public static UserDto createUserDto(String id, String firstName, String lastName, String email, String userName,
                                        String password, String phone, String gender, String address, String city,
                                        String county, String postalCode) {
        final UserDto userDto = new UserDto();
        userDto.setId(id);
        userDto.setFirstName(firstName);
        userDto.setLastName(lastName);
        userDto.setEmail(email);
        userDto.setUsername(userName);
        userDto.setPassword(password);
        userDto.setTelephone(phone);
        userDto.setSex(gender);

        final AddressEntity addressEntity = new AddressEntity();
        addressEntity.setAddress(address);
        addressEntity.setCity(city);
        addressEntity.setCounty(county);
        addressEntity.setPostalCode(postalCode);
        userDto.setAddressEntity(addressEntity);
        return userDto;
    }
}
