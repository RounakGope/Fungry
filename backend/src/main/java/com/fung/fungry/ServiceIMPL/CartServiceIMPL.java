package com.fung.fungry.ServiceIMPL;

import com.fung.fungry.Model.Cart;
import com.fung.fungry.Model.CartItem;
import com.fung.fungry.Model.MenuItem;
import com.fung.fungry.Model.User;
import com.fung.fungry.ModelDTO.CartDTO;
import com.fung.fungry.ModelDTO.CartItemDTO;
import com.fung.fungry.Repository.*;
import com.fung.fungry.Service.CartService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;
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
    @Autowired
    MenuItemRepository menuItemRepository;
    @Autowired
    CartItemRepository cartItemRepository;
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
    @Transactional
    public void addToCart(Long userId, Long menuItemId) {

        User user=userRepository.findById(userId).orElseThrow(()->new RuntimeException("User Not Found"));
        Cart cart = cartRepository.findByUser_UserId(userId).orElse(null);
        if(cart==null)
        {
            cart=new Cart();
            cart.setUser(user);
            user.setCart(cart);
        }
        List<CartItem> cartItems=cart.getCartItems();
        if(cartItems==null)
        {
            cart.setCartItems(new ArrayList<>());
        }
        MenuItem menuItem=menuItemRepository.findById(menuItemId).orElseThrow(()->new RuntimeException("No such Menu item"));
        Optional<CartItem> existingCartItems=cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getMenuItem().getMenuItemId().equals(menuItemId))
                .findFirst();
        if(existingCartItems.isPresent())
        {
           CartItem  cartItem=existingCartItems.get();
             cartItem.setQuantity(cartItem.getQuantity()+1);
        }
        else {
            CartItem cartItem=new CartItem();
            cartItem.setCart(cart);
            cartItem.setQuantity(1);
            cartItem.setMenuItem(menuItem);
            cartItems.add(cartItem);
        }
        cartRepository.save(cart);
    }
    @Override
    @Transactional
    public CartDTO updateItemQuantityByOne(Long cartItemId, Long userId) {
        User user=userRepository.findById(userId).orElseThrow(()-> new RuntimeException("User Not Found"));
        Cart cart=cartRepository.findByUser_UserId(userId).orElseThrow(()->new RuntimeException("Cart Not Found"));

        CartItem cartItem =cart.getCartItems().stream().filter(
                cartItemLocal -> cartItemLocal.getCartItemId().equals(cartItemId))
                .findFirst()
                .orElseThrow(()->new RuntimeException("No such CartItem"));


        if(cartItem.getQuantity()+1>cartItem.getMenuItem().getAvailableQuantity()||!cartItem.getMenuItem().getIsAvailable()||
                cartItem.getMenuItem().getAvailableQuantity()==0)
            throw new RuntimeException("You cannot add items");

        cartItem.setQuantity(cartItem.getQuantity()+1);

        cartRepository.save(cart);
        return cartToDTO(cart);

    }

    @Override
    public CartDTO clearAll(Long userId) {
        User user=userRepository.findById(userId).orElseThrow(()->new RuntimeException("No Such User"));
        Cart cart=cartRepository.findById(user.getCart().getCartId()).orElseThrow(()->new RuntimeException("No such cart available"));
        cartRepository.delete(cart);
        Cart newCart= new Cart();
        cartRepository.save(newCart);
        return cartToDTO(newCart);

    }

    @Override
    public CartDTO removeItem(Long userId, Long cartItemId) {
        User user =userRepository.findById(userId).orElseThrow(()->new RuntimeException("No such user found"));
        Cart cart=user.getCart();
        CartItem cartItem=cartItemRepository.findById(cartItemId).orElseThrow(()->new RuntimeException("No such cart Item"));
        List<CartItem> cartItemList=cart.getCartItems();
        for(CartItem item : cartItemList)
        {
            if(item.getMenuItem().getName().equals(cartItem.getMenuItem().getName()))
            {
                cartItemList.remove(item);
            }
        }
        cartRepository.save(cart);
        return  cartToDTO(cart);
    }
}
