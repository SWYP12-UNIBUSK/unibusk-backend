package team.unibusk.backend.domain.performance.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import team.unibusk.backend.domain.performance.domain.PerformanceImage;

import java.util.List;

public interface PerformanceImageJpaRepository extends JpaRepository<PerformanceImage, Long> {

    void deleteByPerformanceId(Long performanceId);

    PerformanceImage findByPerformanceId(Long performanceId);

    List<PerformanceImage> findByPerformanceIdIn(List<Long> performanceIds);

}
