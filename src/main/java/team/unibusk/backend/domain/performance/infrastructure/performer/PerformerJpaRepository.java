package team.unibusk.backend.domain.performance.infrastructure.performer;

import org.springframework.data.jpa.repository.JpaRepository;
import team.unibusk.backend.domain.performance.domain.Performer;

import java.util.Optional;

public interface PerformerJpaRepository extends JpaRepository<Performer, Long> {
    Optional<Performer> findByPerformanceId(Long performanceId);
    void deleteByPerformanceId(Long performanceId);
}
