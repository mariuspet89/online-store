package eu.accesa.onlinestore.service;

import eu.accesa.onlinestore.model.dto.ProductDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductService {

    Page<ProductDto> findAll(Pageable pageable);
    ProductDto findById(String Id);
    ProductDto addNewProduct(ProductDto productDto);
    List<ProductDto>findByName(String name);
    void deleteProductById(String name);
    ProductDto updateProduct(ProductDto productDto);

}
