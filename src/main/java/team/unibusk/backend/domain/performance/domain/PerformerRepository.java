package team.unibusk.backend.domain.performance.domain;

import java.util.List;

public interface PerformerRepository {

    void deleteByPerformanceId(Long performanceId);

    List<Performer> saveAll(List<Performer> performers);

    List<Performer> findByPerformanceId(Long performanceId);

}
