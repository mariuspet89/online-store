package eu.accesa.onlinestore.utils;

import eu.accesa.onlinestore.model.dto.CartDto;
import eu.accesa.onlinestore.model.dto.CartDtoNoId;
import eu.accesa.onlinestore.model.entity.CartEntity;

import java.util.HashMap;

public class CartTestUtils {
    public static CartEntity createCartEntity(String id, String userId,
                                              HashMap<String, Integer> cartProducts) {

        CartEntity cartEntity = new CartEntity();
        cartEntity.setId(id);
        cartEntity.setUserId(userId);
        cartEntity.setProducts(cartProducts);
        return cartEntity;
    }

    public static CartDto createCartDto(String id, String userId, HashMap<String, Integer> products) {

        CartDto cartDto = new CartDto();
        cartDto.setId(id);
        cartDto.setUserId(userId);
        cartDto.setProducts(products);
        return cartDto;
    }

    public static CartDtoNoId createCartDtoNoId(String userId, HashMap<String, Integer> products) {

        CartDtoNoId cartDtoNoId = new CartDtoNoId();
        cartDtoNoId.setUserId(userId);
        cartDtoNoId.setProducts(products);
        return cartDtoNoId;
    }

}
