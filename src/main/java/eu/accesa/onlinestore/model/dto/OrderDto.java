package eu.accesa.onlinestore.model.dto;

import java.time.LocalDateTime;
import java.util.HashMap;

public class OrderDto {

    private String id;
    private HashMap<String, Integer> orderedProducts;
    private LocalDateTime orderDate;
    private Double orderValue;
    private String userId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public HashMap<String, Integer> getOrderedProducts() {
        return orderedProducts;
    }

    public void setOrderedProducts(HashMap<String, Integer> orderedProducts) {
        this.orderedProducts = orderedProducts;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(LocalDateTime orderDate) {
        this.orderDate = orderDate;
    }

    public Double getOrderValue() {
        return orderValue;
    }

    public void setOrderValue(Double orderValue) {
        this.orderValue = orderValue;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

}
