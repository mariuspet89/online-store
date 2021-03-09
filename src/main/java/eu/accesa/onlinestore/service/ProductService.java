package eu.accesa.onlinestore.service;

import eu.accesa.onlinestore.model.dto.ProductDto;
import eu.accesa.onlinestore.model.dto.ProductDtoNoId;
import eu.accesa.onlinestore.model.dto.UserPageDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    Page<ProductDto> findAll(UserPageDto userPageDto);

    ProductDto findById(String id);

    List<ProductDto> findByName(String name);

    ProductDto createProduct(ProductDtoNoId productDtoNoId);

    ProductDto updateProduct(String id, ProductDtoNoId productDtoNoId);

    void deleteProduct(String id);
}
