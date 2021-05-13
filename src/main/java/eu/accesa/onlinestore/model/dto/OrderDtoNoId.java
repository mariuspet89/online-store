package eu.accesa.onlinestore.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import eu.accesa.onlinestore.model.annotation.OrderedQuantity;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Map;

public class OrderDtoNoId {

    @NotEmpty(message = "The ordered products list must not be null or empty!")
    @OrderedQuantity
    private Map<String, Integer> orderedProducts;

    @NotNull(message = "The order date must not be null or empty!")
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime orderDate;

    @DecimalMin(value = "0.0", inclusive = false, message = "The order total value must be greater than zero!")
    private Double orderValue;

    @NotBlank(message = "The user ID must not be null or empty!")
    private String userId;

    public Map<String, Integer> getOrderedProducts() {
        return orderedProducts;
    }

    public void setOrderedProducts(Map<String, Integer> orderedProducts) {
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
