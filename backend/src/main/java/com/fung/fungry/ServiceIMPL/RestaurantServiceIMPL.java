package com.fung.fungry.ServiceIMPL;

import com.fung.fungry.Model.Restaurant;
import com.fung.fungry.ModelDTO.MenuItemDTO;
import com.fung.fungry.ModelDTO.RestaurantDTO;
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
    RestaurantRepository restaurantRepository;
    private RestaurantDTO mapToDTO(Restaurant restaurant)
    {
        RestaurantDTO restaurantdto=new RestaurantDTO();
        restaurantdto.setRestaurantId(restaurant.getRestaurantId());
        restaurantdto.setName(restaurant.getName());
        restaurantdto.setRating(restaurant.getRating());
        return restaurantdto;
    }
    @Override
    public List<RestaurantDTO> getAllRestaurantBy(int page, int size, String direction, String sortBy) {
        Sort sort =direction.equalsIgnoreCase("desc")?Sort.by(sortBy).descending():Sort.by(sortBy).ascending();
        Pageable pageable= PageRequest.of(page,size, sort);
        Page<Restaurant> restaurantPage=restaurantRepository.findAll(pageable);
        return restaurantPage
                .getContent()
                .stream()
                .map(this::mapToDTO)
                .toList();

    }

    @Override
    public List<MenuItemDTO> getMenuItem(Long restaurantId) {
        return List.of();
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
