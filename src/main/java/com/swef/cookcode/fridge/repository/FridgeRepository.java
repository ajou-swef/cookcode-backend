package com.swef.cookcode.fridge.repository;

import com.swef.cookcode.fridge.domain.Fridge;
import com.swef.cookcode.user.domain.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FridgeRepository extends JpaRepository<Fridge, Long> {
    Optional<Fridge> findByOwner(User user);

}
