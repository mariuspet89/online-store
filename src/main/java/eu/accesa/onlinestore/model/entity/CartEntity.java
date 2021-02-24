package eu.accesa.onlinestore.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.HashMap;
import java.util.Objects;

@Document(collection = "carts")
public class CartEntity {

    @Id
    private String cartId;
    private String userId;
    private HashMap<ProductEntity, Integer> products;

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

    public HashMap<ProductEntity, Integer> getProducts() {
        return products;
    }

    public void setProducts(HashMap<ProductEntity, Integer> products) {
        this.products = products;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartEntity cart = (CartEntity) o;
        return Objects.equals(cartId, cart.cartId) && Objects.equals(userId, cart.userId) && Objects.equals(products, cart.products);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cartId, userId, products);
    }

    @Override
    public String toString() {
        return "CartEntity{" +
                "cartId='" + cartId + '\'' +
                ", userId='" + userId + '\'' +
                ", products=" + products +
                '}';
    }
}
