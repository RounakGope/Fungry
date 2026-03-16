package com.fung.fungry.Controller;

import com.fung.fungry.ModelDTO.MenuItemDTO;
import com.fung.fungry.ModelDTO.RestaurantCreateDTO;
import com.fung.fungry.ModelDTO.RestaurantDTO;
import com.fung.fungry.ServiceIMPL.RestaurantServiceIMPL;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api-v1.0")
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


}
