package com.fung.fungry.ModelDTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class AddressCreateDTO {
    private Integer zipCode;
    private String state;
    private String country;
    private String landMark;
    private Integer houseNumber;
    private String address;

}
