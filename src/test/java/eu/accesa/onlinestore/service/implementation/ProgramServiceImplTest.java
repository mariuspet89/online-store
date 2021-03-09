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

import static eu.accesa.onlinestore.utils.TestUtils.createProductDtoNoId;
import static eu.accesa.onlinestore.utils.TestUtils.createProductEnity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProgramServiceImplTest {

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
}
