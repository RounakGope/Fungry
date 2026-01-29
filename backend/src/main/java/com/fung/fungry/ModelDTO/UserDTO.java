package com.fung.fungry.ModelDTO;

import com.fung.fungry.Enums.UserRole;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserDTO {
    private Long userId;
    private String userEmail;
    private String userName;
    private UserRole userRole;
    private String phoneNumber;

}
