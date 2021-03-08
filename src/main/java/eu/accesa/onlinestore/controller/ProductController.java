package eu.accesa.onlinestore.controller;

import eu.accesa.onlinestore.model.dto.ProductDto;
import eu.accesa.onlinestore.model.dto.ProductDtoWithoutId;
import eu.accesa.onlinestore.model.dto.UserPageDto;
import eu.accesa.onlinestore.repository.ProductRepository;
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
    private final ProductRepository productRepository;

    public ProductController(ProductService productService, ProductRepository productRepository) {
        this.productService = productService;
        this.productRepository = productRepository;
    }

    @PostMapping
    @ApiResponse(responseCode = "201", description = "Successfully added a product")
    public ResponseEntity<ProductDtoWithoutId> createNewProduct(@Valid @RequestBody ProductDtoWithoutId productDtoWithoutId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.addNewProduct(productDtoWithoutId));
    }

    @GetMapping("name-contains/{name}")
    public ResponseEntity<List<ProductDto>> findByNameContains(@PathVariable String name) {
        return ResponseEntity.status(HttpStatus.OK).body((productService.findByName(name)));
    }

    @GetMapping("/findAll")
    public ResponseEntity<Page<ProductDto>> findAll(UserPageDto userPageDto) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.findAll(userPageDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> findById(@PathVariable String id) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.findById(id));
    }

    @PutMapping
    public ResponseEntity<ProductDtoWithoutId> updateProduct(@Valid @RequestBody ProductDto productDto) {
        return ResponseEntity.status(HttpStatus.OK).body(productService.updateProduct(productDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProductById(@PathVariable String id) {
       productService.deleteProductById(id);
        return ResponseEntity.status(HttpStatus.OK).body("Product Deleted");
    }

}
