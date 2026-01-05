package com.example.demo.repository;

import com.example.demo.model.Event;
import com.example.demo.model.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByOrganizerId(Long organizerId);
    List<Event> findByStartDateAfterOrderByStartDateAsc(LocalDateTime date);
    List<Event> findByDepartmentOrderByStartDateAsc(String department);
    List<Event> findByTypeOrderByStartDateAsc(EventType type);

    @Query("SELECT e FROM Event e WHERE " +
            "(:department IS NULL OR e.department = :department) AND " +
            "(:type IS NULL OR e.type = :type) AND " +
            "(:startDate IS NULL OR e.startDate >= :startDate) AND " +
            "(:endDate IS NULL OR e.endDate <= :endDate) AND " +
            "(:search IS NULL OR LOWER(e.title) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "OR LOWER(e.description) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "ORDER BY e.startDate ASC")
    List<Event> findByFilter(@Param("department") String department,
                             @Param("type") EventType type,
                             @Param("startDate") LocalDateTime startDate,
                             @Param("endDate") LocalDateTime endDate,
                             @Param("search") String search);
}
