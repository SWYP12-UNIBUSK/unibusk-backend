package team.unibusk.backend.domain.performance.domain.repository;

import team.unibusk.backend.domain.performance.domain.Performance;

public interface PerformanceRepository {

    //Performance 저장
    Performance save(Performance performance);
}
