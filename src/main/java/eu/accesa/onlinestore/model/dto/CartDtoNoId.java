package eu.accesa.onlinestore.model.dto;

import eu.accesa.onlinestore.model.annotation.OrderedQuantity;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;

public class CartDtoNoId {

    @NotBlank(message = "The user ID must not be null or empty!")
    private String userId;

    @NotEmpty(message = "The ordered products list must not be null or empty!")
    @OrderedQuantity
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
