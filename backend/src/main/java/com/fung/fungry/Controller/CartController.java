package com.fung.fungry.Controller;

import com.fung.fungry.ModelDTO.CartDTO;
import com.fung.fungry.ServiceIMPL.CartServiceIMPL;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api-v1.0/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartServiceIMPL cartServiceIMPL;

    // View Cart
    @GetMapping("/{userId}")
    public ResponseEntity<CartDTO> viewCart(@PathVariable Long userId) {

        CartDTO cartDTO = cartServiceIMPL.viewCart(userId);
        return ResponseEntity.ok(cartDTO);
    }

    // Add Item to Cart
    @PostMapping("/add/{userId}/{menuItemId}")
    public ResponseEntity<String> addToCart(@PathVariable Long userId,
                                          @PathVariable Long menuItemId) {

        cartServiceIMPL.addToCart(userId, menuItemId);
        return ResponseEntity.ok("Menu item added successfully.");
    }

    //Increase Quantity by 1
    @PutMapping("/increase/{cartItemId}/{userId}")
    public ResponseEntity<CartDTO> increaseQuantity(@PathVariable Long cartItemId,
                                                    @PathVariable Long userId) {

        CartDTO cartDTO = cartServiceIMPL.updateItemQuantityByOne(cartItemId, userId);
        return ResponseEntity.ok(cartDTO);
    }

    //Remove Item from Cart
    @DeleteMapping("/remove/{cartItemId}/{userId}")
    public ResponseEntity<CartDTO> removeItem(@PathVariable Long cartItemId,
                                              @PathVariable Long userId) {

        CartDTO cartDTO = cartServiceIMPL.removeItem(userId, cartItemId);
        return ResponseEntity.ok(cartDTO);
    }

    //Clear Entire Cart
    @DeleteMapping("/clear/{userId}")
    public ResponseEntity<CartDTO> clearCart(@PathVariable Long userId) {

        CartDTO cartDTO = cartServiceIMPL.clearAll(userId);
        return ResponseEntity.ok(cartDTO);
    }
}
