package com.fung.fungry.Service;

import com.fung.fungry.Model.Restaurant;
import com.fung.fungry.ModelDTO.MenuItemDTO;
import com.fung.fungry.ModelDTO.RestaurantDTO;

import java.util.List;

public interface RestaurantService {

    public List<RestaurantDTO> getAllRestaurantByRatings(int page,int size,String direction,String sortBy);//pagination is neded
    public List<MenuItemDTO> getMenuItem(Long restaurantId);
    public RestaurantDTO addRestaurant(RestaurantDTO restaurant,Long userId);//only for admin
    public String deleteRestaurant(Long restaurantId ,Long userId);//only for admin
    public RestaurantDTO updateRestaurant(RestaurantDTO restaurantDTO,Long userId);
    public RestaurantDTO rateRestaurant(Long userId,Long restaurantId,Integer rating);
    public void addItemInMenu(MenuItemDTO itemDTO,Long restaurantId,Long userId);
    public void deleteItemInMenu(Long menuItemId,Long restaurantId,Long userId);
    public MenuItemDTO updateItemInMenu(Long menuItemId,Long restaurantId,Long userId);

}
