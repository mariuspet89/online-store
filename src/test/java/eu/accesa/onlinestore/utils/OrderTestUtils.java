package eu.accesa.onlinestore.utils;

import eu.accesa.onlinestore.model.dto.OrderDto;
import eu.accesa.onlinestore.model.dto.OrderDtoNoId;
import eu.accesa.onlinestore.model.entity.OrderEntity;
import eu.accesa.onlinestore.model.entity.UserEntity;

import java.util.*;

import static eu.accesa.onlinestore.utils.UserTestUtils.createUserEntity;

public class OrderTestUtils {

    public static OrderEntity testOrderEntity(String id, Double orderValue, UserEntity user) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setId(id);
        orderEntity.setOrderedProducts(testHMOrderedProducts("fistProductId",
                "secondProductId",
                "thirdProductId",
                1,
                2,
                3));
        orderEntity.setUser(user);
        orderEntity.setOrderValue(orderValue);
        return orderEntity;
    }

    public static Map<String, Integer> testHMOrderedProducts(String a, String b, String c, Integer one, Integer two, Integer three) {
        List<String> productIds = new ArrayList<>();
        productIds.add(a);
        productIds.add(b);
        productIds.add(c);

        List<Integer> quantities = new ArrayList<>();
        quantities.add(one);
        quantities.add(two);
        quantities.add(three);

        Map<String, Integer> orderedProducts = new HashMap<>();
        for (String s : productIds) {
            for (Integer i : quantities) {
                orderedProducts.put(s, i);
            }
        }
        return orderedProducts;
    }

    public static Map<String, Integer> testHMOrderedProduct(String a, Integer one) {
        Map<String, Integer> orderedProducts = new HashMap<>();
        orderedProducts.put(a, one);
        return orderedProducts;
    }

    public static List<OrderEntity> testOrdersList() {
        return Arrays.asList(
                testOrderEntity("60377ec00e2cb07c9a3811d3", 11.11, createUserEntity("603648273ed85832b440eb99",
                        "John", "Doe", "jd@mockemail.com", "jd", "qwerty",
                        "40722112211", "male", "Toamnei nr.1", "Las Vegas", "Nevada", "440055")),
                testOrderEntity("60377ec00e2cb07c9a3800c2", 22.22, createUserEntity("603648273ed85832b440e11a",
                        "Ion", "Iliescu", "ii@mockemail.com", "nemuritorul", "elena",
                        "40722112222", "male", "Permanentei nr.1", "Voluntari", "Ilfov", "220055"))
        );
    }

    public static OrderDtoNoId testOrderDtoNoId(Double orderValue, String userId) {
        OrderDtoNoId newOrderDtoNoId = new OrderDtoNoId();
        newOrderDtoNoId.setUserId(userId);
        newOrderDtoNoId.setOrderValue(orderValue);
        return newOrderDtoNoId;
    }

    public static OrderDto testOrderDto(String id, Double orderValue, String userId) {
        OrderDto order = new OrderDto();
        order.setId(id);
        order.setOrderValue(orderValue);
        order.setUserId(userId);
        return order;
    }

    public static List<OrderDto> testOrderDtoList() {
        return Arrays.asList(
                testOrderDto("orderId1", 1.1, "userId1"),
                testOrderDto("orderId2", 2.2, "userId2")
        );
    }
}
