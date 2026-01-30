package com.fung.fungry.Repository;

import com.fung.fungry.Model.Order;
import com.fung.fungry.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order,Long> {

    Page<Order> findByUser(User user, Pageable pageable);
}
