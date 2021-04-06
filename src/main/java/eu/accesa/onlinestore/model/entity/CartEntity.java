package eu.accesa.onlinestore.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;
import java.util.Objects;

@Document(collection = "carts")
public class CartEntity {

    @Id
    private String id;
    private String userId;
    private Map<String, Integer> products;

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

    public Map<String, Integer> getProducts() {
        return products;
    }

    public void setProducts(Map<String, Integer> products) {
        this.products = products;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartEntity cart = (CartEntity) o;
        return Objects.equals(id, cart.id) && Objects.equals(userId, cart.userId) && Objects.equals(products, cart.products);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, userId, products);
    }

    @Override
    public String toString() {
        return "CartEntity{" +
                "id='" + id + '\'' +
                ", userId='" + userId + '\'' +
                ", products=" + products +
                '}';
    }
}
