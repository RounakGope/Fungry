package com.fung.fungry.Controller;

import com.fung.fungry.Configuration.UserPrincipal;
import com.fung.fungry.Enums.UserRole;
import com.fung.fungry.Model.User;
import com.fung.fungry.ModelDTO.*;
import com.fung.fungry.ServiceIMPL.UserServiceIMPL;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api-v2.0/users")
public class UserController {
    private final UserServiceIMPL userServiceIMPL;

    @PostMapping
    public ResponseEntity<UserDTO> addUser(@RequestBody UserCreateDTO userCreateDTO)
    {
        com.fung.fungry.ModelDTO.UserDTO user =userServiceIMPL.addUser(userCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }


    //updated
    @GetMapping("/fetch")
    public ResponseEntity<UserDTO> fetchUser(@AuthenticationPrincipal UserPrincipal principal)
    {
        Long id=principal.getUser().getUserId();
        UserDTO userDTO=userServiceIMPL.getUserById(id);
        return ResponseEntity.status(HttpStatus.OK).body(userDTO);
    }
    @PutMapping("/update")
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO,@AuthenticationPrincipal UserPrincipal principal)
    {
        Long userId=principal.getUser().getUserId();
        UserDTO userDTO1=userServiceIMPL.updateUser(userId, userDTO);
        return ResponseEntity.ok(userDTO1);
    }
    @DeleteMapping("/delete")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserPrincipal principal)
    {
        Long id=principal.getUser().getUserId();
        userServiceIMPL.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/userRole")
    public ResponseEntity<UserRole> userRole(@AuthenticationPrincipal UserPrincipal principal)
    {
        Long id=principal.getUser().getUserId();
        UserRole userRole=userServiceIMPL.getUserRole(id);
        return ResponseEntity.ok(userRole);
    }
    @PutMapping("/updatePhone")
    public ResponseEntity<UserDTO> updatePhone(@RequestBody PhoneDTO Number,@AuthenticationPrincipal UserPrincipal principal)
    {
        Long id=principal.getUser().getUserId();
        UserDTO userDTO=userServiceIMPL.updateUserPNo(id,Number.getNumber());
        return ResponseEntity.ok(userDTO);
    }
    @PutMapping("/updatePassword")
    public ResponseEntity<UserDTO> updatePass( @RequestBody PasswordUpdateDTO passwordUpdateDTO,@AuthenticationPrincipal UserPrincipal principal)
    {

        Long id=principal.getUser().getUserId();
        UserDTO userDTO=userServiceIMPL.updatePassword(id,
                passwordUpdateDTO.getOldPassword(), passwordUpdateDTO.getNewPassword());
        return ResponseEntity.ok(userDTO);
    }
    @GetMapping("/orderHistory")
    public ResponseEntity<List<OrderHistoryDTO>> orderHistory(@RequestParam (defaultValue = "0")Integer page,@RequestParam
                                                                          (defaultValue = "15")Integer size, @RequestParam (defaultValue = "createdAt")String sortBy,
                                                              @RequestParam (defaultValue = "descending")String direction, @AuthenticationPrincipal UserPrincipal principal)
    {
        Long id=principal.getUser().getUserId();
        List<OrderHistoryDTO> dtoList=userServiceIMPL.viewOrderHistory
                (id,page,size,sortBy,direction);
        return ResponseEntity.ok(dtoList);
    }






    
}
