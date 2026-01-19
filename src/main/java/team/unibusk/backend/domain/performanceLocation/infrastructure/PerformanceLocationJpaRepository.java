package team.unibusk.backend.domain.performanceLocation.infrastructure;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import team.unibusk.backend.domain.performanceLocation.domain.PerformanceLocation;

import java.util.Optional;


public interface PerformanceLocationJpaRepository extends JpaRepository<PerformanceLocation, Long> {

    // 단건 조회 시에는 Fetch Join으로 한 번에 가져오기
    @Query("select pl from PerformanceLocation pl join fetch pl.images where pl.name = :name")
    Optional<PerformanceLocation> findByNameWithImages(@Param("name") String name);

    // 키워드 검색은 Batch Size 설정을 활용하기 위해 기본 쿼리 유지
    Page<PerformanceLocation> findByNameContaining(String keyword, Pageable pageable);

}
