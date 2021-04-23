package eu.accesa.onlinestore.service.implementation;


import eu.accesa.onlinestore.model.dto.CartDto;
import eu.accesa.onlinestore.model.dto.CartDtoNoId;
import eu.accesa.onlinestore.model.entity.CartEntity;
import eu.accesa.onlinestore.repository.CartRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static eu.accesa.onlinestore.utils.CartTestUtils.createCartDto;
import static eu.accesa.onlinestore.utils.CartTestUtils.createCartEntity;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CartServiceImplTest {

    @Spy
    private ModelMapper mapper;

    @Mock
    private CartRepository cartRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    // public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);
    @Test
    void createCart() {

        //GIVEN

        Map<String, Integer> productsToBuy = new HashMap<>();
        productsToBuy.put("blaba22", 3432);

        CartEntity createdCartEntity = createCartEntity("4141", "3232", productsToBuy);

        CartDtoNoId cartDtoNoId = createCartDto(null, "3232", productsToBuy);

        //WHEN
        when(cartRepository.save(any(CartEntity.class))).thenReturn(createdCartEntity);
        CartDto cartDto = cartService.createCart(cartDtoNoId);

        //THEN
        assertNotNull(cartDto, "created cart cannot be null");
        assertNotNull(cartDto.getId(), "Id cart should not be null");
        assertNotEquals(0, cartDto.getId().length(), "The Id should not be empty.");
        assertEquals(cartDtoNoId.getUserId(), cartDto.getUserId(), "User Id should be the same.");
        assertTrue(cartDto.getProducts().equals(cartDtoNoId.getProducts()));

        verify(cartRepository).save(any(CartEntity.class));
    }

    @Test
    void findById() {
        Map<String, Integer> productsToBuy = new HashMap<>();
        productsToBuy.put("blaba22", 3432);
        String id = "1313";
        CartEntity foundCart = createCartEntity("1313", "1414", productsToBuy);

        when(cartRepository.findById(id)).thenReturn(Optional.of(foundCart));

        CartDto foundCartDto = cartService.getCartById(id);

        assertNotNull(foundCartDto);
        assertEquals(id, foundCartDto.getId(), "ID mismatch");

        verify(cartRepository).findById(id);
    }


    @Test
    void findCartByUserID() {

        Map<String, Integer> productsToBuy = new HashMap<>();
        productsToBuy.put("blaba22", 3432);
        String id = "3232";

        CartEntity foundCartEntity = createCartEntity("4141", "3232", productsToBuy);

        when(cartRepository.findByUserId(anyString())).thenReturn(Optional.of(foundCartEntity));

        CartDto foundCartDto = cartService.getCartByUserId(id);

        assertEquals(id, foundCartDto.getUserId());
        assertNotNull(foundCartEntity);
        assertNotNull(foundCartDto);

        verify(cartRepository).findByUserId(id);
    }

    @Test
    void testUpdateCart() {

        Map<String, Integer> productsToBuy = new HashMap<>();
        productsToBuy.put("blaba22", 3432);
        CartDtoNoId cartDtoNoId = createCartDto(null, "1", productsToBuy);
        CartEntity foundCartEntity = createCartEntity("4141", "3232", productsToBuy);
        CartEntity updatedCartEntity = createCartEntity("4141", "1", productsToBuy);

        when(cartRepository.findById(anyString())).thenReturn(Optional.of(foundCartEntity));
        when(cartRepository.save(foundCartEntity)).thenReturn(updatedCartEntity);

        CartDto cartDto = cartService.updateCart("4141", cartDtoNoId);

        assertEquals(cartDtoNoId.getUserId(), cartDto.getUserId(), "User should not be the same.");

        verify(cartRepository, times(2)).findById("4141");
        verify(cartRepository).save(foundCartEntity);
    }

    @Test
    void testDeleteCart() {
        Map<String, Integer> productsToBuy = new HashMap<>();
        productsToBuy.put("blaba22", 3432);
        CartEntity foundCart = createCartEntity("4141", "1414", productsToBuy);
        lenient().when(cartRepository.findById(foundCart.getId())).thenReturn(Optional.of(foundCart));
        cartService.deleteCart(foundCart.getId());
    }


}
