package team.unibusk.backend.domain.performance.domain;

import java.util.Optional;

public interface PerformanceRepository {

    Performance save(Performance perfomance);

    Optional<Performance> findById(Long id);

    void delete(Performance performance);
}
