package team.unibusk.backend.domain.performanceLocation.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PerformanceLocationRepository {

    //기본적인 전체 조회 페이징
    public Page<PerformanceLocation> findAll(Pageable pageable);
}
