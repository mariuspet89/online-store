package eu.accesa.onlinestore.controller;

import eu.accesa.onlinestore.model.dto.CartDto;
import eu.accesa.onlinestore.service.CartService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    private CartDto createCart(@Valid @RequestBody CartDto cartDto){
        return cartService.createCart(cartDto);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    private CartDto getCartById(@PathVariable String id){
        return cartService.getCartById(id);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    private CartDto updateCart(@Valid @RequestBody CartDto cartDto){
        return cartService.updateCart(cartDto);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    private void deleteCart(@Valid @RequestBody CartDto cartDto){
        cartService.deleteCart(cartDto);
    }
}
