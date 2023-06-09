package com.swef.cookcode.user.repository;

import com.swef.cookcode.user.domain.Subscribe;
import com.swef.cookcode.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SubscribeRepository extends JpaRepository<Subscribe, Long> {

    @Query("select s from Subscribe s join fetch s.subscriber where s.publisher = :user")
    List<Subscribe> findSubscribers(User user);

    @Query("select s from Subscribe s join fetch s.publisher where s.subscriber = :user")
    List<Subscribe> findPublishers(User user);

    Optional<Subscribe> findBySubscriberAndPublisher(User subscriber, User publisher);

}
