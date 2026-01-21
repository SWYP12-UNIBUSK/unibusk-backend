package team.unibusk.backend.domain.performanceLocation.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PerformanceLocationRepository {

    //name으로 키워드 검색 (대소문자 구분 없음)
    Page<PerformanceLocation> findByNameContatinin(String name, Pageable pageable);

}
