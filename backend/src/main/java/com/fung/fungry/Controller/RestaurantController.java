package com.fung.fungry.Controller;

import com.fung.fungry.ModelDTO.MenuItemDTO;
import com.fung.fungry.ModelDTO.RestaurantCreateDTO;
import com.fung.fungry.ModelDTO.RestaurantDTO;
import com.fung.fungry.ModelDTO.RestaurantUpdateDTO;
import com.fung.fungry.ServiceIMPL.RestaurantServiceIMPL;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api-v1.0/restaurant")
@RequiredArgsConstructor
public class RestaurantController {
    private final RestaurantServiceIMPL restaurantServiceIMPL;

    @GetMapping("/menuItems/{id}")
    public ResponseEntity<List<MenuItemDTO>> getMenuItems(@PathVariable Long id
    , @RequestParam String sortBy,@RequestParam String direction)
    {
        List<MenuItemDTO> menuItemDTOS=restaurantServiceIMPL
                .getMenuItem(id, sortBy, direction);
        return ResponseEntity.ok(menuItemDTOS);
    }
    @PostMapping("/{userId}")
    public ResponseEntity<RestaurantDTO> addRest(@PathVariable Long userId ,@RequestBody RestaurantCreateDTO restaurantCreateDTO)
    {
        RestaurantDTO restaurantDTO=restaurantServiceIMPL.addRestaurant(restaurantCreateDTO,userId);
        return ResponseEntity.ok(restaurantDTO);
    }
    @DeleteMapping("/{restId}/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long restId,@PathVariable Long userId)
    {
        restaurantServiceIMPL.deleteRestaurant(restId,userId);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{userId}")
    public ResponseEntity<RestaurantDTO> update(@PathVariable Long userId,@RequestBody RestaurantUpdateDTO restaurantDTO) {
        RestaurantDTO restaurantDTO1 = restaurantServiceIMPL.updateRestaurant(
                restaurantDTO, userId);
        return ResponseEntity.ok(restaurantDTO1);
    }
    @PostMapping("/rate/{userId}/{restId}/{rate}")
    public ResponseEntity<RestaurantDTO> rate(@PathVariable Long userId,@PathVariable Long restId,@PathVariable Integer rate)
    {
        RestaurantDTO restaurantDTO=restaurantServiceIMPL.rateRestaurant(userId, restId, rate);
        return ResponseEntity.ok(restaurantDTO);
    }



}
