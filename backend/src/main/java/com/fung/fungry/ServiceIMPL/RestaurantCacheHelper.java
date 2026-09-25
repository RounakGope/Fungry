package com.fung.fungry.ServiceIMPL;

import com.fung.fungry.Model.Restaurant;
import com.fung.fungry.Model.RestaurantAddress;
import com.fung.fungry.ModelDTO.RestaurantAddressDTO;
import com.fung.fungry.ModelDTO.RestaurantDTO;
import com.fung.fungry.ModelDTO.RestaurantListResponse;
import com.fung.fungry.Repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RestaurantCacheHelper {
    private final RestaurantRepository restaurantRepository;

    @Cacheable(value = "restaurants", key = "'all'")
    public RestaurantListResponse getAllRestaurantsRaw() {
        List<RestaurantDTO> list = restaurantRepository.findAll()
                .stream().map(this::mapToRestDTO).toList();
        return new RestaurantListResponse(list);
    }
    private RestaurantDTO mapToRestDTO(Restaurant restaurant)
    {
        RestaurantDTO restaurantdto=new RestaurantDTO();
        restaurantdto.setRestaurantId(restaurant.getRestaurantId());
        restaurantdto.setCuisine(restaurant.getCuisine());
        restaurantdto.setDescription(restaurant.getDescription());
        restaurantdto.setName(restaurant.getName());
        restaurantdto.setRating(restaurant.getRating() != null ? restaurant.getRating().getRatingAverage() : 0.0);
        restaurantdto.setRestaurantAddressDTO(mapToRestAddressDTO(restaurant.getAddress()));
        return restaurantdto;
    }
    private RestaurantAddressDTO mapToRestAddressDTO(RestaurantAddress address)
    {
        if (address == null) return null;
        RestaurantAddressDTO dto = new RestaurantAddressDTO();
        dto.setStreet(address.getStreet());
        dto.setArea(address.getArea());
        dto.setCity(address.getCity());
        dto.setState(address.getState());
        dto.setZipcode(address.getZipcode());
        return dto;
    }

}