package eu.accesa.onlinestore.service.implementation;

import eu.accesa.onlinestore.exceptionhandler.EntityNotFoundException;
import eu.accesa.onlinestore.model.dto.ProductDto;
import eu.accesa.onlinestore.model.entity.ProductEntity;
import eu.accesa.onlinestore.repository.ProductRepository;
import eu.accesa.onlinestore.service.ProductService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);
    private final ModelMapper modelMapper;
    ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ProductDto addNewProduct(ProductDto productDto) {
        LOGGER.info("Creating Product " + productDto.getId());

        ProductEntity productEntity = modelMapper.map(productDto, ProductEntity.class);

        return modelMapper.map(productRepository.save(productEntity), ProductDto.class);
    }

    @Override
    public Page<ProductDto> findAll(Pageable pageable) {
        LOGGER.info("Searching for all Products");
        Pageable paging = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        return modelMapper.map(productRepository.findAll(paging),new TypeToken<Page<ProductDto>>(){}.getType());
    }

    @Override
    public ProductDto findById(String id) {
        LOGGER.info("Searching for the Product with the following ID: " + id);

        ProductEntity productEntity = productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(ProductEntity.class.getSimpleName(),
                        "ProductId", id));

        return modelMapper.map(productEntity, ProductDto.class);
    }


    @Override
    public List<ProductDto> findByName(String name) {
        LOGGER.info("Searching for Products with the following Name: " + name);

        List<ProductEntity> products = productRepository.findByNameIsContainingIgnoreCase(name);

        return products.stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .collect(toList());
    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        LOGGER.info("Updating for Product with the following Id: " + productDto.getId());

        ProductEntity productEntity = productRepository.findById(productDto.getId()).orElseThrow(
                () -> new EntityNotFoundException(ProductEntity.class.getSimpleName(),
                        "ProductId", productDto.getId()));

        modelMapper.map(productDto, productEntity);
        productRepository.save(productEntity);

        return modelMapper.map(productEntity, ProductDto.class);
    }

    @Override
    public void deleteProductById(String id) {
        LOGGER.info("Deleting for Product with the following Id: " + id);

        ProductEntity productEntity = productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(ProductEntity.class.getSimpleName(),
                        "ProductId", id));

        productRepository.delete(productEntity);
    }

}
