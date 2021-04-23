package eu.accesa.onlinestore.service.implementation;

import eu.accesa.onlinestore.exceptionhandler.EntityNotFoundException;
import eu.accesa.onlinestore.exceptionhandler.OnlineStoreException;
import eu.accesa.onlinestore.model.dto.CartDto;
import eu.accesa.onlinestore.model.dto.CartDtoNoId;
import eu.accesa.onlinestore.model.entity.CartEntity;
import eu.accesa.onlinestore.repository.CartRepository;
import eu.accesa.onlinestore.service.CartService;
import org.bson.types.ObjectId;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImpl implements CartService {

    private static final Logger LOGGER = LoggerFactory.getLogger(CartServiceImpl.class);

    private final CartRepository cartRepository;
    private final ModelMapper mapper;

    public CartServiceImpl(CartRepository cartRepository, ModelMapper mapper) {
        this.cartRepository = cartRepository;
        this.mapper = mapper;
    }

    @Override
    public CartDto getCartById(String id) {
        LOGGER.info("Service: searching for cart with id: {}", id);

        return mapper.map(cartRepository.findById(id).orElseThrow(() -> new EntityNotFoundException
                (CartEntity.class.getName(), "CartId", id)), CartDto.class);
    }

    @Override
    public CartDto getCartByUserId(String id) {
        LOGGER.info("Service: searching for cart belonging to user with id: {}", id);

        return mapper.map(cartRepository.findByUserId(id).orElseThrow(() -> new EntityNotFoundException
                (CartEntity.class.getName(), "CartId", id)), CartDto.class);
    }

    @Override
    public CartDto createCart(CartDtoNoId cartDtoNoId) {
        LOGGER.info("Service: creating cart with values: {}", cartDtoNoId.toString());
        ObjectId objectId = new ObjectId();
        CartEntity cart = mapper.map(cartDtoNoId, CartEntity.class);
        cart.setId(objectId.toString());
        try {
            return mapper.map(cartRepository.save(cart), CartDto.class);
        }catch(DuplicateKeyException e){
            throw new OnlineStoreException( "User with Id: " + cartDtoNoId.getUserId() + " already has a cart.");
        }
    }

    @Override
    public CartDto updateCart(String id, CartDtoNoId cartDtoNoId) {
        LOGGER.info("Service: updating cart with old values: {} with new values {}",
                cartRepository.findById(id).toString(), cartDtoNoId.toString());

        CartEntity cartFromDatabase = cartRepository.findById(id).
                orElseThrow(() -> new EntityNotFoundException(CartEntity.class.getName(), "CartId", id));

        mapper.map(cartDtoNoId, cartFromDatabase);
        cartFromDatabase.setProducts(cartDtoNoId.getProducts());

        CartEntity savedCartEntity = cartRepository.save(cartFromDatabase);
        return mapper.map(savedCartEntity, CartDto.class);
    }

    @Override
    public void deleteCart(String id) {
        LOGGER.info("Service: deleting cart with ID = {}", id);
        cartRepository.deleteById(id);
    }
}
