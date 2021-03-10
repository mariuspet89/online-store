package eu.accesa.onlinestore.utils;

import eu.accesa.onlinestore.model.dto.ProductDtoNoId;
import eu.accesa.onlinestore.model.dto.UserPageDto;
import eu.accesa.onlinestore.model.entity.AddressEntity;
import eu.accesa.onlinestore.model.entity.OrderEntity;
import eu.accesa.onlinestore.model.entity.ProductEntity;
import eu.accesa.onlinestore.model.entity.UserEntity;
import org.checkerframework.checker.units.qual.A;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class TestUtils {

    public static ProductEntity createProductEnity(String id, String name,
                                                   String description,
                                                   Double price, Double rating,
                                                   Integer itemsInStock, String image,
                                                   String brand) {

        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(id);
        productEntity.setName(name);
        productEntity.setDescription(description);
        productEntity.setPrice(price);
        productEntity.setRating(rating);
        productEntity.setItemsInStock(itemsInStock);
        productEntity.setImage(image);
        productEntity.setBrand(brand);
        return productEntity;
    }

    public static ProductDtoNoId createProductDtoNoId(String name,
                                                      String description,
                                                      Double price, Double rating,
                                                      Integer itemsInStock, String image,
                                                      String brand) {

        ProductDtoNoId productDtoNoId = new ProductDtoNoId();
        productDtoNoId.setName(name);
        productDtoNoId.setDescription(description);
        productDtoNoId.setPrice(price);
        productDtoNoId.setRating(rating);
        productDtoNoId.setItemsInStock(itemsInStock);
        productDtoNoId.setImage(image);
        productDtoNoId.setBrand(brand);
        return productDtoNoId;
    }

    public static UserPageDto createUserPageDto(Integer pageNo, Integer pageSize,
                                                Sort.Direction sortDirection, String sortBy) {
        UserPageDto userPageDto = new UserPageDto();
        userPageDto.setPageNo(pageNo);
        userPageDto.setPageSize(pageSize);
        userPageDto.setSortDirection(sortDirection);
        userPageDto.setSortBy(sortBy);
        return userPageDto;
    }

    public static OrderEntity testOrderEntity(String id, Double orderValue, UserEntity user) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(id);
        orderEntity.setOrderedProducts(testHMOrderedProducts("fistProductId",
                "secondProductId",
                "thirdProductId",
                1,
                2,
                3));
        orderEntity.setOrderDate(LocalDateTime.parse(LocalDateTime.now().format(DateTimeFormatter.ofPattern("uuuu-MM-dd'T'HH:mm:ss.SSS"))));
        orderEntity.setUser(user);
        orderEntity.setOrderValue(orderValue);
        return orderEntity;
    }

    public static HashMap<String, Integer> testHMOrderedProducts(String a, String b, String c, Integer one, Integer two, Integer three) {
        List<String> productIds = new ArrayList<>();
        productIds.add(a);
        productIds.add(b);
        productIds.add(c);

        List<Integer> quantities = new ArrayList<>();
        quantities.add(one);
        quantities.add(two);
        quantities.add(three);

        HashMap<String, Integer> orderedProducts = new HashMap<>();
        for (String s : productIds) {
            for (Integer i : quantities) {
                orderedProducts.put(s, i);
            }
        }
        return orderedProducts;
    }

    public static UserEntity testUserEntity(String id, String firstName, String lastName, String email, String userName,
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
        userEntity.setAddress(addressEntity);
        return userEntity;
    }

    public static List<OrderEntity> testOrdersList() {
        return Arrays.asList(
                testOrderEntity("60377ec00e2cb07c9a3811d3", 11.11, testUserEntity("603648273ed85832b440eb99",
                        "John", "Doe", "jd@mockemail.com", "jd", "qwerty",
                        "40722112211", "male", "Toamnei nr.1", "Las Vegas", "Nevada", "440055")),
                testOrderEntity("60377ec00e2cb07c9a3800c2", 22.22, testUserEntity("603648273ed85832b440e11a",
                        "Ion", "Iliescu", "ii@mockemail.com", "nemuritorul", "elena",
                        "40722112222", "male", "Permanentei nr.1", "Voluntari", "Ilfov", "220055"))
        );
    }

    public static List<ProductEntity> testProductsList() {

        return Arrays.asList(
                createProductEnity("123",
                        "test name 1", "test description 1", 1.2, 2.5, 0,
                        "test1", "test1"),
                createProductEnity("456",
                        "test name 2", "test description 2", 2.2, 3.5, 0,
                        "test2", "test2"));

    }
}