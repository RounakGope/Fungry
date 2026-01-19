package com.fung.fungry.Service;

import com.fung.fungry.Model.Restaurant;
import com.fung.fungry.ModelDTO.MenuItemDTO;
import com.fung.fungry.ModelDTO.RestaurantCreateDTO;
import com.fung.fungry.ModelDTO.RestaurantDTO;
import com.fung.fungry.ModelDTO.RestaurantUpdateDTO;

import java.util.List;

public interface RestaurantService {

    public List<RestaurantDTO> getAllRestaurantBy(int page,int size,String direction,String sortBy);//pagination is nede
    List<MenuItemDTO> getMenuItem(Long restaurantId, String sortBy, String direction);
    public RestaurantDTO addRestaurant(RestaurantCreateDTO restaurant, Long userId);//only for admin
    public String deleteRestaurant(Long restaurantId ,Long userId);//only for admin
    public RestaurantDTO updateRestaurant(RestaurantUpdateDTO restaurantDTO, Long userId);
    public RestaurantDTO rateRestaurant(Long userId,Long restaurantId,Integer rating);
    public void addItemInMenu(MenuItemDTO itemDTO,Long restaurantId,Long userId);
    public void deleteItemInMenu(Long menuItemId,Long restaurantId,Long userId);
    public MenuItemDTO updateItemInMenu(Long menuItemId,Long userId,MenuItemDTO menuItemDTO);

}
