package com.fung.fungry.ModelDTO;

import jakarta.persistence.Column;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RestaurantAddressDTO {
    private String street;
    private String area;
    private String city;
    private String state;
    private Integer zipcode;

}
