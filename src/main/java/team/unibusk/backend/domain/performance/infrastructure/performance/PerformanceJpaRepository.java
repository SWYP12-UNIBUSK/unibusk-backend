package team.unibusk.backend.domain.performance.infrastructure.performance;

import org.springframework.data.jpa.repository.JpaRepository;
import team.unibusk.backend.domain.performance.domain.Performance;


public interface PerformanceJpaRepository extends JpaRepository<Performance, Long> {
}
