package eu.accesa.onlinestore.model.dto;

import java.util.Map;

public class CartDtoNoId {

    private String userId;
    private Map<String, Integer> products;

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
}
