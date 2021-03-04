package eu.accesa.onlinestore.service;

import eu.accesa.onlinestore.model.dto.ProductDto;
import eu.accesa.onlinestore.model.dto.ProductDtoPost;
import eu.accesa.onlinestore.model.dto.UserPageDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductService {

    Page<ProductDto> findAll(UserPageDto userPageDto);

    ProductDto findById(String Id);

    ProductDtoPost addNewProduct(ProductDtoPost productDtoPost);

    List<ProductDto> findByName(String name);

    void deleteProductById(String name);

    ProductDtoPost updateProduct(ProductDto productDto);

}
