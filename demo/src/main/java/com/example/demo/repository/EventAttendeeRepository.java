package com.example.demo.repository;

import com.example.demo.model.EventAttendee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventAttendeeRepository extends JpaRepository<EventAttendee, Long> {
    boolean existsByEventIdAndUserId(Long eventId, Long userId);
    Optional<EventAttendee> findByEventIdAndUserId(Long eventId, Long userId);
    List<EventAttendee> findByEventId(Long eventId);
    List<EventAttendee> findByUserId(Long userId);
    long countByEventId(Long eventId);
    void deleteByEventIdAndUserId(Long eventId, Long userId);
}
