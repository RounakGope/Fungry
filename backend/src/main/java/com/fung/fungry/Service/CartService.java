package com.fung.fungry.Service;

import com.fung.fungry.ModelDTO.CartDTO;

public interface CartService {
//no create or delete methods
    public CartDTO viewCart( Long userId);

    public CartDTO addToCart(Long userId, Long menuItemId);

    public CartDTO updateItemQuantity(Long cartItemId, Long userId, Integer quantity);

    public CartDTO clearAll(Long userId);
    public CartDTO removeItem(Long userId, Long cartItemId);

}
