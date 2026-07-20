package com.fung.fungry.ModelDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class AddressDTO {
    private Long addressId;
    private Integer zipCode;
    private String address;
    private String landMark;
    private Integer houseNumber;
    private String state;
    
}
