package eu.accesa.onlinestore.service.implementation;

import eu.accesa.onlinestore.model.dto.ProductDto;
import eu.accesa.onlinestore.model.dto.ProductDtoNoId;
import eu.accesa.onlinestore.model.entity.ProductEntity;
import eu.accesa.onlinestore.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static eu.accesa.onlinestore.utils.ProductTestUtils.createProductDtoNoId;
import static eu.accesa.onlinestore.utils.ProductTestUtils.createProductEnity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Spy
    private ModelMapper mapper;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void createProduct() {
        // GIVEN
        ProductEntity createdProductEntity = createProductEnity("123",
                "test name 1", "test description 1", 1.2, 2.5, 0,
                "test1", "test1");


        ProductDtoNoId productDtoNoId = createProductDtoNoId(
                "test name 1", "test description 1", 1.2, 2.5, 0,
                "test1", "test1");

        when(productRepository.save(any(ProductEntity.class))).thenReturn(createdProductEntity);

        // WHEN
        ProductDto productDto = productService.createProduct(productDtoNoId);

        // THEN
        assertNotNull(productDto, "Created product can not be null");
        assertNotNull(productDto.getId(), "The Id Should not be null");
        assertNotEquals(0, productDto.getId().length(), "The Id Should not be Empty");
        assertEquals("test name 1", productDto.getName());
        assertEquals("test description 1", productDto.getDescription());
        assertEquals(1.2, productDto.getPrice());
        assertEquals(2.5, productDto.getRating());

        verify(productRepository).save(any(ProductEntity.class));
    }

    @Test
    public void findById() {
        String id = "123";
        ProductEntity foundProduct = createProductEnity("123",
                "test name 1", "test description 1", 1.2, 2.5, 0,
                "test1", "test1");
        when(productRepository.findById(id)).thenReturn(Optional.of(foundProduct));
        ProductDto foundProductDto = productService.findById(id);
        assertEquals(id, foundProductDto.getId(), "ID mismatch");
        verify(productRepository).findById(id);

    }

    @Test
    public void findByName() {
        String name = "test name 1";
        ProductEntity productEntity = createProductEnity("123",
                "test name 1", "test description 1", 1.2, 2.5, 0,
                "test1", "test1");

        List<ProductEntity> foundProducts = new ArrayList<>();
        foundProducts.add(productEntity);

        when(productRepository.findByNameIsContainingIgnoreCase(name))
                .thenReturn(foundProducts);

        List<ProductDto> foundProductDtos = productService.findByName(name);
        assertEquals(name, foundProductDtos.get(0).getName());
        verify(productRepository).findByNameIsContainingIgnoreCase(name);
    }

    @Test
    public void update() {

        String id = "123";
        ProductEntity foundProduct = createProductEnity("123",
                "test name 1", "test description 1", 1.2, 2.5, 0,
                "test1", "test1");


        ProductEntity updatedProductEntity = createProductEnity("123",
                "update name", "update description ", 3.3, 3.3, 0,
                "update image", "update");

        when(productRepository.findById(id)).thenReturn(Optional.of(foundProduct));
        when(productRepository.save(foundProduct)).thenReturn(updatedProductEntity);

        ProductDtoNoId productDtoNoId = createProductDtoNoId(
                updatedProductEntity.getName(),
                updatedProductEntity.getDescription(),
                updatedProductEntity.getPrice(),
                updatedProductEntity.getRating(),
                updatedProductEntity.getItemsInStock(),
                updatedProductEntity.getImage(),
                updatedProductEntity.getBrand()
        );

        ProductDtoNoId updatedDto = productService.updateProduct(id, productDtoNoId);
        assertEquals("update name", updatedDto.getName());
        assertEquals("update description ", updatedDto.getDescription());

    }

    @Test
    void delete() {
        ProductEntity foundProduct = createProductEnity("123",
                "test name 1", "test description 1", 1.2, 2.5, 0,
                "test1", "test1");

        when(productRepository.findById(foundProduct.getId())).thenReturn(Optional.of(foundProduct));
        productService.deleteProduct(foundProduct.getId());
        verify(productRepository, times(1)).delete(foundProduct);

    }

}
