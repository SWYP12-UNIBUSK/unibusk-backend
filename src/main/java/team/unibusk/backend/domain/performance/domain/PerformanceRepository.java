package team.unibusk.backend.domain.performance.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface PerformanceRepository {

    Performance save(Performance perfomance);

    Page<Performance> findPastPerformances(LocalDateTime now, Pageable pageable);

    Page<Performance> findUpcomingPerformances(LocalDateTime now, Pageable pageable);

    List<Performance> findTop8ByEndTimeGreaterThanEqualOrderByStartTimeAsc(LocalDateTime now);

}
