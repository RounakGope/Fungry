package com.fung.fungry.ModelDTO;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class PasswordUpdateDTO {
    private String oldPassword;
    private String newPassword;
}
