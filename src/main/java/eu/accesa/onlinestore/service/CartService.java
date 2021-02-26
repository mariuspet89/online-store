package eu.accesa.onlinestore.service;

import eu.accesa.onlinestore.model.dto.CartDto;

public interface CartService {

    CartDto createCart(CartDto cartDto);

    CartDto getCartById(String id);

    CartDto getCartByUserId(String id);

    CartDto updateCart(CartDto cartDto);

    void deleteCart(CartDto cartDto);
}
