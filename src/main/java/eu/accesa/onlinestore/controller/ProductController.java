package eu.accesa.onlinestore.controller;

import eu.accesa.onlinestore.model.dto.ProductDto;
import eu.accesa.onlinestore.model.dto.ProductDtoNoId;
import eu.accesa.onlinestore.model.dto.UserPageDto;
import eu.accesa.onlinestore.service.ProductService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/findAll")
    public ResponseEntity<Page<ProductDto>> findAll(UserPageDto userPageDto) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.findAll(userPageDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> findById(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.findById(id));
    }

    @GetMapping("name-contains/{name}")
    public ResponseEntity<List<ProductDto>> findByNameContains(@PathVariable String name) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.findByName(name));
    }

    @PostMapping
    @ApiResponse(responseCode = "201", description = "Successfully added a product")
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDtoNoId productDtoNoId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(productDtoNoId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(@PathVariable String id, @Valid @RequestBody ProductDtoNoId productDtoNoId) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.updateProduct(id, productDtoNoId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable String id) {
        productService.deleteProduct(id);
        return ResponseEntity.status(HttpStatus.OK).body("Product Deleted");
    }
}
