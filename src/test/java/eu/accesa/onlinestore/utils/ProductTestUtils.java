package eu.accesa.onlinestore.utils;

import eu.accesa.onlinestore.model.dto.ProductDtoNoId;
import eu.accesa.onlinestore.model.dto.UserPageDto;
import eu.accesa.onlinestore.model.entity.ProductEntity;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;

public class ProductTestUtils {

    public static List<ProductEntity> testProductsList() {

        return Arrays.asList(
                createProductEnity("123",
                        "test name 1", "test description 1", 1.2, 2.5, 0,
                        "test1", "test1"),
                createProductEnity("456",
                        "test name 2", "test description 2", 2.2, 3.5, 0,
                        "test2", "test2"));

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

    public static UserPageDto createUserPageDto(Integer pageNo, Integer pageSize,
                                                Sort.Direction sortDirection, String sortBy) {
        UserPageDto userPageDto = new UserPageDto();
        userPageDto.setPageNo(pageNo);
        userPageDto.setPageSize(pageSize);
        userPageDto.setSortDirection(sortDirection);
        userPageDto.setSortBy(sortBy);
        return userPageDto;
    }

}
