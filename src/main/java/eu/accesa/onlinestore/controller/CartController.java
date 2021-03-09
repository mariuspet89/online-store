package eu.accesa.onlinestore.controller;

import eu.accesa.onlinestore.model.dto.CartDto;
import eu.accesa.onlinestore.model.dto.CartDtoNoId;
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

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CartDto getCartById(@PathVariable String id) {
        return cartService.getCartById(id);
    }

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public CartDto getCartByUserId(@PathVariable String userId) {
        return cartService.getCartByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CartDto createCart(@Valid @RequestBody CartDtoNoId cartDtoNoId) {
        return cartService.createCart(cartDtoNoId);
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public CartDto updateCart(@PathVariable String id, @Valid @RequestBody CartDtoNoId cartDtoNoId) {
        return cartService.updateCart(id, cartDtoNoId);
    }

    @DeleteMapping("{/id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCart(@PathVariable String id) {
        cartService.deleteCart(id);
    }
}
