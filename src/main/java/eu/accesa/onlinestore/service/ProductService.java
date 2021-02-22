package eu.accesa.onlinestore.service;

import eu.accesa.onlinestore.model.dto.ProductDto;

import java.util.List;

public interface ProductService {

    List<ProductDto> findAll();
}
