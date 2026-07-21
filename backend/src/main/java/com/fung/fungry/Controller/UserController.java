package com.fung.fungry.Controller;

import com.fung.fungry.Enums.UserRole;
import com.fung.fungry.ModelDTO.*;
import com.fung.fungry.ServiceIMPL.UserServiceIMPL;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api-v1.0/users")
public class UserController {
    private final UserServiceIMPL userServiceIMPL;

    @PostMapping
    public ResponseEntity<UserDTO> addUser(@RequestBody UserCreateDTO userCreateDTO)
    {
        com.fung.fungry.ModelDTO.UserDTO user =userServiceIMPL.addUser(userCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
    @GetMapping("/all")
    public ResponseEntity<List<UserDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "asc") String dir,
            @RequestParam(defaultValue = "userId") String sort,
            @RequestParam(required = false) UserRole role
    ) {
        List<UserDTO> users = userServiceIMPL.getAllUsers(page, size, dir, sort, role);
        return ResponseEntity.ok(users);
    }
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> fetchUser(@PathVariable Long id)
    {
        UserDTO userDTO=userServiceIMPL.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(userDTO);
    }
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id,@RequestBody UserDTO userDTO)
    {
        UserDTO userDTO1=userServiceIMPL.updateUser(id, userDTO);
        return ResponseEntity.ok(userDTO1);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id)
    {
        userServiceIMPL.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/userRole/{id}")
    public ResponseEntity<UserRole> userRole(@PathVariable Long id)
    {
        UserRole userRole=userServiceIMPL.getUserRole(id);
        return ResponseEntity.ok(userRole);
    }
    @PutMapping("/updatePhone/{id}")
    public ResponseEntity<UserDTO> updatePhone(@PathVariable Long id,@RequestBody PhoneDTO Number)
    {
        UserDTO userDTO=userServiceIMPL.updateUserPNo(id,Number.getNumber());
        return ResponseEntity.ok(userDTO);
    }
    @PutMapping("/updatePassword/{id}")
    public ResponseEntity<UserDTO> updatePass(@PathVariable Long id, @RequestBody PasswordUpdateDTO passwordUpdateDTO)
    {
        UserDTO userDTO=userServiceIMPL.updatePassword(id,
                passwordUpdateDTO.getOldPassword(), passwordUpdateDTO.getNewPassword());
        return ResponseEntity.ok(userDTO);
    }
    @GetMapping("/orderHistory/{id}")
    public ResponseEntity<List<OrderHistoryDTO>> orderHistory(@PathVariable Long id
    , @RequestParam (defaultValue = "0")Integer page,@RequestParam
                                                                          (defaultValue = "15")Integer size, @RequestParam (defaultValue = "createdAt")String sortBy,
                                                              @RequestParam (defaultValue = "descending")String direction)
    {
        List<OrderHistoryDTO> dtoList=userServiceIMPL.viewOrderHistory
                (id,page,size,sortBy,direction);
        return ResponseEntity.ok(dtoList);
    }






    
}
