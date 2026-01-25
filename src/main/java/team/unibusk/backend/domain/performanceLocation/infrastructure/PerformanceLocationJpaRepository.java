package team.unibusk.backend.domain.performanceLocation.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;

public interface PerformanceLocationJpaRepository extends JpaRepository<PerformanceLocation, Long> {
}
