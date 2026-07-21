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
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RestaurantServiceIMPL  implements RestaurantService {
    private final Logger log= LoggerFactory.getLogger(RestaurantServiceIMPL.class);

    private final RestaurantRepository restaurantRepository;

    private final MenuItemRepository menuItemRepository;
    private final UserRepository userRepository;
    private void assertCanManage(Restaurant restaurant, User user) {
        boolean isOwner = restaurant.getOwner() != null
                && restaurant.getOwner().getUserId().equals(user.getUserId());
        if (user.getRole() != UserRole.ADMIN && !isOwner) {
            throw new RuntimeException("Not authorized to manage this restaurant");
        }
    }
    public RestaurantDTO getByOwner(Long userId) {
        log.info("started getByOwner for userId={}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id={}", userId);
                    return new RuntimeException("USER NOT FOUND");
                });

        Restaurant restaurant = restaurantRepository.findByOwner_UserId(userId)
                .orElseThrow(() -> {
                    log.warn("no restaurant found for owner userId={}", userId);
                    return new RuntimeException("No restaurant assigned to this user");
                });

        return mapToRestDTO(restaurant);
    }
    private RestaurantDTO mapToRestDTO(Restaurant restaurant)
    {
        RestaurantDTO restaurantdto=new RestaurantDTO();
        restaurantdto.setRestaurantId(restaurant.getRestaurantId());
        restaurantdto.setCuisine(restaurant.getCuisine());
        restaurantdto.setDescription(restaurant.getDescription());
        restaurantdto.setName(restaurant.getName());
        restaurantdto.setRating(restaurant.getRating().getRatingAverage());
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
        menuItemDTO.setMenuItemId(menuItem.getMenuItemId());
        menuItemDTO.setPrice(menuItem.getPrice());
        menuItemDTO.setFoodType(menuItem.getType());
        menuItemDTO.setFoodName(menuItem.getName());
        menuItemDTO.setFoodCategory(menuItem.getCategory());
        menuItemDTO.setIsAvailable(menuItem.getIsAvailable());
        menuItemDTO.setAvailableQuantity(menuItem.getAvailableQuantity());

        return menuItemDTO;
    }
    private MenuItem mapToMenuItem(MenuItemDTO menuItemDTO,Restaurant restaurant)
    {
        MenuItem menuItem=new MenuItem();
        menuItem.setMenuItemId(menuItemDTO.getMenuItemId());
        menuItem.setRestaurant(restaurant);
        menuItem.setPrice(menuItemDTO.getPrice());
        menuItem.setType(menuItemDTO.getFoodType());
        menuItem.setName(menuItemDTO.getFoodName());
        menuItem.setCategory(menuItemDTO.getFoodCategory());
        menuItem.setIsAvailable(menuItemDTO.getIsAvailable());
        menuItem.setAvailableQuantity(menuItemDTO.getAvailableQuantity());

        return menuItem;
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
        log.info("started get menu items for restId={}",restaurantId);
        Sort sort=direction.equalsIgnoreCase("desc")?Sort.by(sortBy).descending():Sort.by(sortBy).ascending();
        List< MenuItem> menuItems = menuItemRepository.findByRestaurant_RestaurantId(restaurantId,sort);

        return menuItems.stream()
                .map(this::mapToMenuDTO)
                .toList();
    }

    @Transactional
    @Override
    public RestaurantDTO addRestaurant(RestaurantCreateDTO restaurantCreateDTO, Long adminId,Long userId) {
        log.info("started adding restaurant for userId={}",userId);
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> {
                    log.error("User not found with id={}", userId);
                    return new RuntimeException("ADMIN NOT FOUND");
                });
        if (admin.getRole()!= UserRole.ADMIN)
        {

            log.warn("cannot add restaurant as the user is not admin user={}",userId);
            throw new RuntimeException("ONLY ADMIN CAN ADD RESTAURANT");
        }
        Restaurant restaurant=new Restaurant();
        restaurant.setName(restaurantCreateDTO.getName());
        restaurant.setOwner(userRepository.findById(userId).orElseThrow());
        restaurant.setCuisine(restaurantCreateDTO.getCuisine());
        restaurant.setDescription(restaurantCreateDTO.getDescription());
        RestaurantAddress restaurantAddress = mapToRestAddress(restaurantCreateDTO.getRestaurantAddressDTO());
        restaurant.setAddress(restaurantAddress);
        Rating rating = new Rating();
        rating.setRatingSum(0L);
        rating.setRatingCount(0L);
        rating.setRatingAverage(0.0);
        rating.setRestaurant(restaurant);
        restaurant.setRating(rating);

        Restaurant savedRestaurant =restaurantRepository.save(restaurant);
        log.info("restaurant is saved for user ={}",userId);

        RestaurantDTO restaurantDTO =mapToRestDTO(savedRestaurant);
        return restaurantDTO;
    }

    @Override
    public RestaurantDTO viewRestaurant(Long restaurantId) {
        Optional<Restaurant> restaurant=restaurantRepository.findById(restaurantId);
        if (!restaurant.isPresent())
        {
            log.warn("no such restaurant available restId={}",restaurantId);
            throw new RuntimeException("No such restaurant available");
        }
        RestaurantDTO restaurantDTO=mapToRestDTO(restaurant.get());
        return restaurantDTO;

    }

    @Transactional
    @Override
    public void deleteRestaurant(Long restaurantId, Long userId) {
        log.info("started deleting restaurant for userId={}",userId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id={}", userId);
                    return new RuntimeException("USER NOT FOUND");
                });
        if(user.getRole()!=UserRole.ADMIN)
        {

            log.warn("cannot delete restaurant as the user is not admin user={}",userId);
            throw  new RuntimeException("YOU ARE NOT AN ADMIN");
        }
        Optional<Restaurant> restaurant=restaurantRepository.findById(restaurantId);
        if(restaurant.isPresent())
        {
            log.info("deleted restaurant of restId={},for user ={}",restaurantId,userId);
            restaurantRepository.deleteById(restaurantId);
        }
        else
        {
            log.warn("no such restaurant present with restarant id={}",restaurantId);

        }
    }

    @Override
    @Transactional
    public RestaurantDTO updateRestaurant(RestaurantUpdateDTO restaurantDTO, Long userId,Long restId) {
        log.info("started updating restaurant for user={}",userId);User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id={}", userId);
                    return new RuntimeException("USER NOT FOUND");
                });

        Optional<Restaurant> restaurant=restaurantRepository.findById(restId);
        if (!restaurant.isPresent())
        {
            log.warn("no such restaurant available restId={}",restId);
            throw new RuntimeException("No such restaurant available");
        }
        assertCanManage(restaurant.get(),user);

        RestaurantAddress address =mapToRestAddress(restaurantDTO.getAddressDTO());
        restaurant.get().setAddress(address);
        restaurant.get().setDescription(restaurantDTO.getDescription());
        restaurant.get().setCuisine(restaurantDTO.getCuisine());
        restaurant.get().setName(restaurantDTO.getName());
        restaurant.get().setName(restaurantDTO.getName());
       Restaurant savedRest = restaurantRepository.save(restaurant.get());
       log.info("saved restaurant with userId={}",userId);

        return mapToRestDTO(savedRest);
    }


    private final RatingRepository ratingRepository;
    @Transactional
    @Override
    public RestaurantDTO rateRestaurant(Long userId, Long restaurantId, Integer rating) {
        log.info("started rateRestaurant for user={} ,of restaurant ={}",userId,restaurantId);
        if(rating>5||rating<1)
        {
            log.warn("invalid rating ={}",rating);
            throw new RuntimeException("Rating must be between 1 and 5");
        }
        Optional<Restaurant> restaurant=restaurantRepository.findById(restaurantId);
        if (!restaurant.isPresent())
        {
            log.warn("no such restaurant with restId={}",restaurantId);
            throw new RuntimeException("NO SUCH RESTAURANT");
        }
        else
        {
            log.info("started giving ratings");
            Rating ratings =ratingRepository.findByRestaurant(restaurant.get()).orElseGet(()->
            {
                Rating r =new Rating();
                r.setRestaurant(restaurant.get());
                r.setRatingSum(0L);
                r.setRatingCount(0L);
                r.setRatingAverage(0.0);
                return r;
            });
            ratings.setRatingSum(ratings.getRatingSum()+rating);
            ratings.setRatingCount(ratings.getRatingCount()+1);
           double avg=(double) ratings.getRatingSum()/ratings.getRatingCount();
           ratings.setRatingAverage(avg);

           ratingRepository.save(ratings);
            log.info("saved the rating with average of {},of restId={}",avg,restaurantId);
           return mapToRestDTO(restaurant.get());
        }


    }
    private MenuItem mapToMenuItemCreate(MenuItemCreateDTO menuItemDTO, Restaurant restaurant)
    {
        MenuItem menuItem = new MenuItem();
        // menuItemId intentionally NOT set — DB generates it on save
        menuItem.setRestaurant(restaurant);
        menuItem.setPrice(menuItemDTO.getPrice());
        menuItem.setType(menuItemDTO.getFoodType());
        menuItem.setName(menuItemDTO.getFoodName());
        menuItem.setCategory(menuItemDTO.getFoodCategory());
        menuItem.setIsAvailable(menuItemDTO.getIsAvailable());
        menuItem.setAvailableQuantity(menuItemDTO.getAvailableQuantity());

        return menuItem;
    }

    @Override
    @Transactional
    public void addItemInMenu(MenuItemCreateDTO itemDTO, Long restaurantId, Long userId) {
        log.info("started add item in menu for rest id={}",restaurantId);User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id={}", userId);
                    return new RuntimeException("USER NOT FOUND");
                });
        if(!(user.getRole()==UserRole.ADMIN||user.getRole()==UserRole.RESTAURANT_OWNER))
        {
            log.warn("User cannot add item with userid={} for rest={}",userId,restaurantId);
            throw new RuntimeException( "Only an Admin or Owner can add Menu item");
        }
        Optional<Restaurant> restaurant=restaurantRepository.findById(restaurantId);
        //restaurant ownership to be added
        if(restaurant.isPresent())
        {
            Restaurant restaurant1=restaurant.get();
            List<MenuItem>menuItemList=restaurant1.getMenuItems();
            menuItemList.add(mapToMenuItemCreate(itemDTO,restaurant1));
            restaurantRepository.save(restaurant1);
            log.info("successfully added item ={} in restarant ={}",restaurantId);
        }
        else {
            log.warn("no such restaurant present with restarant id={}",restaurantId);
            throw new RuntimeException("No such Restaurant Present");
        }
    }

    @Transactional
    @Override
    public void deleteItemInMenu(Long menuItemId, Long restaurantId, Long userId) {
        log
                .info("started deleting item ={} by user={}",menuItemId,userId);User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.warn("User not found with id={}", userId);
                    return new RuntimeException("USER NOT FOUND");
                });
        if(!(user.getRole()==UserRole.ADMIN||user.getRole()==UserRole.RESTAURANT_OWNER))
        {
            log.warn("cannot delete menu item with user ={}",userId);
            throw new RuntimeException( "Only an Admin or Owner can Delete Menu item");

        }
        Optional<MenuItem > menuItem=menuItemRepository.findById(menuItemId);
        if (menuItem.isPresent())
        {

            Optional<Restaurant> restaurant=restaurantRepository.findById(restaurantId);
            if (restaurant.isPresent())
            {
                if(menuItem.get().getRestaurant().getRestaurantId().equals(restaurant.get().getRestaurantId()))
                {
                    List<MenuItem> menuItems=restaurant.get().getMenuItems();
                    menuItems.remove(menuItem.get());
                    restaurantRepository.save(restaurant.get());
                    log.info("deleted menu item with id={}, for restid={}",menuItem.get().getMenuItemId(),restaurantId);
                }
                else {
                    log.warn("menu with id={} do not belong to rest with id={}",menuItem.get().getMenuItemId(),userId);
                    throw new RuntimeException("Menu item does not belong to restaurant");
                }

            }
            else {
                log.warn("no such restaurant with restid={}",restaurantId);
                throw new RuntimeException("No such restaurant");
            }
        }
        else {
            log.warn("no such menu item with id={}",menuItemId);
            throw new RuntimeException("Menu item not present");
        }
    }

    @Transactional
    @Override
    public MenuItemDTO updateItemInMenu(Long menuItemId, Long userId,MenuItemDTO menuItemDTO) {
        log.info("started updating item in menu with id={} by user={}",menuItemId,userId);User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("User not found with id={}", userId);
                    return new RuntimeException("USER NOT FOUND");
                });
        if(!(user.getRole()==UserRole.ADMIN||user.getRole()==UserRole.RESTAURANT_OWNER))
        {
            log.error("user has not access to update menu with userId={}",userId);
            throw new RuntimeException( "Only an Admin or Owner can Update Menu item");
        }
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> {
                    log.warn("Menu item not found with id={}", menuItemId);
                    return new RuntimeException("menuItem not available");
                });


                    menuItem.setType(menuItemDTO.getFoodType());
                    menuItem.setPrice(menuItemDTO.getPrice());
                    menuItem.setCategory(menuItemDTO.getFoodCategory());
                    menuItem.setName(menuItemDTO.getFoodName());
                    menuItem.setAvailableQuantity(menuItemDTO.getAvailableQuantity());
                    menuItem.setIsAvailable(menuItemDTO.getIsAvailable());
                    menuItemRepository.save(menuItem);
                    log.info("updated menu with itemid={},by userid={}",menuItem.getMenuItemId(),userId);
                    return mapToMenuDTO(menuItem);

        }

        }


