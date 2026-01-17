package com.fung.fungry.ModelDTO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class RestaurantCreateDTO {
    private String name;
    private RestaurantAddressDTO restaurantAddressDTO;


}
