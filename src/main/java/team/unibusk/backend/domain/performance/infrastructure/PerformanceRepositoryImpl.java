package team.unibusk.backend.domain.performance.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.unibusk.backend.domain.performance.domain.Performance;
import team.unibusk.backend.domain.performance.domain.PerformanceRepository;
import team.unibusk.backend.global.file.application.FileUploadService;

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
    public Optional<Performance> findById(Long id) {
        return performanceJpaRepository.findById(id);
    }

    @Override
    public void delete(Performance performance) {
        performanceJpaRepository.delete(performance);
    }
}
