package team.unibusk.backend.domain.performance.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.unibusk.backend.domain.performance.domain.Performance;

import java.time.LocalDateTime;
import java.util.List;

public interface PerformanceJpaRepository extends JpaRepository<Performance, Long> {

    @Query(
        value = """
            select distinct p
            from Performance p
            left join fetch p.images
            where p.endTime < :now
            order by p.startTime desc
        """,
        countQuery = """
            select count(p)
            from Performance p
            where p.endTime < :now
        """
    )
    Page<Performance> findPastPerformances(@Param("now") LocalDateTime now, Pageable pageable);

    @Query(
        value = """
            select distinct p
            from Performance p
            left join fetch p.images
            where p.endTime >= :now
            order by p.startTime asc
        """,
        countQuery = """
        select count(p)
        from Performance p
        where p.endTime >= :now
        """
    )
    Page<Performance> findUpcomingPerformances(@Param("now") LocalDateTime now, Pageable pageable);

    List<Performance> findTop8ByEndTimeGreaterThanEqualOrderByStartTimeAsc(LocalDateTime now);

}
