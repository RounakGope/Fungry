package com.fung.fungry.Repository;

import com.fung.fungry.Enums.UserRole;
import com.fung.fungry.Model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;   // ✅ Spring Data's Pageable
import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<User,Long> {

    Optional<User> findByUserMail(String userMail);
    Page<User> findByRole(UserRole role, Pageable pageable);

}
