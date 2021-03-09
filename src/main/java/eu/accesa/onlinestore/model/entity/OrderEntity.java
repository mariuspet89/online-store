package eu.accesa.onlinestore.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.Past;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Objects;

@Document(collection = "orders")
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderEntity {
    @Id
    private String id;
    @Field(value = "ordered_products")
    private HashMap<String, Integer> orderedProducts;
    @Field(value = "order_date")
    @JsonDeserialize(using= LocalDateTimeDeserializer.class)
    @JsonSerialize(using= LocalDateTimeSerializer.class)
    private LocalDateTime orderDate;
    @Field(value = "order_value")
    private Double orderValue;
    private UserEntity user;

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

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderEntity that = (OrderEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(orderedProducts, that.orderedProducts) && Objects.equals(orderDate, that.orderDate) && Objects.equals(orderValue, that.orderValue) && Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderedProducts, orderDate, orderValue, user);
    }

    @Override
    public String toString() {
        return "OrderEntity{" +
                "id='" + id + '\'' +
                ", orderedProducts=" + orderedProducts +
                ", orderDate=" + orderDate +
                ", orderValue=" + orderValue +
                ", user=" + user +
                '}';
    }
}
