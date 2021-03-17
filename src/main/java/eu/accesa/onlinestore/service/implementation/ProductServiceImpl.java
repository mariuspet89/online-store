package eu.accesa.onlinestore.service.implementation;

import com.mongodb.client.gridfs.model.GridFSFile;
import eu.accesa.onlinestore.exceptionhandler.EntityNotFoundException;
import eu.accesa.onlinestore.model.dto.ProductDto;
import eu.accesa.onlinestore.model.dto.ProductDtoNoId;
import eu.accesa.onlinestore.model.dto.UserPageDto;
import eu.accesa.onlinestore.model.entity.ProductEntity;
import eu.accesa.onlinestore.repository.FileRepository;
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
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.util.stream.Collectors.toList;

@Service
public class ProductServiceImpl implements ProductService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProductServiceImpl.class);

    private final ModelMapper modelMapper;
    private final ProductRepository productRepository;
    private final FileRepository fileRepository;

    public ProductServiceImpl(ModelMapper modelMapper,
                              ProductRepository productRepository,
                              FileRepository fileRepository) {
        this.modelMapper = modelMapper;
        this.productRepository = productRepository;
        this.fileRepository = fileRepository;
    }

    @Override
    public ProductDto createProduct(ProductDtoNoId productDtoNoId) {
        LOGGER.info("Creating Product ");

        ProductEntity productEntity = modelMapper.map(productDtoNoId, ProductEntity.class);

        productEntity = productRepository.save(productEntity);
        return modelMapper.map(productEntity, ProductDto.class);
    }

    @Override
    public ProductDto createProduct(ProductDtoNoId productDtoNoId, MultipartFile file) throws IOException {
        LOGGER.info("Creating Product " + productDtoNoId.getName());

        ProductEntity productEntity = modelMapper.map(productDtoNoId, ProductEntity.class);

        String imageId = fileRepository.store(file.getOriginalFilename(), file.getContentType(), file.getSize(),
                file.getInputStream()).toString();

        productEntity.setImage(imageId);

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
    public Optional<GridFsResource> findImageByImageId(String id) {
        LOGGER.info("Searching for image with the following id: " + id);
        GridFSFile file = fileRepository.findImageOfProductByImageId(id);

        if (file == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        } else return Optional.of(new GridFsResource(file, fileRepository.getContent(file.getId())));
    }

    @Override
    public List<GridFsResource> findImagesByProductId(String id) {
        LOGGER.info("Searching for images of the following product: " + id);
        List<GridFsResource> files = new ArrayList<>();
        for (GridFSFile file : fileRepository.findImagesOfProductByProductId(id)) {
            files.add(new GridFsResource(file, fileRepository.getContent(file.getId())));

        }
        return files;
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
