package team.unibusk.backend.domain.performanceLocation.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;


import java.util.List;

public interface PerformanceLocationJpaRepository extends JpaRepository<PerformanceLocation, Long> {

    //name으로 키워드 검색 (대소문자 구분 없음)
    Page<PerformanceLocation> findByNameContainingIgnoreCase(String name, Pageable pageable);


}
