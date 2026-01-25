package team.unibusk.backend.domain.performance.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.unibusk.backend.domain.performance.domain.Performance;

import java.time.LocalDateTime;
import java.util.List;

public interface PerformanceJpaRepository extends JpaRepository<Performance, Long> {

    @Query("""
        select distinct p
        from Performance p
        left join fetch p.images
        where p.endTime >= :now
        order by p.startTime asc
    """)
    List<Performance> findUpcomingPerformances(@Param("now") LocalDateTime now);

}
