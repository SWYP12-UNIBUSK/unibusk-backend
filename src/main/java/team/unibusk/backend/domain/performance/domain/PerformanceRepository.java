package team.unibusk.backend.domain.performance.domain;

import java.time.LocalDateTime;
import java.util.List;

public interface PerformanceRepository {

    Performance save(Performance perfomance);

    List<Performance> findUpcomingPerformances(LocalDateTime now);

    List<Performance> findTop8ByEndTimeGreaterThanEqualOrderByStartTimeAsc(LocalDateTime now);

}
