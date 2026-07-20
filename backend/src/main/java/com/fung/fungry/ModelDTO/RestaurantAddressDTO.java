package com.fung.fungry.ModelDTO;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RestaurantAddressDTO {
    @NotBlank
    private String street;
    private String area;
    @NotBlank
    private String city;
    @NotBlank
    private String state;
    @NotNull
    @Min(100000)
    @Max(999999)
    private Integer zipcode;
}
