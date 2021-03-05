package eu.accesa.onlinestore.model.dto;

import java.time.LocalDateTime;
import java.util.HashMap;

public class OrderDtoWithoutId {

    private HashMap<String, Integer> orderedProducts;
    private LocalDateTime orderDate;
    private Double orderValue;
    private String userId;

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
