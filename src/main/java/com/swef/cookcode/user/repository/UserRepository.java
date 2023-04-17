package com.swef.cookcode.user.repository;

import com.swef.cookcode.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
