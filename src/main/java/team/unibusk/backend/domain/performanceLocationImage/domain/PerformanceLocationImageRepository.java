package team.unibusk.backend.domain.performanceLocationImage.domain;

import java.util.List;

public interface PerformanceLocationImageRepository {

    List<PerformanceLocationImage> findAllByPerformanceLocationIdIn(List<Long> performanceLocationIds);

}