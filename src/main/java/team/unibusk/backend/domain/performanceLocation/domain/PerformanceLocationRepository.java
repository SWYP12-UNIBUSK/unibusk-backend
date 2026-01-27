package team.unibusk.backend.domain.performanceLocation.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PerformanceLocationRepository {

    //키워드로 검색
    public Page<PerformanceLocation> searchByKeyword(String keyword, Pageable pageable);


}
