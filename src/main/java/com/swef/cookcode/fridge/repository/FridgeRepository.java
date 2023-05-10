package com.swef.cookcode.fridge.repository;

import com.swef.cookcode.fridge.domain.Fridge;
import com.swef.cookcode.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface FridgeRepository extends JpaRepository<Fridge, Long> {
    Optional<Fridge> findByOwner(User user);

}
