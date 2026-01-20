package com.fung.fungry.ServiceIMPL;

import com.fung.fungry.Model.Cart;
import com.fung.fungry.Model.CartItem;
import com.fung.fungry.Model.User;
import com.fung.fungry.ModelDTO.CartDTO;
import com.fung.fungry.ModelDTO.CartItemDTO;
import com.fung.fungry.Repository.CartRepository;
import com.fung.fungry.Repository.RestaurantRepository;
import com.fung.fungry.Repository.UserRepository;
import com.fung.fungry.Service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CartServiceIMPL implements CartService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    CartRepository cartRepository;
    @Autowired
    RestaurantRepository restaurantRepository;
    public List<CartItemDTO> cartItemDTO(List<CartItem> cartItems)
    {
        List<CartItemDTO>cartItemDTOS=new ArrayList<>();

        for (CartItem cartItem:cartItems)
        {
            CartItemDTO cartItemDTO=new CartItemDTO();
            cartItemDTO.setCartItemId(cartItem.getCartItemId());
            cartItemDTO.setItemName(cartItem.getMenuItem().getName());
            cartItemDTO.setPrice(cartItem.getMenuItem().getPrice());
            cartItemDTO.setQuantity(cartItem.getQuantity());
            cartItemDTOS.add(cartItemDTO);
        }
        return  cartItemDTOS;

    }
    public CartDTO cartToDTO(Cart cart)
    {
        CartDTO cartDTO=new CartDTO();
        cartDTO.setCartId(cart.getCartId());
        if (cart.getRestaurant()!=null)
        cartDTO.setRestaurantName(cart.getRestaurant().getName());
        cartDTO.setTotalAmt(cart.getTotalAmt());
        cartDTO.setCartItemDTOS(cartItemDTO(cart.getCartItems()));
        return cartDTO;
    }


    @Override
    public CartDTO viewCart(Long userId) {
        User user=userRepository.findById(userId).orElseThrow(()->new RuntimeException("User Not Found"));
        Cart cart=cartRepository.findById(user.getCart().getCartId()).orElseThrow(()->new RuntimeException("No Cart Found"));
        CartDTO cartDTO=cartToDTO(cart);
        return cartDTO;
    }

    @Override
    public CartDTO addToCart(Long userId, Long menuItemId) {
        return null;
    }

    @Override
    public CartDTO updateItemQuantity(Long cartItemId, Long userId, Integer quantity) {
        return null;
    }

    @Override
    public CartDTO clearAll(Long userId) {
        return null;
    }

    @Override
    public CartDTO removeItem(Long userId, Long cartItemId) {
        return null;
    }
}
