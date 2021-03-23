package eu.accesa.onlinestore.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import eu.accesa.onlinestore.model.dto.ProductDto;
import eu.accesa.onlinestore.model.dto.ProductDtoNoId;
import eu.accesa.onlinestore.service.implementation.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

import static eu.accesa.onlinestore.utils.ProductTestUtils.createProductDto;
import static eu.accesa.onlinestore.utils.ProductTestUtils.createProductDtoNoId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
@ContextConfiguration(classes = {ProductController.class, ProductServiceImpl.class})
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductServiceImpl productService;

    @Autowired
    private ObjectMapper objectMapper;

    @Captor
    private ArgumentCaptor<ProductDtoNoId> productDtoArgumentCaptor;

    @Test
    void testFindById() throws Exception {
        String productId = "1234";
        ProductDto productDto = createProductDto(productId, "test1", "test", 0.0, 0.0,
                0, "test1", "test1");

        when(productService.findById(productDto.getId())).thenReturn(productDto);
        mockMvc.perform(get("/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.id").value(productDto.getId()));
        verify(productService).findById(productDto.getId());
    }

    @Test
    void testCreateProductWithMultipartFile() throws Exception {
        // GIVEN
        ProductDtoNoId requestProductDto = createProductDto(null, "test1", "test", 0.0, 0.0,
                0, "", "test1");
        MockMultipartFile file = new MockMultipartFile("file", "bike.png",
                MediaType.IMAGE_PNG_VALUE, new byte[1]);
        MockMultipartFile json = new MockMultipartFile("productDto", "json",
                MediaType.APPLICATION_JSON_VALUE, asJsonString(requestProductDto).getBytes(StandardCharsets.UTF_8));

        ProductDto createdProduct = createProductDto("1", "test1", "test", 0.0, 0.0,
                0, "imageId", "test1");

        doReturn(createdProduct).when(productService).createProduct(any(ProductDtoNoId.class), any(MultipartFile.class));

        // WHEN
        mockMvc.perform(multipart("/products")
                .file(file)
                .file(json)
                .accept(MediaType.APPLICATION_JSON))
                // validate the status and response content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                // validate response
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name", is(requestProductDto.getName())))
                .andExpect(jsonPath("$.description", is(requestProductDto.getDescription())))
                .andExpect(jsonPath("$.price", is(requestProductDto.getPrice())))
                .andExpect(jsonPath("$.rating", is(requestProductDto.getRating())))
                .andExpect(jsonPath("$.itemsInStock", is(requestProductDto.getItemsInStock())))
                .andExpect(jsonPath("$.image", is(createdProduct.getImage())))
                .andExpect(jsonPath("$.brand", is(requestProductDto.getBrand())));

        // THEN
        verify(productService).createProduct(any(ProductDtoNoId.class), any(MultipartFile.class));
        verifyNoMoreInteractions(productService);
    }

    @Test
    void testCreateProductWithoutMultipartFile() throws Exception {
        // GIVEN
        ProductDtoNoId requestProductDto = createProductDto(null, "test1", "test", 0.0, 0.0,
                0, "imageUrl", "test1");
        MockMultipartFile json = new MockMultipartFile("productDto", "json",
                MediaType.APPLICATION_JSON_VALUE, asJsonString(requestProductDto).getBytes(StandardCharsets.UTF_8));

        ProductDto createdProduct = createProductDto("1", "test1", "test", 0.0, 0.0,
                0, "imageUrl", "test1");

        doReturn(createdProduct).when(productService).createProduct(any(ProductDtoNoId.class));

        // WHEN
        mockMvc.perform(multipart("/products")
                .file(json)
                .accept(MediaType.APPLICATION_JSON))
                // validate the status and response content type
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                // validate response
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.name", is(requestProductDto.getName())))
                .andExpect(jsonPath("$.description", is(requestProductDto.getDescription())))
                .andExpect(jsonPath("$.price", is(requestProductDto.getPrice())))
                .andExpect(jsonPath("$.rating", is(requestProductDto.getRating())))
                .andExpect(jsonPath("$.itemsInStock", is(requestProductDto.getItemsInStock())))
                .andExpect(jsonPath("$.image", is(requestProductDto.getImage())))
                .andExpect(jsonPath("$.brand", is(requestProductDto.getBrand())));

        // THEN
        verify(productService).createProduct(any(ProductDtoNoId.class));
        verifyNoMoreInteractions(productService);
    }

    @Test
    void testUpdateProduct() throws Exception {
        String productId = "1234";
        ProductDtoNoId product = createProductDtoNoId("test1", "test1", 0.0, 0.0,
                0, "test1", "test1");
        ProductDto updateProduct = createProductDto(productId, "test1update", "test1update", 2.5, 0.0,
                0, "test1update", "test1update");

        when(productService.findById(productId)).thenReturn(updateProduct);
        when(productService.updateProduct(productId, product)).thenReturn(updateProduct);
        final ObjectMapper objectMapper = new ObjectMapper();
        final String productToUpdateJson = objectMapper.writeValueAsString(updateProduct);
        final ResultActions resultActions = mockMvc.perform(put("/products/{id}", productId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(productToUpdateJson));
        resultActions.andExpect(status().isOk());
        verify(productService).updateProduct(Mockito.anyString(), productDtoArgumentCaptor.capture());
        assertThat(productDtoArgumentCaptor.getValue().getBrand().equals("test1update"));
        assertThat(productDtoArgumentCaptor.getValue().getPrice()).isEqualTo(2.5);
        verifyNoMoreInteractions(productService);
    }

    @Test
    void testDeleteProduct() throws Exception {
        // GIVEN
        String productId = "1";

        // WHEN
        mockMvc.perform(delete("/products/{id}", productId))
                // validate the status and response content type
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.valueOf("text/plain;charset=UTF-8")))
                // validate body response
                .andExpect(content().string(containsString("Product Deleted")))
                .andReturn().getResponse().getContentAsString();

        // THEN
        verify(productService).deleteProduct(productId);
        verifyNoMoreInteractions(productService);
    }

    private String asJsonString(final Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

}
