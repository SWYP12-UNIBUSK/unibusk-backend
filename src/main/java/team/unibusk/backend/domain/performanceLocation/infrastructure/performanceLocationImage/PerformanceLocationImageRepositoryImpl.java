package team.unibusk.backend.domain.performanceLocation.infrastructure.performanceLocationImage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationImage;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocationImageRepository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class PerformanceLocationImageRepositoryImpl implements PerformanceLocationImageRepository {

    private final PerformanceLocationImageJpaRepository performanceLocationImageJpaRepository;

    @Override
    public List<PerformanceLocationImage> findAllByPerformanceLocationId(Long performanceLocationId) {
        return performanceLocationImageJpaRepository.findAllByPerformanceLocationIdOrderBySortOrderAsc(performanceLocationId);
    }

    @Override
    public List<PerformanceLocationImage> findAllByPerformanceLocationIdIn(List<Long> performanceLocationIds) {
        if (performanceLocationIds == null || performanceLocationIds.isEmpty()) {
            return List.of();
        }
        return performanceLocationImageJpaRepository.findAllByPerformanceLocationIdIn(performanceLocationIds);
    }

}