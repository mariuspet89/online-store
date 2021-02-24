package eu.accesa.onlinestore.model.dto;

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

    public String get_id() {
        return id;
    }

    public void set_id(String _id) {
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
}
