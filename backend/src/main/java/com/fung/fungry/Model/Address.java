package com.fung.fungry.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "addresses")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long addressId;

    @Column(name = "zipcode",nullable = false)
    private Integer zipcode;

    @Column(name = "house_number")
    private int houseNumber;
    @Column(name = "landmark")
    private String landmark;
    @Column(name = "address")
    private String address;

    @OneToMany( mappedBy = "address",fetch = FetchType.LAZY)
    private List<Order> orders=new ArrayList<>();


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
}
