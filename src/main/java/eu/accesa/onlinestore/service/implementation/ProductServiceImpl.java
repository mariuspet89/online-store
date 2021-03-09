package eu.accesa.onlinestore.service.implementation;

import eu.accesa.onlinestore.exceptionhandler.EntityNotFoundException;
import eu.accesa.onlinestore.model.dto.ProductDto;
import eu.accesa.onlinestore.model.dto.ProductDtoNoId;
import eu.accesa.onlinestore.model.dto.UserPageDto;
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

import java.util.List;

import static java.util.stream.Collectors.toList;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;

    public ProductServiceImpl(ModelMapper modelMapper, ProductRepository productRepository) {
        this.modelMapper = modelMapper;
        this.productRepository = productRepository;
    }

    @Override
    public ProductDto createProduct(ProductDtoNoId productDtoNoId) {
        LOGGER.info("Creating Product ");

        ProductEntity productEntity = modelMapper.map(productDtoNoId, ProductEntity.class);

        return modelMapper.map(productRepository.save(productEntity), ProductDto.class);
    }

    @Override
    public Page<ProductDto> findAll(UserPageDto userPageDto) {
        LOGGER.info("Searching for all Products");
        Sort sort = Sort.by(userPageDto.getSortDirection(), userPageDto.getSortBy());
        Pageable paging = PageRequest.of(userPageDto.getPageNo(), userPageDto.getPageSize(), sort);

        return modelMapper.map(productRepository.findAll(paging), new TypeToken<Page<ProductDto>>() {
        }.getType());
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
    public ProductDto updateProduct(String id, ProductDtoNoId productDtoNoId) {
        ProductEntity productEntity = productRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(ProductEntity.class.getSimpleName(), "ProductId", id));

        modelMapper.map(productDtoNoId, productEntity);
        productRepository.save(productEntity);

        return modelMapper.map(productEntity, ProductDto.class);
    }

    @Override
    public void deleteProduct(String id) {
        LOGGER.info("Deleting for Product with the following Id: " + id);

        ProductEntity productEntity = productRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(ProductEntity.class.getSimpleName(),
                        "ProductId", id));

        productRepository.delete(productEntity);
    }
}
