package team.unibusk.backend.domain.performanceLocation.infrastructure.performanceLocationImage;

import org.springframework.data.jpa.repository.JpaRepository;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationImage;

import java.util.List;

public interface PerformanceLocationImageJpaRepository extends JpaRepository<PerformanceLocationImage, Long> {
    // 단건 조회 및 정렬
    List<PerformanceLocationImage> findAllByPerformanceLocationIdOrderBySortOrderAsc(Long performanceLocationId);

    // N+1 방지용 Batch 조회
    List<PerformanceLocationImage> findAllByPerformanceLocationIdIn(List<Long> performanceLocationIds);
}