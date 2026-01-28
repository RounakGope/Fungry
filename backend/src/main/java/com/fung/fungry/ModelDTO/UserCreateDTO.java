package com.fung.fungry.ModelDTO;

import com.fung.fungry.Enums.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@ToString(exclude = "password")
@NoArgsConstructor
public class UserCreateDTO {
    @NotBlank(message = "UserName cannot be null")
    private String userName;
    @NotNull(message = "UserRole is required")
   private UserRole userRole;
    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8,message = "Minimum size of 8 is required")
    private String password;
}
