package eu.accesa.onlinestore.model.dto;

import java.util.Objects;

public class ProductDto {

    private String id;
    private String name;
    private String description;
    private Double price;
    private Double rating;
    private Integer itemsinstock;
    private String image;
    private String brand;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public Integer getItemsinstock() {
        return itemsinstock;
    }

    public void setItemsinstock(Integer itemsinstock) {
        this.itemsinstock = itemsinstock;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ProductDto that = (ProductDto) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(description, that.description) && Objects.equals(price, that.price) && Objects.equals(rating, that.rating) && Objects.equals(itemsinstock, that.itemsinstock) && Objects.equals(image, that.image) && Objects.equals(brand, that.brand);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, description, price, rating, itemsinstock, image, brand);
    }

    @Override
    public String toString() {
        return "ProductDto{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", rating=" + rating +
                ", itemsinstock=" + itemsinstock +
                ", image='" + image + '\'' +
                ", brand='" + brand + '\'' +
                '}';
    }
}
