package com.fung.fungry.ModelDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AddressDTO {
    private Long addressId;
    private Integer zipcode;
    private String address;
    private String landmark;
    private Integer houseNumber;
    private String state;
    
}
