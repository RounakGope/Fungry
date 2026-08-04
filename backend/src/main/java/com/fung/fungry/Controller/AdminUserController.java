package com.fung.fungry.Controller;

import com.fung.fungry.Enums.UserRole;
import com.fung.fungry.ModelDTO.UserDTO;
import com.fung.fungry.ServiceIMPL.UserServiceIMPL;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api-v2.0/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {
    final UserServiceIMPL userServiceIMPL;
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

    // new: admin viewing/acting on a specific user by id
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO userDTO= userServiceIMPL.getUserById(id);
        return ResponseEntity.ok(userDTO);


    }
}
