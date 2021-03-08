package eu.accesa.onlinestore.model.dto;

import java.util.HashMap;

public class CartDtoNoId {

    private String userId;
    private HashMap<String, Integer> products;

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
}
