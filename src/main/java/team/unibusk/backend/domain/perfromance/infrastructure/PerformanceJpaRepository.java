package team.unibusk.backend.domain.perfromance.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import team.unibusk.backend.domain.perfromance.domain.Performance;


public interface PerformanceJpaRepository extends JpaRepository<Performance, Long> {
}
