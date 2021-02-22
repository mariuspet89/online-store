package eu.accesa.onlinestore.controller;

import eu.accesa.onlinestore.model.dto.ProductDto;
import eu.accesa.onlinestore.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }


    @GetMapping()
    public ResponseEntity<List<ProductDto>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(productService.getAllProducts());
    }
}
