package team.unibusk.backend.domain.performanceLocation.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface PerformanceLocationRepository {

    //키워드로 검색
    Page<PerformanceLocation> searchByKeyword(String keyword, Pageable pageable);

    //공연 장소 등록
    PerformanceLocation save(PerformanceLocation performanceLocation);

    // 이름 일치하는 데이터가 있는지 확인
    Optional<PerformanceLocation> findByName(String name);

    List<PerformanceLocation> findByIds(Set<Long> locationIds);

    PerformanceLocation findById(Long id);

    List<PerformanceLocation> findInMapBounds(Double north, Double south, Double east, Double west);

    List<PerformanceLocation> searchByNameOrAddress(String keyword);

}
