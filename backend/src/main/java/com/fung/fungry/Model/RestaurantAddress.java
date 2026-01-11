package com.fung.fungry.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
public class RestaurantAddress {
    @Column(name = "street",nullable = false)
    private String street;
    @Column(name = "area",nullable = false)
    private String area;
    @Column(name = "city",nullable = false)
    private String city;
    @Column(name = "state",nullable = false)
    private String state;
    @Column(name = "zipcode",nullable = false)
    private Integer zipcode;

}
