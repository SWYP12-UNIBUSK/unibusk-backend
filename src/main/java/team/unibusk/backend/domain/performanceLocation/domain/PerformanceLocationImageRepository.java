package team.unibusk.backend.domain.performanceLocation.domain;

import java.util.List;

public interface PerformanceLocationImageRepository {
    List<PerformanceLocationImage> findAllByPerformanceLocationId(Long performanceLocationId);
    List<PerformanceLocationImage> findAllByPerformanceLocationIdIn(List<Long> performanceLocationIds);

}