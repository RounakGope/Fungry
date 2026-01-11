package com.fung.fungry.Model;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="users")
@Getter
@Setter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @Column(name = "user_Name", nullable = false)
    private String userName;
    @Column(name = "user_Password",nullable = false)//this is db level validation
    private String userPasswordHash;
    @Column(name = "role",nullable = false)
    private String role;
    @Column(name = "phone_Number",nullable = false)
    private Long phoneNumber;
    @OneToMany(
            mappedBy = "user",//mappedBy must match the field(variable ) name in the CHILD entity, not the table name.
           cascade = CascadeType.ALL
            ,fetch=FetchType.LAZY
    )
    private List<Address> addressList=new ArrayList<>();

    @OneToOne(mappedBy = "user",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private Cart cart;

    @OneToMany(
            mappedBy = "user",
            cascade = CascadeType.ALL
            ,fetch = FetchType.LAZY
    )
    private List<Order> orderHistory=new ArrayList<>();
    @CreationTimestamp
    LocalDateTime createdAt;
    @UpdateTimestamp
    LocalDateTime updatedAt;
}
