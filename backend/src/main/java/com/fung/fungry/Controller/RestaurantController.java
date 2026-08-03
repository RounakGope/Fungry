package com.fung.fungry.Controller;

import com.fung.fungry.Configuration.UserPrincipal;
import com.fung.fungry.ModelDTO.*;
import com.fung.fungry.ServiceIMPL.RestaurantServiceIMPL;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api-v2.0/restaurant")
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
    @GetMapping("/all")
    public ResponseEntity<List<RestaurantDTO>> getAllRestaurant(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String dir,
            @RequestParam(defaultValue = "restaurantId") String sort
    )
    {
        List<RestaurantDTO> all =
                restaurantServiceIMPL.getAllRestaurantBy(page,size,dir,sort);

            return ResponseEntity.ok(all);

    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{userId}")
    public ResponseEntity<RestaurantDTO> addRest(@PathVariable Long userId ,@Valid@RequestBody RestaurantCreateDTO restaurantCreateDTO, @AuthenticationPrincipal UserPrincipal userPrincipal)
    {
        Long adminId=userPrincipal.getUser().getUserId();
        RestaurantDTO restaurantDTO=restaurantServiceIMPL.addRestaurant(restaurantCreateDTO,adminId,userId);
        return ResponseEntity.ok(restaurantDTO);
    }
    @GetMapping("/owner")
    public ResponseEntity<RestaurantDTO> getByOwner(@AuthenticationPrincipal UserPrincipal principal)
    {
        Long userId
                =principal.getUser().getUserId();
       RestaurantDTO restaurantDTO= restaurantServiceIMPL.getByOwner(userId);
       return ResponseEntity.ok(restaurantDTO);

    }
    @GetMapping("/viewRestaurant/{restId}")
    public ResponseEntity<RestaurantDTO> viewRestaurant(@PathVariable Long restId)
    {
        RestaurantDTO restaurantDTO = restaurantServiceIMPL.viewRestaurant(restId);
        return ResponseEntity.ok(restaurantDTO);
    }
    @DeleteMapping("/{restId}")
    public ResponseEntity<Void> delete(@PathVariable Long restId,@AuthenticationPrincipal UserPrincipal principal)
    {
        Long adminId=principal.getUser().getUserId();
        restaurantServiceIMPL.deleteRestaurant(restId,adminId);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/{restId}")
    public ResponseEntity<RestaurantDTO> update(@PathVariable Long restId,@Valid @RequestBody RestaurantUpdateDTO restaurantDTO
    ,@AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long userId=userPrincipal.getUser().getUserId();
        RestaurantDTO restaurantDTO1 = restaurantServiceIMPL.updateRestaurant(
                restaurantDTO, userId,restId);
        return ResponseEntity.ok(restaurantDTO1);
    }
    @PostMapping("/rate/{restId}/{rate}")
    public ResponseEntity<RestaurantDTO> rate(@PathVariable Long restId,@PathVariable Integer rate
    ,@AuthenticationPrincipal UserPrincipal userPrincipal)
    {
        Long userId =userPrincipal.getUser().getUserId();

        RestaurantDTO restaurantDTO=restaurantServiceIMPL.rateRestaurant(userId, restId, rate);
        return ResponseEntity.ok(restaurantDTO);
    }
    @PostMapping("/addItem/{restId}")
    public ResponseEntity<Void> addItem(@PathVariable Long restId,@RequestBody MenuItemCreateDTO menuItemDTO,@AuthenticationPrincipal UserPrincipal principal)
    {
        Long userId=principal.getUser().getUserId();
        restaurantServiceIMPL.addItemInMenu(menuItemDTO,restId,userId);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/deleteItem/{restId}/{itemId}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long restId,  @PathVariable Long itemId,@AuthenticationPrincipal UserPrincipal principal)
    {
        Long userId=principal.getUser().getUserId();
        restaurantServiceIMPL.deleteItemInMenu(itemId,restId,userId);
        return ResponseEntity.noContent().build();
    }
    @PutMapping("/updateItem/{itemId}")
    public ResponseEntity<MenuItemDTO> updateItem(@PathVariable Long itemId,@RequestBody MenuItemDTO menuItemDTO,@AuthenticationPrincipal UserPrincipal principal)
    {
        Long userId=principal.getUser().getUserId();
        MenuItemDTO menuItemDTO1=restaurantServiceIMPL.updateItemInMenu(itemId,userId,menuItemDTO);
        return ResponseEntity.ok(menuItemDTO1);
    }





}
