package team.unibusk.backend.domain.performanceLocation.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

public interface PerformanceLocationRepository {

    //키워드로 검색
    public Page<PerformanceLocation> searchByKeyword(String keyword, Pageable pageable);

    List<PerformanceLocation> findByIds(Set<Long> locationIds);

    PerformanceLocation findById(Long id);

}
