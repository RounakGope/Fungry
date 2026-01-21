package com.fung.fungry.Service;

import com.fung.fungry.ModelDTO.CartDTO;

public interface CartService {
//no create or delete methods
    public CartDTO viewCart( Long userId);

    public void addToCart(Long userId, Long menuItemId);

    public CartDTO updateItemQuantityByOne(Long cartItemId, Long userId);

    public CartDTO clearAll(Long userId);
    public CartDTO removeItem(Long userId, Long cartItemId);

}
