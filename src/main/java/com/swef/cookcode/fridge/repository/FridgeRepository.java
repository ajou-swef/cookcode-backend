package com.swef.cookcode.fridge.repository;

import com.swef.cookcode.fridge.domain.Fridge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FridgeRepository extends JpaRepository<Fridge, Long> {
}
