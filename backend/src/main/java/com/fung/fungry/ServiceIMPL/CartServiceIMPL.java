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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CartServiceIMPL implements CartService {
   private static final Logger log= LoggerFactory.getLogger(CartServiceIMPL.class);

    private final
    UserRepository userRepository;
    private final
    CartRepository cartRepository;
    private final
    RestaurantRepository restaurantRepository;
    private final
    MenuItemRepository menuItemRepository;
    private final
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
        User user=userRepository.findById(userId).orElseThrow(()->{
            log.error("User not found for {}",userId); return new RuntimeException("User Not Found");
        });
        Cart cart=cartRepository.findById(user.getCart().getCartId()).orElseThrow(()->new RuntimeException("No Cart Found"));
        CartDTO cartDTO=cartToDTO(cart);
        return cartDTO;
    }

    @Override
    @Transactional
    public void addToCart(Long userId, Long menuItemId) {
        log.info("started add to cart for user id ={} , menuitemid={}",userId,menuItemId);



        User user=userRepository.findById(userId).orElseThrow(()->{
            log.error("User not found for {}",userId); return new RuntimeException("User Not Found");
        });
        Cart cart = cartRepository.findByUser_UserId(userId).orElse(null);
        if(cart==null)
        {
            log.info("cart is null");
            cart=new Cart();
            cart.setUser(user);
            user.setCart(cart);
        }
        List<CartItem> cartItems=cart.getCartItems();
        if(cartItems==null)
        {
            log.info("cart items is null");
            cart.setCartItems(new ArrayList<>());
        }
        MenuItem menuItem=menuItemRepository.findById(menuItemId).orElseThrow(()->new RuntimeException("No such Menu item"));
        if (!menuItem.getIsAvailable())
        {
            throw new RuntimeException("The Item is Unavailable right now");
        }

        // NEW: block adding items from a different restaurant while cart is non-empty
        if (!cart.getCartItems().isEmpty()
                && cart.getRestaurant() != null
                && !cart.getRestaurant().getRestaurantId().equals(menuItem.getRestaurant().getRestaurantId())) {
            log.warn("cart already has items from restaurant={}, cannot add item from restaurant={}",
                    cart.getRestaurant().getRestaurantId(), menuItem.getRestaurant().getRestaurantId());
            throw new RuntimeException("Your cart has items from " + cart.getRestaurant().getName()
                    + ". Clear your cart to order from a different restaurant.");
        }

        Optional<CartItem> existingCartItems=cart.getCartItems().stream()
                .filter(cartItem -> cartItem.getMenuItem().getMenuItemId().equals(menuItemId))
                .findFirst();

        if(existingCartItems.isPresent())
        {
            CartItem  cartItem=existingCartItems.get();
            cartItem.setQuantity(cartItem.getQuantity()+1);
            log.info("increased cart item quantity by 1");
        }
        else {
            log.info("creating a new cartitem");
            CartItem cartItem=new CartItem();
            cartItem.setCart(cart);
            cartItem.setQuantity(1);
            cartItem.setMenuItem(menuItem);
            cartItems.add(cartItem);
        }
        cart.setRestaurant(menuItem.getRestaurant());
        long total = cart.getCartItems()
                .stream()
                .mapToLong(item ->
                        item.getMenuItem().getPrice() * item.getQuantity())
                .sum();

        cart.setTotalAmt(total);
        cart.setRestaurant(menuItem.getRestaurant());
        cartRepository.save(cart);
    }
    @Override
    @Transactional
    public CartDTO updateItemQuantityByOne(Long cartItemId, Long userId) {


        Cart cart=cartRepository.findByUser_UserId(userId).orElseThrow(()->new RuntimeException("Cart Not Found"));

        CartItem cartItem =cart.getCartItems().stream().filter(
                cartItemLocal -> cartItemLocal.getCartItemId().equals(cartItemId))
                .findFirst()
                .orElseThrow(()->new RuntimeException("No such CartItem"));


        if(cartItem.getQuantity()+1>cartItem.getMenuItem().getAvailableQuantity()||!cartItem.getMenuItem().getIsAvailable()||
                cartItem.getMenuItem().getAvailableQuantity()==0)
        {
            log.info("either the quantity is exceeding storage or out of stock for cartitem={} ",cartItemId);
            throw new RuntimeException("You cannot add items");

        }
        cartItem.setQuantity(cartItem.getQuantity()+1);
        log.info("updated the quantity by one for cart item={}",cartItemId);
        long newTotal = cart.getCartItems().stream()
                .mapToLong(item -> item.getMenuItem().getPrice() * item.getQuantity())
                .sum();
        cart.setTotalAmt(newTotal);
        cartRepository.save(cart);
        log.info("saved the cart ");
        return cartToDTO(cart);
    }
    @Transactional
    @Override
    public CartDTO clearAll(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("No Such User"));
        Cart cart = cartRepository.findById(user.getCart().getCartId()).orElseThrow(() -> new RuntimeException("No such cart available"));
        cart.getCartItems().clear();
        cart.setTotalAmt(0L);   // ← added

        cartRepository.save(cart);
        return cartToDTO(cart);
    }

    @Transactional
    @Override
    public CartDTO removeItem(Long userId, Long cartItemId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("No such user found"));
        Cart cart = user.getCart();
        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(() -> new RuntimeException("No such cart Item"));
        if (!user.getCart().getCartId().equals(cartItem.getCart().getCartId())) {
            log.warn("user cart mismatch for user_id={}", userId);
            throw new RuntimeException("User Cart Mismatch");
        }
        cart.getCartItems().remove(cartItem);
        log.info("removed the cartitems={}", cartItemId);

        long newTotal = cart.getCartItems().stream()
                .mapToLong(item -> item.getMenuItem().getPrice() * item.getQuantity())
                .sum();
        cart.setTotalAmt(newTotal);

        cartRepository.save(cart);
        return cartToDTO(cart);
    }
}
