package com.swef.cookcode.recipe.repository;

import com.swef.cookcode.recipe.domain.Step;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StepRepository extends JpaRepository<Step, Long> {
}
