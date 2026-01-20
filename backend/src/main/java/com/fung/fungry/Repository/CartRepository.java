package com.fung.fungry.Repository;

import com.fung.fungry.Model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartRepository  extends JpaRepository <Cart ,Long> {
    Optional<Cart> findByUser_UserId(Long userId);
}
