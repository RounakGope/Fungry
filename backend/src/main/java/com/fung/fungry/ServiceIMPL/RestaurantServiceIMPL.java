package com.fung.fungry.ServiceIMPL;

import com.fung.fungry.Model.MenuItem;
import com.fung.fungry.Model.Restaurant;
import com.fung.fungry.ModelDTO.MenuItemDTO;
import com.fung.fungry.ModelDTO.RestaurantDTO;
import com.fung.fungry.Repository.MenuItemRepository;
import com.fung.fungry.Repository.RestaurantRepository;
import com.fung.fungry.Service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RestaurantServiceIMPL  implements RestaurantService {
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
   private MenuItemRepository menuItemRepository;
    private RestaurantDTO mapToRestDTO(Restaurant restaurant)
    {
        RestaurantDTO restaurantdto=new RestaurantDTO();
        restaurantdto.setRestaurantId(restaurant.getRestaurantId());
        restaurantdto.setName(restaurant.getName());
        restaurantdto.setRating(restaurant.getRating());
        return restaurantdto;
    }
    private MenuItemDTO mapToMenuDTO(MenuItem menuItem)
    {
        MenuItemDTO menuItemDTO=new MenuItemDTO();
        menuItemDTO.setMenuItemId(menuItemDTO.getMenuItemId());
        menuItemDTO.setPrice(menuItem.getPrice());
        menuItemDTO.setFoodType(menuItem.getType());
        menuItemDTO.setFoodName(menuItem.getName());
        menuItemDTO.setFoodCategory(menuItem.getCategory());
        menuItemDTO.setIsAvailable(menuItem.getIsAvailable());
        menuItemDTO.setAvailableQuantity(menuItem.getAvailableQuantity());

        return menuItemDTO;
    }

    @Override
    public List<RestaurantDTO> getAllRestaurantBy(int page, int size, String direction, String sortBy) {
        Sort sort =direction.equalsIgnoreCase("desc")?Sort.by(sortBy).descending():Sort.by(sortBy).ascending();
        Pageable pageable= PageRequest.of(page,size, sort);
        Page<Restaurant> restaurantPage=restaurantRepository.findAll(pageable);
        return restaurantPage
                .getContent()
                .stream()
                .map(this::mapToRestDTO)
                .toList();

    }

    @Override
    public List<MenuItemDTO> getMenuItem(Long restaurantId, String sortBy, String direction) {

        Sort sort=direction.equalsIgnoreCase("desc")?Sort.by(sortBy).descending():Sort.by(sortBy).ascending();
        List< MenuItem> menuItems = menuItemRepository.findByRestaurant_RestaurantId(restaurantId,sort);
        return menuItems.stream()
                .map(this::mapToMenuDTO)
                .toList();
    }

    @Override
    public RestaurantDTO addRestaurant(RestaurantDTO restaurant, Long userId) {
        return null;
    }

    @Override
    public String deleteRestaurant(Long restaurantId, Long userId) {
        return "";
    }

    @Override
    public RestaurantDTO updateRestaurant(RestaurantDTO restaurantDTO, Long userId) {
        return null;
    }

    @Override
    public RestaurantDTO rateRestaurant(Long userId, Long restaurantId, Integer rating) {
        return null;
    }

    @Override
    public void addItemInMenu(MenuItemDTO itemDTO, Long restaurantId, Long userId) {

    }

    @Override
    public void deleteItemInMenu(Long menuItemId, Long restaurantId, Long userId) {

    }

    @Override
    public MenuItemDTO updateItemInMenu(Long menuItemId, Long restaurantId, Long userId) {
        return null;
    }
}
