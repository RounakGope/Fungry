package com.fung.fungry.Repository;

import com.fung.fungry.Model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository  extends JpaRepository <Cart ,Long> {
}
