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

    public ProductServiceImpl(ProductRepository productRepository,ModelMapper modelMapper) {
        this.productRepository=productRepository;
        this.modelMapper = modelMapper;
    }


    @Override
    public List<ProductDto> findAll() {
        List<ProductEntity> products = productRepository.findAll();
        return products.stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .collect(toList());
    }

}
