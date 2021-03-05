package eu.accesa.onlinestore.service;

import eu.accesa.onlinestore.model.dto.ProductDto;
import eu.accesa.onlinestore.model.dto.ProductDtoWithoutId;
import eu.accesa.onlinestore.model.dto.UserPageDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    Page<ProductDto> findAll(UserPageDto userPageDto);

    ProductDto findById(String Id);

    ProductDtoWithoutId addNewProduct(ProductDtoWithoutId productDtoWithoutId);

    List<ProductDto> findByName(String name);

    void deleteProductById(String name);

    ProductDtoWithoutId updateProduct(ProductDto productDto);

}
