package eu.accesa.onlinestore.service;

import eu.accesa.onlinestore.model.dto.ProductDto;
import eu.accesa.onlinestore.model.dto.ProductDtoNoId;
import eu.accesa.onlinestore.model.dto.UserPageDto;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface ProductService {

    ProductDto createProduct(ProductDtoNoId productDtoNoId);

    ProductDto createProduct(ProductDtoNoId productDtoNoId, MultipartFile file) throws IOException;

    Page<ProductDto> findAll(UserPageDto userPageDto);

    ProductDto findById(String id);

    List<ProductDto> findByName(String name);

    Optional<GridFsResource> findImageByImageId(String id);

    List<GridFsResource> findImagesByProductId(String id);

    ProductDto updateProduct(String id, ProductDtoNoId productDtoNoId);

    void deleteProduct(String id);
}
