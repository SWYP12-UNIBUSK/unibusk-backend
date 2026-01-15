package team.unibusk.backend.domain.performance.infrastructure.performance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.unibusk.backend.domain.performance.domain.Performance;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface PerformanceJpaRepository extends JpaRepository<Performance, Long> {

    boolean existsByTitle(String title);

    // 특정 장소(location)에서 입력받은 시간대(startTime ~ endTime)와 겹치는 공연이 있는지 확인
    @Query("SELECT COUNT(p) > 0 FROM Performance p " +
            "WHERE p.performanceLocationId = :locationId " +
            "AND p.performanceDate = :date " +
            "AND ((p.startTime < :endTime AND p.endTime > :startTime))")
    boolean existsOverlappingPerformance(
            @Param("locationId") Long locationId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalDateTime startTime,
            @Param("endTime") LocalDateTime endTime
    );
}

