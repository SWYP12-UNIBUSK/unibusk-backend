package team.unibusk.backend.domain.performance.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import team.unibusk.backend.domain.performance.domain.Performance;
import team.unibusk.backend.domain.performance.domain.PerformanceRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PerformanceRepositoryImpl implements PerformanceRepository {

    private final PerformanceJpaRepository performanceJpaRepository;

    @Override
    public Performance save(Performance performance) {
        return performanceJpaRepository.save(performance);
    }

    @Override
    public Page<Performance> findPastPerformances(LocalDateTime now, Pageable pageable) {
        return performanceJpaRepository.findPastPerformances(now, pageable);
    }

    @Override
    public Page<Performance> findUpcomingPerformances(LocalDateTime now, Pageable pageable) {
        return performanceJpaRepository.findUpcomingPerformances(now, pageable);
    }

    @Override
    public List<Performance> findUpcomingPreview(LocalDateTime now) {
        return performanceJpaRepository.findUpcomingPreview(now);
    }

    @Override
    public Optional<Performance> findDetailById(Long performanceId) {
        return performanceJpaRepository.findDetailById(performanceId);
    }

}
