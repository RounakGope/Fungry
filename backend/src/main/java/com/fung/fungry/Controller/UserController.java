package com.fung.fungry.Controller;

import com.fung.fungry.Enums.UserRole;
import com.fung.fungry.ModelDTO.*;
import com.fung.fungry.ServiceIMPL.UserServiceIMPL;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class UserController {
    UserServiceIMPL userServiceIMPL;

    @PostMapping("/addUser")
    public ResponseEntity<UserDTO> addUser(@RequestBody UserCreateDTO userCreateDTO)
    {
        com.fung.fungry.ModelDTO.UserDTO user =userServiceIMPL.addUser(userCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }
    @GetMapping("/fetchUser/{id}")
    public ResponseEntity<UserDTO> fetchUser(@PathVariable Long id)
    {
        UserDTO userDTO=userServiceIMPL.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(userDTO);
    }
    @PutMapping("/updateUser/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id,@RequestBody UserDTO userDTO)
    {
        UserDTO userDTO1=userServiceIMPL.updateUser(id, userDTO);
        return ResponseEntity.ok(userDTO1);
    }
    @DeleteMapping("/deletUser/{id}")
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
    public ResponseEntity<UserDTO> updatePhone(@PathVariable Long id,@RequestBody String Number)
    {
        UserDTO userDTO=userServiceIMPL.updateUserPNo(id,Number);
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
    , @RequestParam Integer page,@RequestParam Integer size, @RequestParam String sortBy,
                                                              @RequestParam String direction)
    {
        List<OrderHistoryDTO> dtoList=userServiceIMPL.viewOrderHistory
                (id,page,size,sortBy,direction);
        return ResponseEntity.ok(dtoList);
    }





    
}
