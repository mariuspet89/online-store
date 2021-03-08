package eu.accesa.onlinestore.service;

import eu.accesa.onlinestore.model.dto.CartDto;
import eu.accesa.onlinestore.model.dto.CartDtoNoId;

public interface CartService {

    CartDto createCart(CartDtoNoId cartDtoNoId);

    CartDto getCartById(String id);

    CartDto getCartByUserId(String id);

    CartDto updateCart(CartDto cartDto);

    void deleteCart(CartDto cartDto);
}
