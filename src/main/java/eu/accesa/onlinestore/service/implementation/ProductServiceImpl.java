package eu.accesa.onlinestore.service.implementation;

import eu.accesa.onlinestore.model.dto.ProductDto;
import eu.accesa.onlinestore.model.entity.ProductEntity;
import eu.accesa.onlinestore.repository.ProductRepository;
import eu.accesa.onlinestore.service.ProductService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

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
    public List<ProductDto> findAll() {
        List<ProductEntity> products = productRepository.findAll();
        return products.stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .collect(toList());
    }

    @Override
    public ProductDto findById(String Id) {
        ProductEntity productEntity = productRepository.findById(Id).orElseThrow();
        return modelMapper.map(productEntity, ProductDto.class);
    }

    @Override
    public ProductDto addNewProduct(ProductDto productDto) {
        ProductEntity productEntity = modelMapper.map(productDto, ProductEntity.class);

        return modelMapper.map(productRepository.save(productEntity), ProductDto.class);
    }

    @Override
    public List<ProductDto> findByName(String name) {
        List<ProductEntity> products = productRepository.findByNameIsContainingIgnoreCase(name);

        return products.stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .collect(toList());
    }

    @Override
    public void deleteProductById(String name) {

        ProductEntity productEntity = productRepository.findById(name).orElseThrow();

        productRepository.delete(productEntity);

    }

    @Override
    public ProductDto updateProduct(ProductDto productDto) {
        ProductEntity productEntity = productRepository.findById(productDto.getName()).orElseThrow();
        modelMapper.map(productDto, productEntity);
        productRepository.save(productEntity);
        return modelMapper.map(productEntity, ProductDto.class);
    }
}
