package eu.accesa.onlinestore.model.dto;

public class ProductDto {

    private String ID;
    private String Name;
    private String Description;
    private Double Price;
    private Double Rating;
    private Integer ItemInStock;
    private String ImageUrl;
    private String Brand;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public Double getPrice() {
        return Price;
    }

    public void setPrice(Double price) {
        Price = price;
    }

    public Double getRating() {
        return Rating;
    }

    public void setRating(Double rating) {
        Rating = rating;
    }

    public Integer getItemInStock() {
        return ItemInStock;
    }

    public void setItemInStock(Integer itemInStock) {
        ItemInStock = itemInStock;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }
}
