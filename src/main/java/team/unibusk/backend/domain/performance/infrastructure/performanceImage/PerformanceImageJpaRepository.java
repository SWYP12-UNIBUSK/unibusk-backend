package team.unibusk.backend.domain.performance.infrastructure.performanceImage;

import org.springframework.data.jpa.repository.JpaRepository;
import team.unibusk.backend.domain.performance.domain.PerformanceImage;

import java.util.List;

public interface PerformanceImageJpaRepository extends JpaRepository<PerformanceImage, Long> {
    List<PerformanceImage> findAllByPerformanceIdOrderBySortOrderAsc(Long performanceId);
}