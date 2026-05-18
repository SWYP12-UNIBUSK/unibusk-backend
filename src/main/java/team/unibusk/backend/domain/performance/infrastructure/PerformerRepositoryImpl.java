package team.unibusk.backend.domain.performance.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import team.unibusk.backend.domain.performance.domain.Performer;
import team.unibusk.backend.domain.performance.domain.PerformerRepository;

import java.util.List;

@RequiredArgsConstructor
@Repository
public class PerformerRepositoryImpl implements PerformerRepository {

    private final PerformerJpaRepository performerJpaRepository;

    @Override
    public void deleteByPerformanceId(Long performanceId) {
        performerJpaRepository.deleteByPerformanceId(performanceId);
    }

    @Override
    public List<Performer> saveAll(List<Performer> performers) {
        return performerJpaRepository.saveAll(performers);
    }

    @Override
    public List<Performer> findByPerformanceId(Long performanceId) {
        return performerJpaRepository.findByPerformanceId(performanceId);
    }

}
