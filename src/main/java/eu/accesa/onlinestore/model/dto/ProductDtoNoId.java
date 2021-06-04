package eu.accesa.onlinestore.model.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ProductDtoNoId {

    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Double price;
    @NotNull
    private Double rating;
    @NotNull
    private Integer itemsInStock;
    @NotBlank
    private String image;
    @NotBlank
    private String brand;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public Integer getItemsInStock() {
        return itemsInStock;
    }

    public void setItemsInStock(Integer itemsInStock) {
        this.itemsInStock = itemsInStock;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }
}
