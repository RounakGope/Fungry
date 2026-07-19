package com.fung.fungry.ModelDTO;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String userEmail;
    private String password;
}