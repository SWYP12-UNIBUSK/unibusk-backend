package team.unibusk.backend.domain.performance.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.unibusk.backend.domain.performance.domain.PerformanceImage;
import team.unibusk.backend.domain.performance.domain.PerformanceImageRepository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class PerformanceImageRepositoryImpl implements PerformanceImageRepository {

    private final PerformanceImageJpaRepository performanceImageJpaRepository;

    @Override
    public void deleteByPerformanceId(Long performanceId) {
        performanceImageJpaRepository.deleteByPerformanceId(performanceId);
    }

    @Override
    public PerformanceImage save(PerformanceImage performanceImage) {
        return performanceImageJpaRepository.save(performanceImage);
    }

    @Override
    public PerformanceImage findByPerformanceId(Long performanceId) {
        return performanceImageJpaRepository.findByPerformanceId(performanceId);
    }

    @Override
    public List<PerformanceImage> findByPerformanceIdIn(List<Long> performanceIds) {
        return performanceImageJpaRepository.findByPerformanceIdIn(performanceIds);
    }

}
