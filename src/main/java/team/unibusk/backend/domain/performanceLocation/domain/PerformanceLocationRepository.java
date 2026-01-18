package team.unibusk.backend.domain.performanceLocation.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PerformanceLocationRepository {

    //특정 키워드로 검색하여 페이징 반환
    Page<PerformanceLocation> findByKeywordContaining(String keyword, Pageable pageable);

    //name 으로 공연 장소 검색
    Optional<PerformanceLocation> findByName(String name);

}
