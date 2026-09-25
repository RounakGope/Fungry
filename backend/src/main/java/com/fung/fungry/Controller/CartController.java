package com.fung.fungry.Controller;

import com.fung.fungry.Configuration.UserPrincipal;
import com.fung.fungry.ModelDTO.CartDTO;
import com.fung.fungry.ServiceIMPL.CartServiceIMPL;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api-v2.0/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartServiceIMPL cartServiceIMPL;

    // View Cart
    @GetMapping("/")
    public ResponseEntity<CartDTO> viewCart(@AuthenticationPrincipal UserPrincipal principal) {
        Long userId=principal.getUser().getUserId();

        CartDTO cartDTO = cartServiceIMPL.viewCart(userId);
        return ResponseEntity.ok(cartDTO);
    }

    // Add Item to Cart
    @PostMapping("/add/{menuItemId}")
    public ResponseEntity<String> addToCart(
                                          @PathVariable Long menuItemId,@AuthenticationPrincipal UserPrincipal principal) {

        Long userId=principal.getUser().getUserId();
        cartServiceIMPL.addToCart(userId, menuItemId);
        return ResponseEntity.ok("Menu item added successfully.");
    }

    //Increase Quantity by 1
    @PutMapping("/increase/{cartItemId}")
    public ResponseEntity<CartDTO> increaseQuantity(@PathVariable Long cartItemId,
                                                    @AuthenticationPrincipal UserPrincipal principal) {
        Long userId=principal.getUser().getUserId();
        CartDTO cartDTO = cartServiceIMPL.updateItemQuantityByOne(cartItemId, userId);
        return ResponseEntity.ok(cartDTO);
    }

    //Remove Item from Cart
    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<CartDTO> removeItem(@PathVariable Long cartItemId,
                                              @AuthenticationPrincipal UserPrincipal principal) {
        Long userId=principal.getUser().getUserId();
        CartDTO cartDTO = cartServiceIMPL.removeItem(userId, cartItemId);
        return ResponseEntity.ok(cartDTO);
    }

    //Clear Entire Cart
    @DeleteMapping("/clear")
    public ResponseEntity<CartDTO> clearCart(@AuthenticationPrincipal UserPrincipal principal) {
        Long userId=principal.getUser().getUserId();
        CartDTO cartDTO = cartServiceIMPL.clearAll(userId);
        return ResponseEntity.ok(cartDTO);
    }
}
