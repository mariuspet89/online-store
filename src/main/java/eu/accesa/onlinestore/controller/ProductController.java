package eu.accesa.onlinestore.controller;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.lang.Nullable;
import eu.accesa.onlinestore.model.dto.ProductDto;
import eu.accesa.onlinestore.model.dto.ProductDtoNoId;
import eu.accesa.onlinestore.model.dto.UserPageDto;
import eu.accesa.onlinestore.service.ProductService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@CrossOrigin
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @ApiResponse(responseCode = "201", description = "Successfully added a product")
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestPart("productDto") ProductDtoNoId productDtoNoId,
                                                    @Nullable MultipartFile file) throws IOException {
        if (file != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(productDtoNoId, file));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(productDtoNoId));
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
        return ResponseEntity.status(HttpStatus.OK).body((productService.findByName(name)));
    }

    @GetMapping("image/{id}")
    public ResponseEntity<InputStreamResource> findProductImageByImageId(@PathVariable String id) throws IOException {
        Optional<GridFsResource> file = productService.findImageByImageId(id);

        if (file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else {
            GridFsResource gridFsResource = file.get();
            GridFSFile gridFSFile = gridFsResource.getGridFSFile();

            String contentType;
            if (gridFSFile != null && gridFSFile.getMetadata() != null) {
                contentType = gridFSFile.getMetadata().get("_contentType").toString();
            } else {
                contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
            }

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .contentLength(gridFsResource.contentLength())
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(gridFsResource);
        }
    }

    @GetMapping("/images/{productId}")
    public ResponseEntity<List<InputStreamResource>> findProductImagesByProductId(@PathVariable String productId) {
        List<GridFsResource> gridFsResources = productService.findImagesByProductId(productId);

        return ResponseEntity
                .status(HttpStatus.OK)
                .contentLength(gridFsResources.size())
                .body(new ArrayList<>(gridFsResources));
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
