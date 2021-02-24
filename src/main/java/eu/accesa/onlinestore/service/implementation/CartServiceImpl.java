package eu.accesa.onlinestore.service.implementation;

import eu.accesa.onlinestore.OnlineStoreApplication;
import eu.accesa.onlinestore.exceptionhandler.EntityNotFoundException;
import eu.accesa.onlinestore.model.dto.CartDto;
import eu.accesa.onlinestore.model.entity.CartEntity;
import eu.accesa.onlinestore.repository.CartRepository;
import eu.accesa.onlinestore.service.CartService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OnlineStoreApplication.class);
    private final CartRepository cartRepository;
    private final ModelMapper mapper;

    public CartServiceImpl(CartRepository cartRepository, ModelMapper mapper) {
        this.cartRepository = cartRepository;
        this.mapper = mapper;
    }

    @Override
    public CartDto createCart(CartDto cartDto) {
        LOGGER.info("Service: creating cart with values: {}", cartDto.toString());

        if (mapper.map(cartRepository.findById(cartDto.getUserId()), CartDto.class) == cartDto) {
            return mapper.map(cartRepository.findById(cartDto.getUserId()), CartDto.class);
        } else {
            CartEntity cart = mapper.map(cartDto, CartEntity.class);
            return mapper.map(cartRepository.save(cart), CartDto.class);
        }
    }

    @Override
    public CartDto getCartById(String id) {
        LOGGER.info("Service: searching for cart with id: {}", id);

        return mapper.map(cartRepository.findById(id).orElseThrow(() -> new EntityNotFoundException
                (CartEntity.class.getName(), "CartId", id)), CartDto.class);
    }

    @Override
    public CartDto updateCart(CartDto cartDto) {
        LOGGER.info("Service: updating cart with old values: {} with new values {}",
                cartRepository.findById(cartDto.getCartId()), cartDto.toString());

        CartEntity cartFromDatabase = cartRepository.findById(cartDto.getCartId()).
                orElseThrow(() -> new EntityNotFoundException(CartEntity.class.getName(), "CartId", cartDto.getCartId()));

        mapper.map(cartDto, cartFromDatabase);

        return mapper.map(cartRepository.save(cartFromDatabase), CartDto.class);
    }

    @Override
    public void deleteCart(CartDto cartDto) {
        LOGGER.info("Service: deleting cart with values: {}", cartDto.toString());

        cartRepository.delete(mapper.map(cartDto, CartEntity.class));
    }
}
