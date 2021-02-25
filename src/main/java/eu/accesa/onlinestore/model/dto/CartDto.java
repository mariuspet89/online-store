package eu.accesa.onlinestore.model.dto;

import java.util.HashMap;
import java.util.Objects;

public class CartDto {

    private String cartId;
    private String userId;
    private HashMap<String, Integer> products;

    public String getCartId() {
        return cartId;
    }

    public void setCartId(String cartId) {
        this.cartId = cartId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public HashMap<String, Integer> getProducts() {
        return products;
    }

    public void setProducts(HashMap<String, Integer> products) {
        this.products = products;
    }

    @Override
    public String toString() {
        return "CartDto{" +
                "cartId='" + cartId + '\'' +
                ", userId='" + userId + '\'' +
                ", products=" + products +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartDto cartDto = (CartDto) o;
        return Objects.equals(cartId, cartDto.cartId) && Objects.equals(userId, cartDto.userId) && Objects.equals(products, cartDto.products);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cartId, userId, products);
    }
}
