package com.swef.cookcode.recipe.repository;

import com.swef.cookcode.recipe.domain.RecipeComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecipeCommentRepository extends JpaRepository<RecipeComment, Long> {
}
