package com.parkzen.repository;

import com.parkzen.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByOwnerId(Long ownerId);
    List<Feedback> findByUserId(Long userId);
}
