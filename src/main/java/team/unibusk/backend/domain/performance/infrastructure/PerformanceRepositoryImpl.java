package team.unibusk.backend.domain.performance.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.unibusk.backend.domain.performance.domain.Performance;
import team.unibusk.backend.domain.performance.domain.PerformanceRepository;

@Repository
@RequiredArgsConstructor
public class PerformanceRepositoryImpl implements PerformanceRepository {

    private final PerformanceJpaRepository jpaRepository;

    @Override
    public Performance save(Performance performance){
        return jpaRepository.save(performance);
    }


}
