package eu.accesa.onlinestore.model.dto;

import java.util.HashMap;
import java.util.Objects;

public class CartDto {

    private String id;
    private String userId;
    private HashMap<String, Integer> products;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", products=" + products +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartDto cartDto = (CartDto) o;
        return Objects.equals(id, cartDto.id) && Objects.equals(userId, cartDto.userId) && Objects.equals(products, cartDto.products);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, products);
    }
}
