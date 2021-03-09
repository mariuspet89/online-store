package eu.accesa.onlinestore.utils;

import eu.accesa.onlinestore.model.dto.ProductDtoNoId;
import eu.accesa.onlinestore.model.entity.AddressEntity;
import eu.accesa.onlinestore.model.entity.OrderEntity;
import eu.accesa.onlinestore.model.entity.ProductEntity;
import eu.accesa.onlinestore.model.entity.UserEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

    public static OrderEntity createOrderEntity(String id,LocalDateTime orderDate, Double orderValue, UserEntity user) {
               OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(id);
        orderEntity.setOrderedProducts(createOrderedProducts("fistProductId",
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

    public static HashMap<String, Integer> createOrderedProducts(String a,String b,String c,Integer one,Integer two, Integer three) {
        List<String> productIds=new ArrayList<>();
        productIds.add(a);
        productIds.add(b);
        productIds.add(c);

        List<Integer> quantities=new ArrayList<>();
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

    public static UserEntity createUserEntity(String id,String firstName,String lastName,String email,String userName,
                                              String password,String phone,String gender,String address,String city,
                                              String county,String postalCode) {
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
}