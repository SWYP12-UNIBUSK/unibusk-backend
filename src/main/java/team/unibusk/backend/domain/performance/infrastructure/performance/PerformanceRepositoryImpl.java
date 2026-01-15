package team.unibusk.backend.domain.performance.infrastructure.performance;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.unibusk.backend.domain.performance.domain.Performance;
import team.unibusk.backend.domain.performance.domain.PerformanceRepository;

@RequiredArgsConstructor
@Repository
public class PerformanceRepositoryImpl implements PerformanceRepository {

    private final PerformanceJpaRepository performanceJpaRepository;

    @Override
    public Performance save(Performance performance){
        return performanceJpaRepository.save(performance);
    }
}
