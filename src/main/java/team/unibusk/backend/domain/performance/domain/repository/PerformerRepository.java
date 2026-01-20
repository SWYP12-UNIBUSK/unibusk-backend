package team.unibusk.backend.domain.performance.domain.repository;

import team.unibusk.backend.domain.performance.domain.Performer;

import java.util.List;
import java.util.Optional;

public interface PerformerRepository {
    Performer save(Performer performer);
    List<Performer> findByPerformanceId(Long performanceId);
    void deleteByPerformanceId(Long performanceId);
}
