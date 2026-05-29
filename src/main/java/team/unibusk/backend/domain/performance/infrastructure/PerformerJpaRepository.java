package team.unibusk.backend.domain.performance.infrastructure;

import org.springframework.data.jpa.repository.JpaRepository;
import team.unibusk.backend.domain.performance.domain.Performer;

import java.util.List;

public interface PerformerJpaRepository extends JpaRepository<Performer, Long> {

    void deleteByPerformanceId(Long performanceId);

    List<Performer> findByPerformanceId(Long performanceId);

}
