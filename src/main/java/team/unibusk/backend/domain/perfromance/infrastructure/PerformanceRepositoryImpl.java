package team.unibusk.backend.domain.perfromance.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.unibusk.backend.domain.perfromance.domain.Performance;
import team.unibusk.backend.domain.perfromance.domain.PerformanceRepository;

import java.util.Optional;
@Repository
@RequiredArgsConstructor
public class PerformanceRepositoryImpl implements PerformanceRepository {

    private final PerformanceJpaRepository jpaRepository;

    @Override
    public Performance save(Performance performance){
        return jpaRepository.save(performance);
    }


}
