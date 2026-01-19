package com.fung.fungry.ServiceIMPL;

import com.fung.fungry.Enums.UserRole;
import com.fung.fungry.Model.*;
import com.fung.fungry.ModelDTO.*;
import com.fung.fungry.Repository.MenuItemRepository;
import com.fung.fungry.Repository.RatingRepository;
import com.fung.fungry.Repository.RestaurantRepository;
import com.fung.fungry.Repository.UserRepository;
import com.fung.fungry.Service.RestaurantService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RestaurantServiceIMPL  implements RestaurantService {
    @Autowired
    private RestaurantRepository restaurantRepository;
    @Autowired
   private MenuItemRepository menuItemRepository;
    @Autowired
    private UserRepository userRepository;
    private RestaurantDTO mapToRestDTO(Restaurant restaurant)
    {
        RestaurantDTO restaurantdto=new RestaurantDTO();
        restaurantdto.setRestaurantId(restaurant.getRestaurantId());
        restaurantdto.setName(restaurant.getName());
        restaurantdto.setRating(restaurant.getRating());
        return restaurantdto;
    }
    private RestaurantAddress mapToRestAddress(RestaurantAddressDTO restaurantAddressDTO)
    {
        RestaurantAddress restaurantAddress=new RestaurantAddress();
        restaurantAddress.setArea(restaurantAddressDTO.getArea());
        restaurantAddress.setCity(restaurantAddressDTO.getCity());
        restaurantAddress.setState(restaurantAddressDTO.getState());
        restaurantAddress.setZipcode(restaurantAddressDTO.getZipcode());
        restaurantAddress.setStreet(restaurantAddressDTO.getStreet());
        return restaurantAddress;
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

    @Transactional
    @Override
    public RestaurantDTO addRestaurant(RestaurantCreateDTO restaurantCreateDTO, Long userId) {
        User user=userRepository.findById(userId).orElseThrow(()-> new RuntimeException("USER NOT FOUND"));
        if (user.getRole()!= UserRole.ADMIN)
        {
            throw new RuntimeException("ONLY ADMIN CAN ADD RESTAURANT");


        }
        Restaurant restaurant=new Restaurant();
        restaurant.setName(restaurantCreateDTO.getName());
        RestaurantAddress restaurantAddress = mapToRestAddress(restaurantCreateDTO.getRestaurantAddressDTO());
        restaurant.setAddress(restaurantAddress);

        Restaurant savedRestaurant =restaurantRepository.save(restaurant);

        RestaurantDTO restaurantDTO =mapToRestDTO(savedRestaurant);
        return restaurantDTO;
    }

    @Transactional
    @Override
    public String deleteRestaurant(Long restaurantId, Long userId) {
        User user =userRepository.findById(userId).orElseThrow(()->new RuntimeException("NO USER FOUND"));
        if(user.getRole()!=UserRole.ADMIN)
        {
            throw  new RuntimeException("YOU ARE NOT AN ADMIN");
        }
        Optional<Restaurant> restaurant=restaurantRepository.findById(restaurantId);
        if(restaurant.isPresent())
        {

            restaurantRepository.deleteById(userId);

        }
        return "Restaurant Cannot Be Deleted";
    }

    @Override
    @Transactional
    public RestaurantDTO updateRestaurant(RestaurantUpdateDTO restaurantDTO, Long userId) {
        User user =userRepository.findById(userId).orElseThrow(()->new RuntimeException("NO USER FOUND"));
        if(user.getRole()!=UserRole.ADMIN)
        {
            throw  new RuntimeException("YOU ARE NOT AN ADMIN");
        }
        Optional<Restaurant> restaurant=restaurantRepository.findById(restaurantDTO.getId()).get();
        if (!restaurant.isPresent())
        {
            throw new RuntimeException("No such restaurant available");
        }
        RestaurantAddressDTO addressDTO=restaurantDTO.getAddressDTO();
        RestaurantAddress address =mapToRestAddress(addressDTO);
        restaurant.get().setAddress(address);
        restaurant.get().setName(restaurantDTO.getName());
       Restaurant savedRest = restaurantRepository.save(restaurant.get());

        return mapToRestDTO(savedRest);
    }


    @Autowired
    RatingRepository ratingRepository;
    @Transactional
    @Override
    public RestaurantDTO rateRestaurant(Long userId, Long restaurantId, Integer rating) {
        if(rating>5||rating<1)
        {
            throw new RuntimeException("Rating must be between 1 and 5");
        }
        Optional<Restaurant> restaurant=restaurantRepository.findById(restaurantId);
        if (!restaurant.isPresent())
        {
            throw new RuntimeException("NO SUCH RESTAURANT");
        }
        else
        {
            Optional<Rating> ratings =ratingRepository.findByRestaurant(restaurant.get()).orElseGet(()->
            {
                Rating r =new Rating();
                r.setRestaurant(restaurant.get());
                r.setRatingSum(0L);
                r.setRatingCount(0L);
                r.setRatingAverage(0.0);
                return r;
            });
            ratings.get().setRatingSum(ratings.get().getRatingSum()+rating);
            ratings.get().setRatingCount(ratings.get().getRatingCount()+1);
           double avg=(double) ratings.get().getRatingSum()/ratings.get().getRatingCount();
           ratingRepository.save(ratings.get());
           return mapToRestDTO(restaurant.get());
        }


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
