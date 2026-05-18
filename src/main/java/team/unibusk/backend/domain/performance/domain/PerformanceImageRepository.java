package team.unibusk.backend.domain.performance.domain;

import java.util.List;

public interface PerformanceImageRepository {

    void deleteByPerformanceId(Long performanceId);

    PerformanceImage save(PerformanceImage performanceImage);

    PerformanceImage findByPerformanceId(Long performanceId);

    List<PerformanceImage> findByPerformanceIdIn(List<Long> performanceIds);

}
