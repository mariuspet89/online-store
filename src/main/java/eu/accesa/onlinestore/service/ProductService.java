package eu.accesa.onlinestore.service;

import eu.accesa.onlinestore.model.dto.ProductDto;

import java.util.List;

public interface ProductService {

    List<ProductDto> findAll();
    ProductDto findById(String Id);
    ProductDto addNewProduct(ProductDto productDto);
    List<ProductDto>findByName(String name);
    void deleteProductById(String name);
    ProductDto updateProduct(ProductDto productDto);

}
