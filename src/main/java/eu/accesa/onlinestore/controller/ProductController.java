package eu.accesa.onlinestore.controller;

import eu.accesa.onlinestore.model.dto.ProductDto;
import eu.accesa.onlinestore.model.entity.ProductEntity;
import eu.accesa.onlinestore.repository.ProductRepository;
import eu.accesa.onlinestore.service.ProductService;
import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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

    @GetMapping("/findAll")
    public ResponseEntity<List<ProductDto>> findAll() {
        return ResponseEntity.status(HttpStatus.OK).body(productService.findAll());
    }

//    @GetMapping("/{Id}")
//    public ResponseEntity<ProductDto> findById(@PathVariable String Id) {
//        return ResponseEntity.status(HttpStatus.OK).body(productService.findById(Id));
//    }
    @GetMapping("/{Id}")
   public ResponseEntity<ProductEntity> findById(@PathVariable String Id){
        return ResponseEntity.status(HttpStatus.OK).body(productRepository.findBy_idEquals(Id));
    }

}
