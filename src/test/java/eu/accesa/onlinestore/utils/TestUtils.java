package eu.accesa.onlinestore.utils;

import eu.accesa.onlinestore.model.dto.ProductDtoNoId;
import eu.accesa.onlinestore.model.entity.ProductEntity;

public class TestUtils {

    public static ProductEntity createProductEnity(String id, String name,
                                                   String description,
                                                   Double price, Double rating,
                                                   Integer itemsInStock, String image,
                                                   String brand) {

        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(id);
        productEntity.setName(name);
        productEntity.setDescription(description);
        productEntity.setPrice(price);
        productEntity.setRating(rating);
        productEntity.setItemsInStock(itemsInStock);
        productEntity.setImage(image);
        productEntity.setBrand(brand);
        return productEntity;
    }

    public static ProductDtoNoId createProductDtoNoId(String name,
                                                      String description,
                                                      Double price, Double rating,
                                                      Integer itemsInStock, String image,
                                                      String brand) {

        ProductDtoNoId productDtoNoId = new ProductDtoNoId();
        productDtoNoId.setName(name);
        productDtoNoId.setDescription(description);
        productDtoNoId.setPrice(price);
        productDtoNoId.setRating(rating);
        productDtoNoId.setItemsInStock(itemsInStock);
        productDtoNoId.setImage(image);
        productDtoNoId.setBrand(brand);
        return productDtoNoId;
    }

}
