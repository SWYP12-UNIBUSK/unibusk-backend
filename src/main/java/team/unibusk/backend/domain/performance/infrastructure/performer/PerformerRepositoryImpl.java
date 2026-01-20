package team.unibusk.backend.domain.performance.infrastructure.performer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.unibusk.backend.domain.performance.domain.Performer;
import team.unibusk.backend.domain.performance.domain.repository.PerformerRepository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PerformerRepositoryImpl implements PerformerRepository {

    private final PerformerJpaRepository jpaRepository;

    @Override
    public Performer save(Performer performer) {
        return jpaRepository.save(performer);
    }

    @Override
    public List<Performer> findByPerformanceId(Long performanceId) {
        return jpaRepository.findByPerformanceId(performanceId);
    }

    @Override
    public void deleteByPerformanceId(Long performanceId) {
        jpaRepository.deleteByPerformanceId(performanceId);
    }
}
